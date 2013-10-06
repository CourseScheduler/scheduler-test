/**
  * @(#)DataSourceMap.java
  *
  * DataSourceFactory mapping interface for retrieving registered DataSourceFactory instances based on
  * the implementation key. This allows for other classes that need a DataSource instance to be 
  * decoupled from the specific binding organization of the DataSources
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

import com.google.inject.ImplementedBy;

/**
 * DataSourceFactory mapping interface for retrieving registered DataSourceFactory instances based on
 * the implementation key. This allows for other classes that need a DataSource instance to be 
 * decoupled from the specific binding organization of the DataSources
 * 
 * Default implementation is {@link io.coursescheduler.scheduler.datasource.MapBoundDataSourceMap} however
 * this can be overridden in a module by binding an alternative implementation
 *
 * @author Mike Reinhold
 *
 */
@ImplementedBy(MapBoundDataSourceMap.class)
public interface DataSourceMap {
	
	/**
	 * Get a general purpose DataSourceFactory based on the specified  data source key.
	 *
	 * @param key the internal data source name used to reference the data source in configuration
	 * @return a factory instance for the data source specified by the internal name
	 */
	public DataSourceFactory getDataSourceFactory(String key);
	
}
