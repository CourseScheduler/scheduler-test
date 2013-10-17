/**
  * @(#)SingleStreamRetrieval.java
  *
  * Class implementing a simple, single stream data retrieval
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
package io.coursescheduler.scheduler.retrieval.stream;

import java.util.prefs.Preferences;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.coursescheduler.scheduler.datasource.DataSourceMap;
import io.coursescheduler.scheduler.parse.routines.ParserRoutineMap;
import io.coursescheduler.scheduler.retrieval.Retriever;

/**
 * Class implementing a simple, single stream data retrieval
 *
 * @author Mike Reinhold
 *
 */
public class SingleStreamRetrieval extends Retriever {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * TODO Describe this field
	 */
	public static final String DATA_SOURCE_IMPLEMENTATION_KEY = "";
	
	/**
	 * TODO Describe this field
	 */
	public static final String PARSER_ROUTINE_IMPLEMENTATION_KEY = "";
	
	/**
	 * Map of the Parser Routine implementations that can be used to parse the data stream
	 */
	private ParserRoutineMap parserRoutineMap;
	
	/**
	 * Map of the Data Source implementations that can be used to build a data stream
	 */
	private DataSourceMap dataSourceMap;
	
	/**
	 * Preferences node that contains the configuration for the retrieval instance
	 */
	private Preferences config;
	
	@AssistedInject
	public SingleStreamRetrieval(ParserRoutineMap parserRoutineMap, DataSourceMap dataSourceMap, @Assisted("config") Preferences config) {
		super();
		
		this.parserRoutineMap = parserRoutineMap;
		this.dataSourceMap = dataSourceMap;
		this.config = config; 
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		// TODO METHOD STUB
		
	}
	
}
