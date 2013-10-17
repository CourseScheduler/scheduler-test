/**
  * @(#)DataSourceFactory.java
  *
  * Factory interface for Data Source implementations
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

import io.coursescheduler.util.variable.SubstitutionVariableSource;

import java.util.prefs.Preferences;

import com.google.inject.assistedinject.Assisted;

/**
 * Factory interface for Data Source implementations
 *
 * @author Mike Reinhold
 *
 */
public interface DataSourceFactory {
	
	/**
	 * Create a new DataSource using the specified configuration node and the specified local variables
	 *
	 * @param settings the configuration node containing the settings for the DataSource
	 * @param replacements the local variables that can be used for replacements
	 * @return the DataSource
	 */
	public DataSource createDataSource(@Assisted("config") Preferences settings, @Assisted("localVars") SubstitutionVariableSource replacements);
	
}
