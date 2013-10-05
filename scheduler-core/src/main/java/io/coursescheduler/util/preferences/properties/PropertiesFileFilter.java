/**
  * @(#)PropertiesFileFilter.java
  *
  * A FilenameFilter that can be used to filter directory contents to the set of files
  * that match the file extension used by the PropertiesFilePreferences implementation
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
package io.coursescheduler.util.preferences.properties;

import java.io.File;
import java.io.FilenameFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A FilenameFilter that can be used to filter directory contents to the set of files
 * that match the file extension used by the PropertiesFilePreferences implementation
 *
 * @author Mike Reinhold
 *
 */
class PropertiesFileFilter implements FilenameFilter {
	
	/**
	 * Component Based Logger
	 */
	private final Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * File extension which should be considered valid for the filename filter
	 */
	private String extension;
	
	/**
	 * Create a new Properties File Filter that will match directory contents based
	 * on the file extension that is valid for the PropertiesFilePreferences implementation
	 *
	 * @param extension the filename extension which should be considered as a match
	 */
	PropertiesFileFilter(String extension){
		super();
		
		this.extension = extension;
	}
	
	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File dir, String name) {
		boolean matches = name.endsWith(extension);
		
		log.trace("File named {} matches extension {}: {}", new Object[] {
				name, extension, matches
		});
		
		return matches;
	}
}