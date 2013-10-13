/**
  * @(#)ApplicationDirectoryVariableSource.java
  *
  * Global variable source for accessing application directories
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
package io.coursescheduler.scheduler.directory;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coursescheduler.util.variable.GlobalSubstitutionVariableSource;

/**
 * Global variable source for accessing application directories
 *
 * @author Mike Reinhold
 *
 */
public class ApplicationDirectoryVariableSource extends GlobalSubstitutionVariableSource {
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Map containing the directory variables and their values
	 */
	private Map<String, String> directories;
	
	/**
	 * Create and initialize the application directory variables
	 *
	 */
	public ApplicationDirectoryVariableSource() {
		super();
		
		this.directories = new HashMap<>();
		
		initializeDirectories();
	}
	
	/**
	 * Build the Directories map for the application
	 *
	 */
	private void initializeDirectories() {
		directories.put("dir.tmp", "tmp");
		directories.put("dir.log", "log");
		directories.put("dir.conf", "config");
		directories.put("dir.data", "Data");
		directories.put("dir.image", "Images");
		directories.put("dir.cwd", "${system.user.dir}");
		directories.put("dir.home.name", "Scheduler");
		directories.put("dir.home", "${system.user.home}/${dir.home}");
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.lang3.text.StrLookup#lookup(java.lang.String)
	 */
	@Override
	public String lookup(String variable) {
		log.debug("Looking up variable {} in Application Directory Source", variable);
		String value;
		if(directories.containsKey(variable)) {
			value = directories.get(variable);
			log.debug("Value {} found for variable {} found in Application Directory Source", value, variable);
		}else {
			value = null;
			log.debug("Variable {} not found in Application Directory Source, using null ({})", variable, null);
		}
		return value;
	}
	
}
