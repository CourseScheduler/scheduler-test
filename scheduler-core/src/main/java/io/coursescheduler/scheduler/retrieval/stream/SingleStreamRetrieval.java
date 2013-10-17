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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.coursescheduler.scheduler.datasource.DataSource;
import io.coursescheduler.scheduler.datasource.DataSourceMap;
import io.coursescheduler.scheduler.parse.routines.ParserRoutine;
import io.coursescheduler.scheduler.parse.routines.ParserRoutineMap;
import io.coursescheduler.scheduler.retrieval.Retriever;
import io.coursescheduler.util.variable.SubstitutionVariableSource;

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
	 * Preferences configuration entry for the data source
	 * 
	 * Value: {@value}
	 */
	public static final String DATA_SOURCE_CONFIG_NODE_PROPERTY= "config.datasource";
	
	/**
	 * Preferences configuration entry for the parse routine
	 * 
	 * Value: {@value}
	 */
	public static final String PARSE_ROUTINE_CONFIG_NODE_PROPERTY = "config.parseroutine";
	
	/**
	 * Component based logger
	 */
	private transient Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Map of the Parser Routine implementations that can be used to parse the data stream
	 */
	private ParserRoutineMap parseRoutineMap;
	
	/**
	 * Map of the Data Source implementations that can be used to build a data stream
	 */
	private DataSourceMap dataSourceMap;
	
	/**
	 * Preferences node that contains the configuration for the retrieval instance
	 */
	private Preferences config;
	
	/**
	 * Variable source for local variables
	 */
	private SubstitutionVariableSource replacements;
	
	@AssistedInject
	public SingleStreamRetrieval(ParserRoutineMap parserRoutineMap, DataSourceMap dataSourceMap, @Assisted("config") Preferences config, @Assisted("localVars") SubstitutionVariableSource replacements) {
		super();
		
		this.parseRoutineMap = parserRoutineMap;
		this.dataSourceMap = dataSourceMap;
		this.config = config;
		this.replacements = replacements;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		//TODO evaluate finer grain exception handling
		try {
		
			//Retrieve and run the data source
			String dataSourceConfigNode = config.get(DATA_SOURCE_CONFIG_NODE_PROPERTY, null);
			Preferences dataSourceConfig = config.node(dataSourceConfigNode);
			String dataSourceImplementation = dataSourceConfig.get(IMPLEMENTATION_KEY_PROPERTY, null);
			DataSource source = dataSourceMap.getDataSourceFactory(dataSourceImplementation).createDataSource(dataSourceConfig, replacements);
			source.invoke();
			
			//Retrieve and execute the parser on the data stream
			String parserRoutineConfigNode = config.get(PARSE_ROUTINE_CONFIG_NODE_PROPERTY, null);
			Preferences parserRoutineConfig = config.node(parserRoutineConfigNode);
			String parserRoutineImplementation = parserRoutineConfig.get(IMPLEMENTATION_KEY_PROPERTY, null);
			ParserRoutine parser = parseRoutineMap.getStreamParserRoutineFactory(parserRoutineImplementation).createParserRoutine(
					source.getDataSourceAsInputStream(),
					parserRoutineConfig
			);
			parser.invoke();
			
			//Retrieve and use the data sink to persist the data
			//TODO define the data sink, retrieve it, execute it
			
		}catch(Exception e) {
			log.error("Exception encountered during stream retrieval", e);
		}
	}
	
}
