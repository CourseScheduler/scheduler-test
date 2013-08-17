/**
  * @(#)PreferencesFactory.java
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
package io.coursescheduler.util.preferences;

import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class to build Preferences instances as needed by the application
 *
 * @author Mike Reinhold
 *
 */
public class PreferencesFactory {
	
	/**
	 * Instance specific logger
	 */
	private Logger log = LoggerFactory.getLogger("io.coursescheduler.util.preferences");
	
	/**
	 * The root Preferences node for the user specific instance of the 
	 * application. This will be used as the application root for all
	 * user based properties  
	 */
	private Preferences userRoot;
	
	/**
	 * The root Preferences node for the system specific instance of the 
	 * application. This will be used as the application root for all
	 * system based properties  
	 */
	private Preferences systemRoot;
	
	/**
	 * Create a new Preferences Factory rooted at the default location
	 *
	 */
	public PreferencesFactory(){
		log.info("Starting preferences initialization");
		userRoot = Preferences.userRoot();
		log.info("User root initialized: {}", userRoot.absolutePath());
		
		try{
			systemRoot = Preferences.systemRoot();
			log.info("System root initialized: {}", systemRoot.absolutePath());
		}catch(SecurityException e){
			log.warn("Unable to create standard system root. Using userRoot instead", e);
			systemRoot = Preferences.userRoot();
			log.info("System root initialized: {}", systemRoot.absolutePath());
		}
	}
	
	/**
	 * Create a new Preferences Factory rooted at the specified root path.
	 * 
	 * @param rootPath the root path for this node under the user or system path
	 */
	public PreferencesFactory(String rootPath){
		log.info("Starting preferences initialization");
		userRoot = Preferences.userRoot().node(rootPath);
		log.info("User root initialized: {}", userRoot.absolutePath());
		
		try{
			systemRoot = Preferences.systemRoot().node(rootPath);
			log.info("System root initialized: {}", systemRoot.absolutePath());
		}catch(SecurityException e){
			log.warn("Unable to create standard system root. Using userRoot instead", e);
			systemRoot = Preferences.userRoot().node(rootPath);
			log.info("System root initialized: {}", systemRoot.absolutePath());
		}
	}
	
	/**
	 * Create a new Preferences Factory rooted at the specified paths for the
	 * system nodes and the user nodes.
	 * 
	 * @param systemRootPath the root path for the system nodes
	 * @param userRootPath the root path for the user nodes
	 */
	public PreferencesFactory(String systemRootPath, String userRootPath){
		log.info("Starting preferences initialization");
		userRoot = Preferences.userRoot().node(userRootPath);
		log.info("User root initialized: {}", userRoot.absolutePath());
		
		try{
			systemRoot = Preferences.systemRoot().node(systemRootPath);
			log.info("System root initialized: {}", systemRoot.absolutePath());
		}catch(SecurityException e){
			log.warn("Unable to create standard system root. Using userRoot instead", e);
			systemRoot = Preferences.userRoot().node(systemRootPath);
			log.info("System root initialized: {}", systemRoot.absolutePath());
		}
	}
	
	/**
	 * Return a Preferences instance rooted at the specified path, within
	 * user preferences space and relative to the application root path
	 * specified at the creation of the PreferencesFactory.
	 *
	 * @param path the preferences node to return
	 * @return a Preferences rooted at the specified path relative to the 
	 * root of this factory
	 */
	public Preferences getUserNode(String path){
		Preferences pref = userRoot.node(path);
		log.debug("Using User Preferences node {} at {}", path, pref.absolutePath());
		return pref;
	}
	
	/**
	 * Return a Preferences instance rooted at the specified path, within
	 * system preferences space and relative to the application root path
	 * specified at the creation of the PreferencesFactory.
	 *
	 * @param path the preferences node to return
	 * @return a Preferences rooted at the specified path relative to the 
	 * root of this factory
	 */
	public Preferences getSystemNode(String path){
		Preferences pref = systemRoot.node(path);
		log.debug("Using System Preferences node {} at {}", path, pref.absolutePath());
		return pref;
	}
}
