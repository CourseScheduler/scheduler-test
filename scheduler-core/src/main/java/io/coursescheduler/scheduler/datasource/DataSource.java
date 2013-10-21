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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.prefs.Preferences;

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
	 * Preferences node property for indicating whether a disk based buffer should be used for the
	 * data source. If true, a copy of the input will be saved in ${dir.tmp}.
	 * 
	 * If false, data source specific buffer requirements may require in memory buffering, regardless
	 * of the value of {@link #MEMORY_BUFFER_PROPERTY}
	 * 
	 * Value: {@value}
	 */
	public static final String DISK_BUFFER_PROPERTY = "buffer.disk";
	
	/**
	 * Preferences node property for indicating the location and name of the disk based buffer.
	 * 
	 * Value: {@value}
	 */
	public static final String DISK_BUFFER_FILE_PROPERTY = "buffer.file";
	
	/**
	 * Default buffer file location if disk based buffering is in use and not explicitly specified in
	 * {@link #DISK_BUFFER_FILE_PROPERTY}.
	 * 
	 * Value: {@value}
	 */
	public static final String DISK_BUFFER_DEFAULT_FILE = "${dir.tmp}/${random.string}";
	
	/**
	 * Preferences node property for indicating whether a memory based buffer should be used for the
	 * data source. If true, the data source will attempt to buffer the stream in memory.
	 */
	public static final String MEMORY_BUFFER_PROPERTY = "buffer.memory";
	
	/**
	 * Preferences node property for indicating the size of the in memory buffer that should be used
	 * for the data source (if disk based buffering is not enabled via the {@link #DISK_BUFFER_PROPERTY} preferences
	 * property. The default value used by the DataSource (if not specified in the Preferences node), is
	 * specified by {@link #MEMORY_BUFFER_DEFAULT_SIZE}.
	 * 
	 * Value: {@value}
	 */
	public static final String MEMORY_BUFFER_SIZE_PROPERTY = "buffer.size";
	
	/**
	 * Default size of the Memory based buffer if in use and not specified explicitly in the Preferences node
	 * via the {@link #MEMORY_BUFFER_SIZE_PROPERTY}.
	 * 
	 * Value: {@value} bytes
	 */
	public static final int MEMORY_BUFFER_DEFAULT_SIZE = 1024000;
	
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
	 * Return the input stream that is a result of processing the data source.
	 * 
	 * Calling this method prior to execution of the DataSource may result in invalid results.
	 *
	 * @return the input stream resulting from processing the input stream
	 * @throws IOException if there is an error performing IO on the data source
	 */
	public abstract InputStream getDataSourceAsInputStream() throws IOException;
	
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
	 * Return if the data source is configured for disk buffering
	 *
	 * @return if disk buffering is configured
	 */
	protected boolean diskBufferRequested() {
		return getSettings().getBoolean(DISK_BUFFER_PROPERTY, false);
	}
	
	/**
	 * Return if the data source is configured for memory buffering
	 *
	 * @return if memory buffering is configured
	 */
	protected boolean memoryBufferRequested() {
		return getSettings().getBoolean(MEMORY_BUFFER_PROPERTY, false);
	}
	
	/**
	 * Buffer the specified input stream to disk or in memory based on configuration. If no
	 * buffering has been configured for this data source, the original input stream will
	 * be returned
	 *
	 * @param stream the input stream to buffer
	 * @return the buffered input stream, if buffering is configured. Otherwise, the input stream
	 * @throws IOException if there is an issue performing the buffer operation
	 */
	protected InputStream buffer(InputStream stream) throws IOException {
		log.debug("Checking buffer configuration");
		if(diskBufferRequested()) {
			log.debug("Disk based buffer requested");
			return bufferOnDisk(stream);
		} else if(memoryBufferRequested()) {
			log.debug("Memory based buffer requested");
			return bufferInMemory(stream);
		}
		log.debug("No buffering configured");
		return stream;
	}
	
	/**
	 * Buffer the specified input stream to a file on disk and then return an input stream
	 * that references the buffered stream
	 *
	 * @param stream the input stream to buffer on disk
	 * @return the buffered input stream
	 * @throws IOException if the input stream cannot be buffered on disk
	 */
	protected InputStream bufferOnDisk(InputStream stream) throws IOException {
		log.debug("Preparing to buffer input stream on disk");
		long start = System.currentTimeMillis();
		
		String tempFile = performReplacements(getSettings().get(DISK_BUFFER_FILE_PROPERTY, DISK_BUFFER_DEFAULT_FILE));
		log.debug("Writing input stream to temporary file: {}", tempFile);
		
		try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile))){
			BufferedReader contentReader = new BufferedReader(new InputStreamReader(stream));
			
			log.debug("Copying input stream to temporary file {}", tempFile);
			copyStream(contentReader, writer);
			log.debug("Finished copying input to temporary file {}", tempFile);
		} catch (IOException e) {
			log.error("Exception copying input stream to temporary file {}", tempFile);
			throw e;
		}
		
		InputStream input = new FileInputStream(tempFile);  
		long end = System.currentTimeMillis();		
		log.debug("Finished preparing local disk copy in {} ms", end - start);
		
		return input;
	}
	
	/**
	 * Buffer the specified input stream in memory and then return an input stream that
	 * references the buffered stream
	 *
	 * @param stream the input stream to buffer in memory
	 * @return the buffered input stream
	 * @throws IOException if the input stream cannot be buffered in memory
	 */
	protected InputStream bufferInMemory(InputStream stream) throws IOException {
		log.debug("Preparing to buffer input stream in memory");
		long start = System.currentTimeMillis();
		
		PipedInputStream input = new PipedInputStream(getSettings().getInt(MEMORY_BUFFER_SIZE_PROPERTY, MEMORY_BUFFER_DEFAULT_SIZE));
		try(OutputStreamWriter output = new OutputStreamWriter(new PipedOutputStream(input))){
			BufferedReader contentReader = new BufferedReader(new InputStreamReader(stream));
			
			log.debug("Copying input stream to memory buffer");
			copyStream(contentReader, output);
			log.debug("Finished copying input to memory buffer");
		} catch (IOException e) {
			log.error("Exception copying input stream to memory buffer");
			throw e;
		}
		
		long end = System.currentTimeMillis();
		log.debug("Finished preparing in memory copy in {} ms", end - start);
		
		return input;
	}
	
	/**
	 * Copy the lines from the specified BufferedReader to the specified OutputStreamWriter
	 *
	 * @param reader a Reader for the input stream to copy
	 * @param writer a Writer for the output stream target
	 * @throws IOException if there is an issue reading or writing the streams during copy
	 */
	private void copyStream(BufferedReader reader, OutputStreamWriter writer) throws IOException {
		String line;
		while((line = reader.readLine()) != null) {
			writer.write(line);
			writer.write("\n");
		}
		writer.close();
	}
}
