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

import io.coursescheduler.scheduler.datasource.DataSource;

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
	 * Preferences property for specifying the location of the file. This
	 * property is required by the FileDataSource to refer to valid file 
	 * (after placeholder replacement).
	 * 
	 * Value: {@value}
	 */
	public static final String FILE_URI_PROPERTY = "file-uri";
	
	/**
	 * Preferences property for specifying if a copy of the file should be saved
	 * in the temp directory of the application
	 * 
	 * Value: {@value}
	 */
	public static final String FILE_COPY_PROPERTY = "save-copy";
	
	/**
	 * Preferences property for specifying the name of the temp copy of the data
	 * source file (with possible placeholder replacement). 
	 * 
	 * Value: {@value}
	 */
	public static final String FILE_COPY_NAME_PROPERTY = "file-copy";
	
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
	 *
	 * @param settings the Preferences node containing the configuration for the File acccess
	 * @param replacements map of substitution placeholders to values
	 */
	public FileDataSource(Preferences settings, Map<String, String> replacements) {
		super(settings, replacements);
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.datasource.DataSource#getDataSourceAsInputStream()
	 */
	@Override
	public InputStream getDataSourceAsInputStream() throws IOException {
		FileInputStream input;
		
		try {
			log.info("Retrieving input stream for file {}", file);
			input = new FileInputStream(file);
			log.info("Input stream for file {} acquired. {} bytes available without blocking IO", input.available());
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
		String fileURITemplate = getSettings().get(FILE_URI_PROPERTY, null);
		log.debug("Source file URI template: {}", fileURITemplate);
		
		String fileURI = performReplacements(fileURITemplate);
		log.debug("Source file URI result: {}", fileURI);
		
		try {
			File source = new File(new URI(fileURI));
			log.debug("Source file is {}", source);
			
			if(getSettings().getBoolean(FILE_COPY_PROPERTY, false)) {
				String targetFileTemplate = getSettings().get(FILE_COPY_NAME_PROPERTY, null);
				log.debug("Temp target file template: {}", targetFileTemplate);
				
				String targetFile = performReplacements(targetFileTemplate);
				log.debug("Temp target file: {}", targetFile);
				
				File target = new File(targetFile);
				log.debug("Temp target file is {}", target);
				
				try {
					log.info("Temp copy requested, preparing to copy {} to {}", source, target);
					Files.copy(source, target);
					file = target;
				} catch (IOException e) {
					log.error("Exception copying data source file to temporary file", e);
				}
			}else {
				log.info("Temp copy not requested");
				file = source;
			}
		} catch (URISyntaxException e) {
			log.error("Unable to access file due to invalid file URI", e);
		}

		log.info("Using file {} as data source", file);
	}
	
}
