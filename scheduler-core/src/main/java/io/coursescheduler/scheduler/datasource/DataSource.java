/**
  * @(#)DataSource.java
  *
  * Base class for data source implementations to access data for processing
  *
  * @author Mike Reinhold
  * 
  * @license GNU General Public License version 3 (GPLv3)
  *
  * This file is part of Course Scheduler, an open source, cross platform
  * course scheduling tool, configurable for most universities.
  *
  * Copyright (C) 2010-2013 Mike Reinhold
  *
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  * 
  */
package io.coursescheduler.scheduler.datasource;

import io.coursescheduler.util.variable.StrSubstitutorFactory;
import io.coursescheduler.util.variable.SubstitutionVariableSource;
import io.coursescheduler.util.variable.preferences.PreferencesBasedVariableFactory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.prefs.Preferences;

import org.apache.commons.io.input.TeeInputStream;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Base class for data source implementations to access data for processing
 *
 * @author Mike Reinhold
 *
 */
public abstract class DataSource extends RecursiveAction {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Preferences node property for indicating that the DataSource should create a local copy
	 * of the data source. The location of the data source copy can be specified in the property
	 * referenced by the {@link #DISK_COPY_FILE_PROPERTY} preferences property. If no value is
	 * specified, the default copy file (specified in {@link #DISK_COPY_DEFAULT_FILE}) will be used.
	 * 
	 * Value: {@value}
	 */
	public static final String DISK_COPY_PROPRTY = "copy.enabled";
	
	/**
	 * Preferences node property for indicating the location and name of the disk based buffer.
	 * 
	 * Value: {@value}
	 */
	public static final String DISK_COPY_FILE_PROPERTY = "copy.file";
	
	/**
	 * Default copy file location if a local copy is requested (as specified in {@link #DISK_COPY_PROPRTY}
	 * and not explicitly specified in {@link #DISK_COPY_FILE_PROPERTY}.
	 * 
	 * Value: {@value}
	 */
	public static final String DISK_COPY_DEFAULT_FILE = "${dir.tmp}/${random.string}";
	
	/**
	 * Preferences node property for indicating the size of the in memory buffer that should be used
	 * for the data source (if disk based buffering is not enabled via the {@link #DISK_BUFFER_PROPERTY} preferences
	 * property. The default value used by the DataSource (if not specified in the Preferences node), is
	 * specified by {@link #PIPE_BUFFER_DEFAULT_SIZE}.
	 * 
	 * Value: {@value}
	 */
	public static final String PIPE_BUFFER_SIZE_PROPERTY = "buffer.size";
	
	/**
	 * Default size of the Memory based buffer if in use and not specified explicitly in the Preferences node
	 * via the {@link #PIPE_BUFFER_SIZE_PROPERTY}.
	 * 
	 * Value: {@value} bytes
	 */
	public static final int PIPE_BUFFER_DEFAULT_SIZE = 1024000;
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Preferences node containing the data source configuration
	 */
	private Preferences settings;
	
	/**
	 * Variable source for the local replacements
	 */
	private SubstitutionVariableSource replacements;

	/**
	 * String substitutor that will perform variable replacement in configuration elements 
	 */
	private StrSubstitutor replacer;
	
	/**
	 * The substitutor factory which can be used to create new StrSubstitutor instances
	 */
	private StrSubstitutorFactory substitutorFactory;
	
	/**
	 * Factory instance for creating Preferences based variable sources
	 */
	private PreferencesBasedVariableFactory prefSourceFactory;
	
	/**
	 * Input stream that will be used as the Data Source
	 */
	private volatile InputStream dataSource;

	/**
	 * Create a new DataSource using the specified Preferences node and map of placeholders
	 * and replacement values
	 * @param substitutionFactory factory instance for creating StrSubstitution instances
	 * @param prefSourceFactory factory instance for creating PreferencesBasedVariableSource instances
	 * @param settings the Preferences node containing the configuration for the data source access
	 * @param replacements variable source for local substitution variables
	 */
	@AssistedInject
	public DataSource(StrSubstitutorFactory substitutionFactory, PreferencesBasedVariableFactory prefSourceFactory, @Assisted("config") Preferences settings, @Assisted("localVars") SubstitutionVariableSource replacements) {
		super();
		
		this.settings = settings;
		this.substitutorFactory = substitutionFactory;
		this.prefSourceFactory = prefSourceFactory;
		this.replacements = replacements;
		
		setReplacerPreferencesNode(settings);
	}
	
	/**
	 * Set the Data Source input stream that the subsequent tasks can use to access the data
	 * from the source
	 * 
	 * @param dataSource the dataSource to set
	 */
	protected void setDataSource(InputStream dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Retrieve the current DataSource InputStream that subsequent tasks will use to access the data
	 * 
	 * @return the dataSource the data source input stream
	 */
	protected InputStream getDataSource() {
		return dataSource;
	}

	/**
	 * Return the input stream that is a result of processing the data source.
	 * 
	 * Calling this method prior to execution of the DataSource may result in invalid results.
	 * A data source does not need to have completed processing in order to have a valid
	 * DataSource InputStream. Many data sources will prepare the input stream and then allow 
	 * the input stream to be accessed by the subsequent tasks  even before the data source has
	 * processed the stream in full. Since the preparation of the DataSource InputStream is asynchronous
	 * from the calling and subsequent tasks, the calling class should wait until the data source
	 * is ready by checking the {@link #isDataSourceInputStreamReady()} method.
	 * 
	 *
	 * @return the input stream resulting from processing the input stream
	 * @throws IOException if there is an error performing IO on the data source
	 */
	public InputStream getDataSourceAsInputStream() throws IOException{
		return dataSource;
	}
	
	/**
	 * Return if the DataSource InputStream is ready for processing by subsequent tasks. 
	 * 
	 * Calling classes should confirm that the data source is ready to use by calling this
	 * method before calling the {@link #getDataSourceAsInputStream()} method.
	 *
	 * @return if the DataSource InputStream is ready for subsequent processing
	 */
	public boolean isDataSourceInputStreamReady() {
		return dataSource != null;
	}
	
	/**
	 * Update the preferences node used by the current string substituter instance
	 *
	 * @param node the new preferences node for variable reference
	 */
	protected void setReplacerPreferencesNode(Preferences node) {
		Set<StrLookup<String>> sources = new HashSet<>();
		log.debug("Creating Preferences Variable Source from preferences node {}", node);
		sources.add(prefSourceFactory.createPreferencesVariableSource(node));
		log.debug("Using local Variable Source", replacements);
		sources.add(replacements);
		
		this.replacer = substitutorFactory.createSubstitutor(sources);
	}
	
	/**
	 * Retrieve the current Map of variable names and values used by the replacer. 
	 * Warning: modifications to htis map will be reflected by the string substitutor
	 * 
	 * @return the map of substitutions
	 */
	protected SubstitutionVariableSource getReplacements() {
		return replacements;
	}
	
	/**
	 * Perform the string replacements and return the resultant value
	 *
	 * @param input the string upon which to perform the replacements
	 * @return the updated string value
	 */
	protected String performReplacements(String input) {
		String value = input;
		log.trace("Performing placeholder substitution on string: {}", input);
		value = replacer.replace(value);		
		log.debug("Substituted string is: {}", value);
		return value;
	}
	
	/**
	 * Return the Preferences node corresponding to the DataSource configuration
	 *
	 * @return the configuration node
	 */
	protected Preferences getSettings() {
		return settings;
	}
	
	/**
	 * Create a TeeInputStream that transparently copies output to a local file copy, if the 
	 * data source is configured to use a local file copy (see {@link #DISK_COPY_PROPRTY}).
	 *
	 * @param source the data source input stream
	 * @return the input stream of the Tee or the source, if a file copy is not requested or the
	 * file copy output stream creation failed
	 */
	protected InputStream tee(InputStream source) {
		log.debug("Checking if a local copy is requested");
		if(getSettings().getBoolean(DISK_COPY_PROPRTY, false)) {
			try {
				log.info("Preparing to create local copy");
		        String tempFile = performReplacements(getSettings().get(DISK_COPY_FILE_PROPERTY, DISK_COPY_DEFAULT_FILE));
		        log.debug("Writing input stream to temporary file: {}", tempFile);
				
		        FileOutputStream copy = new FileOutputStream(tempFile);
				TeeInputStream tee = new TeeInputStream(source, copy);
		        log.debug("Local copy stream {} created using output stream {} and source stream {}", new Object[] {tee, copy, source});
				
				return tee;
			} catch (IOException e) {
				log.error("Unable to create tee to file output stream for input stearm {}, returning original input without redirection", source);
				return source;
			}
		} else {
			log.info("No local copy requested for this data source");
			return source;
		}
	}
	
	/**
	 * Build the data sink (InputStream) for a Pipe to transfer data from one
	 * stream to another. This is used to buffer and transfer data between 
	 * threads.
	 *
	 * @return the Pipe sink InputStream for retrieving data from the Pipe
	 * @throws IOException if there is a problem creating the Piped streams
	 */
	protected PipedInputStream createPipeSink(PipedOutputStream source) throws IOException{
		log.debug("Preparing to build pipe sink input stream for pipe source output stream {}", source);		
		PipedInputStream sink = new PipedInputStream(source, getSettings().getInt(PIPE_BUFFER_SIZE_PROPERTY, PIPE_BUFFER_DEFAULT_SIZE));
		log.debug("Finished building pipe sink input stream {}", sink);
		
		return sink;
	}
	
	/**
	 * Build the data source (OutputStream) for a Pipe to transfer data from
	 * one stream to another. This is used to buffer and transfer data between
	 * threads.
	 *
	 * @return the Pipe source OutputStream for inputting data into the Pipe
	 */
	protected PipedOutputStream createPipeSource() {
		log.debug("Preparing to build pipe source output stream");
		PipedOutputStream source = new PipedOutputStream();
		log.debug("Finished building pipe source output stream {}", source);
		
		return source;
	}
	
	/**
	 * Transfer data from the source InputStream to the OutputStream of the Pipe. 
	 * 
	 * @param source the input stream supplying data to the Pipe
	 * @param pipe the output stream of the pipe that will receive the data
	 *
	 * @throws IOException if there is an issue transferring data to the pipe
	 */
	protected void transferDataToPipe(InputStream source, PipedOutputStream pipe) throws IOException {
		log.debug("Preparing to transfer data from InputStream {} to PipedOutputStream {}", source, pipe);
		long start = System.currentTimeMillis();
		String charset = settings.get(DataSourceConstants.INPUT_CHARSET_PROPERTY, Charset.defaultCharset().name());
		log.debug("Processing data streams as {}", charset);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(source, Charset.forName(charset)));
		
		try(OutputStreamWriter writer = new OutputStreamWriter(pipe, Charset.forName(charset))) {
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				writer.write(line + "\n");
			}
		} catch (IOException e) {
			log.error("Exception transferring data from source stream to the pipe", e);
		}
		
		long end = System.currentTimeMillis();
		log.debug("Finished transferring data from InputStream {} to PipedOutputStream {} in {} ms", new Object[] { source, pipe, end - start});
	}
}
