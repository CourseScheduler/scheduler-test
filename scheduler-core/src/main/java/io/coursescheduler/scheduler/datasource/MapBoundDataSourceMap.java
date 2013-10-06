/**
  * @(#)MapBoundDataSourceMap.java
  *
  * Default DataSourceFactory mapping class for retrieving registered DataSourceFactory instance based on
  * the implementation key. This implementation uses the MapBinding characteristics of the DataSources and
  * their factories in order to provide the keyed factory retrieval methods.
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

import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Default DataSourceFactory mapping class for retrieving registered DataSourceFactory instance based on
 * the implementation key. This implementation uses the MapBinding characteristics of the DataSources and
 * their factories in order to provide the keyed factory retrieval methods.
 *
 * @author Mike Reinhold
 *
 */
public class MapBoundDataSourceMap implements DataSourceMap {

	/**
	 * Map of the DataSourceFactory implementations based on the DataSource internal name
	 */
	private Map<String, Provider<DataSourceFactory>> dataSourceFactories;
	
	/**
	 * Create a new DataSourceMap containing Maps of the data source names to the factory instances that
	 * create data source instances of the specified type
	 *
	 * @param dataSourceFactories map of internal names to providers of factories for data sources
	 */
	@Inject
	public MapBoundDataSourceMap(Map<String, Provider<DataSourceFactory>> dataSourceFactories) {
		super();
		
		this.dataSourceFactories = dataSourceFactories;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.datasource.DataSourceMap#getDataSourceFactory(java.lang.String)
	 */
	@Override
	public DataSourceFactory getDataSourceFactory(String key) {
		return dataSourceFactories.get(key).get();
	}
	
}
