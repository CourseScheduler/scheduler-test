/**
  * @(#)FileDataSource.java
  *
  * Implement a File based data source
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
package io.coursescheduler.scheduler.datasource.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.coursescheduler.scheduler.datasource.DataSource;
import io.coursescheduler.util.variable.StrSubstitutorFactory;
import io.coursescheduler.util.variable.SubstitutionVariableSource;
import io.coursescheduler.util.variable.preferences.PreferencesBasedVariableFactory;

/**
 * Implement a File based data source
 *
 * @author Mike Reinhold
 *
 */
public class FileDataSource extends DataSource {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Preferences property for specifying the location of the file. Either
	 * this property or the {@link #FILE_PATH_PROPERTY} must be set. The URI
	 * property is checked first.
	 * 
	 * Value: {@value}
	 */
	public static final String FILE_URI_PROPERTY = "file-uri-template";

	/**
	 * Preferences property for specifying the location of the file. Either
	 * this property or the {@link #FILE_URI_PROPERTY} must be set. The URI
	 * property is checked first.
	 * 
	 * Value: {@value}
	 */
	public static final String FILE_PATH_PROPERTY = "file-path-template";
	
	/**
	 * Component based logger
	 */
	private transient Logger log = LoggerFactory.getLogger(getClass().getName());
		
	/**
	 * Create a new FileDataSource using the specified Preferences node and map of placeholders
	 * and replacement values
	 * @param substitutionFactory factory instance for creating StrSubstitution instances
	 * @param prefSourceFactory factory instance for creating PreferencesBasedVariableSource instances
	 * @param settings the Preferences node containing the configuration for the File access
	 * @param replacements variable source for local variables
	 */
	@AssistedInject
	public FileDataSource(StrSubstitutorFactory substitutionFactory, PreferencesBasedVariableFactory prefSourceFactory, @Assisted("config") Preferences settings, @Assisted("localVars") SubstitutionVariableSource replacements) {
		super(substitutionFactory, prefSourceFactory, settings, replacements);
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		log.info("Computing data source for file based source");
		long start = System.currentTimeMillis();
		File source = getSourceFile();
		log.debug("Source file is {}", source);

		log.info("Retrieving input stream for file {}", source);
		try {
			//Access the file
			FileInputStream stream = new FileInputStream(source);
			
			//Tee the input if configured
			InputStream teedSource = tee(stream);
			
			//Prepare the cross thread pipe
			PipedOutputStream bufferSink = createPipeSource();
			PipedInputStream bufferedStream = createPipeSink(bufferSink);

			//Set the data source stream
			setDataSource(bufferedStream);
			long end = System.currentTimeMillis();
			log.info("Input stream for file {} acquired in {} ms. {} bytes available without blocking IO", new Object[] {
				source, end - start, getDataSource().available()
			});
			
			log.info("Preparing to pipe data source");
			start = System.currentTimeMillis();
			transferDataToPipe(teedSource, bufferSink);
			end = System.currentTimeMillis();
			log.info("Finished piping data source in {} ms", end - start);
		} catch (IOException e) {
			log.error("Exception while accessing data source " + source, e);
		}
	}
	
	/**
	 * Access the File DataSource based on the URI or the alternate source path
	 *
	 * @return the File reference for the data source
	 */
	private File getSourceFile() {
		try {
			return getURISourceFile();
		} catch (NullPointerException | URISyntaxException | IllegalArgumentException e) {
			log.error("Unable to access file due to invalid file URI", e);
			return getLocalSourceFile();
		}
	}
	
	/**
	 * Access the File DataSource based on the URI
	 *
	 * @return the File reference for the data source
	 * @throws URISyntaxException if there is an issue accessing the URI
	 */
	private File getURISourceFile() throws URISyntaxException {
		log.info("Checking source file URI");
		String fileURITemplate = getSettings().get(FILE_URI_PROPERTY, null);
		log.debug("Source file URI template: {}", fileURITemplate);
		
		String fileURI = performReplacements(fileURITemplate);
		log.debug("Source file URI result: {}", fileURI);
	
		return new File(new URI(fileURI));
	}
	
	/**
	 * Access the File DataSource based on the alternate source path
	 *
	 * @return the File reference for the data source
	 */
	private File getLocalSourceFile() {		
		log.info("Checking alternate source file path");
		String filePathTemplate = getSettings().get(FILE_PATH_PROPERTY, null);
		log.debug("Source file path template: {}", filePathTemplate);
		
		String filePath = performReplacements(filePathTemplate);
		log.debug("Source file path result: {}", filePath);
	
		return new File(filePath);
	}
}
