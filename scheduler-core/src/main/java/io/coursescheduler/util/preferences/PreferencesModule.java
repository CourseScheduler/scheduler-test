/**
  * @(#)PreferencesModule.java
  *
  * Define a base Guice module for java.util.Preferences factory implementations. Contains helper
  * methods for ensuring that the Guice module can rugister its Properties Factory implementation 
  * properly as part of its module configuration.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Define a base Guice module for java.util.Preferences factory implementations. Contains helper
 * methods for ensuring that the Guice module can rugister its Properties Factory implementation 
 * properly as part of its module configuration.
 *
 * @author Mike Reinhold
 *
 */
public abstract class PreferencesModule extends com.google.inject.AbstractModule {

	/**
	 * Instance specific logger
	 */
	Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * System property for specifying the java.util.pref.Preferences backend store implementation
	 * that should be used by the application
	 */
	protected static final String PREFERENCES_FACTORY_PROPERTY = "java.util.prefs.PreferencesFactory";
	
	/**
	 * Check if the Java system property ({@link #PREFERENCES_FACTORY_PROPERTY}) for the default Preferences Factory
	 * has been set (by the calling application or on the command line). If so, let the JVM use the Preferences Factory 
	 * specified therein. Otherwise, use the preferences factory class passed to this method as the Preferences Factory.
	 * 
	 * Implementations of this abstract module should call this method with its Preferences Factory implementation as a 
	 * parameter to ensure that the factory is properly registered
	 *
	 * @param preferencesFactoryClass the class corresponding to the desired implementation
	 */
	protected final void setPreferencesFactory(Class<? extends PreferencesFactory> preferencesFactoryClass){
		setPreferencesFactory(preferencesFactoryClass.getName());
	}

	/**
	 * Check if the Java system property ({@link #PREFERENCES_FACTORY_PROPERTY}) for the default Preferences Factory
	 * has been set (by the calling application or on the command line). If so, let the JVM use the Preferences Factory 
	 * specified therein. Otherwise, use the preferences factory class passed to this method as the Preferences Factory.
	 * 
	 * Implementations of this abstract module should call this method with its Preferences Factory implementation as a 
	 * parameter to ensure that the factory is properly registered
	 *
	 * @param preferencesFactoryClassName the class name corresponding to the desired implementation
	 */
	protected final void setPreferencesFactory(String preferencesFactoryClassName){
		//check for a desired Preferences Factory class
		log.debug("Checking for Preferences backend specified in {}", PREFERENCES_FACTORY_PROPERTY);
		String factory = System.getProperty(PREFERENCES_FACTORY_PROPERTY);
		if(factory == null){
			System.setProperty(PREFERENCES_FACTORY_PROPERTY, preferencesFactoryClassName);
			log.debug("No backend found in {}. Using default backend: {}", PREFERENCES_FACTORY_PROPERTY, preferencesFactoryClassName);
		}else {
			log.debug("Factory {} found in {}", factory, PREFERENCES_FACTORY_PROPERTY);
		}
	}
}
