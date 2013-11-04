/**
  * @(#)ParseConstants.java
  *
  * This class contains a number of general parser constants that can be used by 
  * on parser component implementation.
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
package io.coursescheduler.scheduler.parse;

/**
 * This class contains a number of general parser constants that can be used by 
 * an parser component implementation. Ideally, if parsers use these constants
 * appropriately and consistently, it will be simpler to translate similar constructs
 * between parsers.
 *
 * @author Mike Reinhold
 *
 */
public class ParseConstants {

	/**
	 * General configuration node for parser settings
	 * 
	 * Value: {@value}
	 */
	public static final String GENERAL_SETTINGS_NODE = "general";
	
	/**
	 * Preferences node name for parser group settings. Parser group settings are the
	 * configurations for data values that are shared between elements that are part
	 * of the same group.
	 * 
	 * Example: In a course data set that is organized by section, the course data 
	 * elements need only be investigated once per group of section elements
	 * 
	 * Value: {@value}
	 */
	public static final String GROUP_SETTINGS_NODE = "group";
	
}
