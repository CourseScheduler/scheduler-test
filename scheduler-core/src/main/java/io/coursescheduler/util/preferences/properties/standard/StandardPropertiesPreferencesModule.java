/**
  * @(#)StandardPropertiesPreferencesModule.java
  *
  * Guice module for binding the Standard Properties File Preferences backend as the PreferencesFactory
  * implementation.
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
package io.coursescheduler.util.preferences.properties.standard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import io.coursescheduler.util.preferences.PreferencesFactory;
import io.coursescheduler.util.preferences.PreferencesModule;
import io.coursescheduler.util.preferences.properties.PropertiesPreferencesFactory;

/**
 * Guice module for binding the Standard Properties File Preferences backend as the PreferencesFactory
 * implementation.
 *
 * @author Mike Reinhold
 *
 */
public class StandardPropertiesPreferencesModule extends PreferencesModule {
	
	/**
	 * Component based logger
	 */
	Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.util.preferences.properties.PropertiesPreferencesModule#configure()
	 */
	@Override
	public void configure() {
		setPreferencesFactory(StandardPropertiesFilePreferencesFactory.class.getName());
		
		//PreferencesFactory will be implemented by a singleton PreferencesPropertiesFactor
		log.debug("Binding {} to {} in scope {}", new Object[] {
				PreferencesFactory.class,
				PropertiesPreferencesFactory.class,
				Singleton.class
		});
		bind(PreferencesFactory.class)
			.to(PropertiesPreferencesFactory.class)
			.in(Singleton.class);
	}
	
}
