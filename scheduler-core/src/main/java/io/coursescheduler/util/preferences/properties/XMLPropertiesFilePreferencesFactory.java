/**
  * @(#)PropertiesFilePreferencesFactory.java
  *
  * Factory class for XMLPropertiesFilePreferences
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


/**
 * Factory class for XMLPropertiesFilePreferences
 *
 * @author Mike Reinhold
 *
 */
public class XMLPropertiesFilePreferencesFactory extends PropertiesPreferencesFactory {
	
	/**
	 * Create a new XMLPropertiesFilePreferencesFactory with System and User root nodes.
	 *
	 */
	public XMLPropertiesFilePreferencesFactory(){
		super(new XMLPropertiesFilePreferences("", false), new XMLPropertiesFilePreferences("", true));
	}
}
