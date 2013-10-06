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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.coursescheduler.scheduler.datasource.DataSource;
import io.coursescheduler.scheduler.datasource.DataSourceConstants;
import io.coursescheduler.util.text.StrSubstitutorFactory;

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
	 * Preferences property for specifying if a copy of the file should be saved
	 * in the temp directory of the application
	 * 
	 * Value: {@value}
	 */
	public static final String FILE_COPY_PROPERTY = "file-save-copy";
	
	/**
	 * Preferences property for specifying the name of the temp copy of the data
	 * source file (with possible placeholder replacement). 
	 * 
	 * Value: {@value}
	 */
	public static final String FILE_COPY_NAME_PROPERTY = "file-copy-path-template";
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * File that will be used as the Data Source
	 */
	private File file;
	
	/**
	 * Create a new FileDataSource using the specified Preferences node and map of placeholders
	 * and replacement values
	 * @param substitutionFactory factory instance for creating StrSubstitution instances
	 * @param settings the Preferences node containing the configuration for the File access
	 * @param replacements map of substitution placeholders to values
	 */
	@AssistedInject
	public FileDataSource(StrSubstitutorFactory substitutionFactory, @Assisted("config") Preferences settings, @Assisted("localVars") Map<String, String> replacements) {
		super(substitutionFactory, settings, replacements);
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.datasource.DataSource#getDataSourceAsInputStream()
	 */
	@Override
	public InputStream getDataSourceAsInputStream() throws IOException {
		FileInputStream input;
		
		try {
			long start = System.currentTimeMillis();
			log.info("Retrieving input stream for file {}", file);
			input = new FileInputStream(file);
			long end = System.currentTimeMillis();
			log.info("Input stream for file {} acquired in {} ms. {} bytes available without blocking IO", new Object[] {
				file, 
				end - start, 
				input.available()
			});
		} catch (FileNotFoundException e) {
			log.error("Exception while retrieving input stream for data source", e);
			throw e;
		}
		
		return input;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		log.info("Computing data source");
		Preferences config = getSettings().node(DataSourceConstants.GENERAL_SETTINGS_NODE);
		File source = null;
		
		try {
			log.info("Checking source file URI");
			String fileURITemplate = config.get(FILE_URI_PROPERTY, null);
			log.debug("Source file URI template: {}", fileURITemplate);
			
			String fileURI = performReplacements(fileURITemplate);
			log.debug("Source file URI result: {}", fileURI);
		
			source = new File(new URI(fileURI));
		} catch (NullPointerException | URISyntaxException | IllegalArgumentException e) {
			log.error("Unable to access file due to invalid file URI", e);
			
			log.info("Checking alternate source file path");
			String filePathTemplate = config.get(FILE_PATH_PROPERTY, null);
			log.debug("Source file path template: {}", filePathTemplate);
			
			String filePath = performReplacements(filePathTemplate);
			log.debug("Source file path result: {}", filePath);
		
			source = new File(filePath);
		}
		log.debug("Source file is {}", source);
		
		if(config.getBoolean(FILE_COPY_PROPERTY, false)) {
			String targetFileTemplate = config.get(FILE_COPY_NAME_PROPERTY, null);
			log.debug("Temp target file template: {}", targetFileTemplate);
			
			String targetFile = performReplacements(targetFileTemplate);
			log.debug("Temp target file: {}", targetFile);

			try {
				File target = new File(targetFile);
				log.debug("Temp target file is {}", target);
			
				log.info("Temp copy requested, preparing to copy {} to {}", source, target);
				Files.copy(source, target);
				file = target;
			} catch (NullPointerException | IOException e) {
				log.error("Exception copying data source file to temporary file", e);
				
				log.debug("Using source without temp copy");
				file = source;
			}
		}else {
			log.info("Temp copy not requested");
			file = source;
		}

		log.info("Using file {} as data source", file);
	}
	
}
