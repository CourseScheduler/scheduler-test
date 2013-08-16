/**
  * @(#)PropertiesFilePreferencesFactory.java
  *
  * Factory class for PropertiesPreferences, used by Properties backed Preferences
  * implementations
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

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * Factory class for PropertiesPreferences, used by Properties backed Preferences
 * implementations
 *
 * @author Mike Reinhold
 *
 */
public abstract class PropertiesPreferencesFactory implements PreferencesFactory {

	/**
	 * The System root preferences node
	 */
	private Preferences systemRoot;
	
	/**
	 * The User root preferences node
	 */
	private Preferences userRoot;
	
	/**
	 * Create a new PropertiesPreferencesFactory using the specified system and user roots
	 *
	 */
	public PropertiesPreferencesFactory(PropertiesPreferences system, PropertiesPreferences user){
		systemRoot = system;
		userRoot = user;
	}
	
	/* (non-Javadoc)
	 * @see java.util.prefs.PreferencesFactory#systemRoot()
	 */
	@Override
	public Preferences systemRoot() {
		return systemRoot;
	}

	/* (non-Javadoc)
	 * @see java.util.prefs.PreferencesFactory#userRoot()
	 */
	@Override
	public Preferences userRoot() {
		return userRoot;
	}

}
