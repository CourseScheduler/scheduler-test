/**
  * @(#)DataSourceConstants.java
  *
  * This class contains a number of general data source constants that can be used by 
  * a data source component implementation. Ideally, if data sources use these constants
  * appropriately and consistently, it will be simpler to translate similar constructs
  * between data sources.
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
package io.coursescheduler.scheduler.datasource;

/**
 * This class contains a number of general data source constants that can be used by 
 * a data source component implementation. Ideally, if data sources use these constants
 * appropriately and consistently, it will be simpler to translate similar constructs
 * between data sources.
 *
 * @author Mike Reinhold
 *
 */
public class DataSourceConstants {

	/**
	 * General configuration node for data source settings
	 * 
	 * Value: {@value}
	 */
	public static final String GENERAL_SETTINGS_NODE = "general";
	
	/**
	 * General configuration property for data sources to indicate what 
	 * character set should be used to interpret the input.
	 * 
	 * Value: {@value}
	 */
	public static final String INPUT_CHARSET_PROPERTY = "source.charset";
}
