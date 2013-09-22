/**
  * @(#)PropertiesPreferencesFactory.java
  *
  * Factory class to build Preferences instances as needed by the application
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

import io.coursescheduler.util.preferences.PreferencesFactory;

import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class to build Preferences instances as needed by the application
 *
 * @author Mike Reinhold
 *
 */
public class PropertiesPreferencesFactory implements PreferencesFactory {
	
	/**
	 * System property for specifying the system root path
	 */
	private static final String ROOT_NODE_SYSTEM = "io.coursescheduler.util.preferences.root.system";
	
	/**
	 * System property for specifying the user root path
	 */
	private static final String ROOT_NODE_USER = "io.coursescheduler.util.preferences.root.user";
	
	/**
	 * Default System Root path
	 */
	private static final String DEFAULT_ROOT_SYSTEM = "/";
	
	/**
	 * Default User Root path
	 */
	private static final String DEFAULT_ROOT_USER = "/";
	
	/**
	 * Component based logger
	 */
	private transient Logger log = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * The root Preferences node for the system specific instance of the 
	 * application. This will be used as the application root for all
	 * system based properties  
	 */
	private Preferences systemRoot;
	
	/**
	 * The root Preferences node for the user specific instance of the 
	 * application. This will be used as the application root for all
	 * user based properties  
	 */
	private Preferences userRoot;
	
	/**
	 * Create a new Preferences Factory rooted at the default location
	 *
	 */
	public PropertiesPreferencesFactory(){
		String systemPath = System.getProperty(ROOT_NODE_SYSTEM, DEFAULT_ROOT_SYSTEM);
		String userPath = System.getProperty(ROOT_NODE_USER, DEFAULT_ROOT_USER);
		
		log.info("Starting preferences initialization");
		userRoot = Preferences.userRoot().node(userPath);
		log.info("User root initialized: {}", userRoot.absolutePath());
		
		try{
			systemRoot = Preferences.systemRoot().node(systemPath);
			log.info("System root initialized: {}", systemRoot.absolutePath());
		}catch(SecurityException e){
			log.warn("Unable to create standard system root. Using userRoot instead", e);
			systemRoot = Preferences.userRoot().node(systemPath);
			log.info("System root initialized: {}", systemRoot.absolutePath());
		}
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.util.preferences.PreferencesFactory#getUserNode(java.lang.String)
	 */
	@Override
	public Preferences getUserNode(String path){
		Preferences pref = userRoot.node(path);
		log.debug("Using User Preferences node {} at {}", path, pref.absolutePath());
		return pref;
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.util.preferences.PreferencesFactory#getSystemNode(java.lang.String)
	 */
	@Override
	public Preferences getSystemNode(String path){
		Preferences pref = systemRoot.node(path);
		log.debug("Using System Preferences node {} at {}", path, pref.absolutePath());
		return pref;
	}
}
