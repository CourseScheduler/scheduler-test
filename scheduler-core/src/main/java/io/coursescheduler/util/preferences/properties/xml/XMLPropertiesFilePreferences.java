/**
  * @(#)XMLPropertiesFilePreferences.java
  *
  * An XML Properties File implementation of PropertiesPreferences
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
package io.coursescheduler.util.preferences.properties.xml;

import io.coursescheduler.util.preferences.properties.PropertiesPreferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.AbstractPreferences;

/**
 * An XML Properties File implementation of PropertiesPreferences
 *
 * @author Mike Reinhold
 *
 */
public class XMLPropertiesFilePreferences extends PropertiesPreferences {

	/**
	 * Create a new Preferences instance as a child of the specified instance using the
	 * specified node name.
	 *
	 * @param parent the parent Preferences node
	 * @param name the preferences node name
	 */
	XMLPropertiesFilePreferences(AbstractPreferences parent, String name) {
		super(parent, name);
	}

	/**
	 * Create a new root Preferences instance using the specified node name. If isUserRoot
	 * is specified then a user root Preferences instance will be created, otherwise a 
	 * system root Preferences instance will be created.
	 *
	 * @param name the preferences node name
	 * @param isUserNode true if a user root instance, false if a system root
	 */
	XMLPropertiesFilePreferences(String name, boolean isUserNode) {
		super(name, isUserNode);
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.util.config.properties.PropertiesPreferences#getFileExtension()
	 */
	@Override
	protected String getFileExtension() {
		return "xml";
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.util.config.properties.PropertiesPreferences#createChildNode(java.util.prefs.AbstractPreferences, java.lang.String)
	 */
	@Override
	protected PropertiesPreferences createChildNode(AbstractPreferences abstractPreferences, String name) {
		return new XMLPropertiesFilePreferences(abstractPreferences, name);
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.util.config.properties.PropertiesPreferences#load(java.util.Properties, java.io.FileInputStream)
	 */
	@Override
	protected void load(Properties properties, FileInputStream fis)	throws IOException {
		properties.loadFromXML(fis);
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.util.config.properties.PropertiesPreferences#store(java.util.Properties, java.io.FileOutputStream)
	 */
	@Override
	protected void store(Properties properties, FileOutputStream fos) throws IOException {
		properties.storeToXML(fos, null);
	}

}
