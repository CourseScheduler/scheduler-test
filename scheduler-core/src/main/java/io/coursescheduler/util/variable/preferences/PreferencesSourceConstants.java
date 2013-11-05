/**
  * @(#)PreferencesSourceConstants.java
  *
  * Constants used by PreferencesVariableSources
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
package io.coursescheduler.util.variable.preferences;

/**
 * Constants used by PreferencesVariableSources
 *
 * @author Mike Reinhold
 *
 */
class PreferencesSourceConstants {

	/**
	 * Node path separator for Preferences nodes - used to determine if a preferences variable
	 * is local or relative.
	 * 
	 * Value: {@value}
	 */
	public static final String PREFERENCES_NODE_PATH_SEPARATOR = "/";
	
	/**
	 * Preferences property prefix for identifying system preferences variables
	 * 
	 * Value: {@value}
	 */
	public static final String SYSTEM_PREFERENCES_VARIABLE_PREFIX = "prefs.system/";
	
	/**
	 * Preferences property prefix for identifying user preferences variables
	 * 
	 * Value: {@value}
	 */
	public static final String USER_PREFERENCES_VARIABLE_PREFIX = "prefs.user/";
	
	/**
	 * Path for root node (either system or user)
	 * 
	 * Value: {@value}
	 */
	public static final String ROOT_NODE_PATH = "/";
	
}
