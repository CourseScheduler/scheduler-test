/**
  * @(#)PreferencesPropertiesModule.java
  *
  * Guice Module for binding the PreferencesPropertiesFactory class as the
  * implementation class for the PreferencesFactory interface
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

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Guice Module for binding the PreferencesPropertiesFactory class as the
 * implementation class for the PreferencesFactory interface
 *
 * @author Mike Reinhold
 *
 */
public class PreferencesPropertiesModule extends AbstractModule {

	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		//PreferencesFactory will be implemented by a singleton PreferencesPropertiesFactor
		bind(PreferencesFactory.class)
			.to(PreferencesPropertiesFactory.class)
			.in(Singleton.class);
	}

}
