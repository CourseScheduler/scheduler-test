/**
  * @(#)SingleStreamRetriever.java
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

import java.io.InputStream;
import java.util.Map;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.coursescheduler.scheduler.datasource.DataSource;
import io.coursescheduler.scheduler.datasource.DataSourceFactory;
import io.coursescheduler.scheduler.datasource.DataSourceMap;
import io.coursescheduler.scheduler.parse.routines.ParserRoutine;
import io.coursescheduler.scheduler.parse.routines.ParserRoutineMap;
import io.coursescheduler.scheduler.parse.routines.StreamParserRoutineFactory;
import io.coursescheduler.scheduler.retrieval.EphemeralRetriever;
import io.coursescheduler.util.variable.SubstitutionVariableSource;

/**
 * Class implementing a simple, single stream data retrieval
 *
 * @author Mike Reinhold
 *
 */
public class SingleStreamRetriever extends EphemeralRetriever {
	
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
	private transient ParserRoutineMap parseRoutineMap;
	
	/**
	 * Map of the Data Source implementations that can be used to build a data stream
	 */
	private transient DataSourceMap dataSourceMap;
	
	/**
	 * Preferences node that contains the configuration for the retrieval instance
	 */
	private transient Preferences config;
	
	/**
	 * Variable source for local variables
	 */
	private transient SubstitutionVariableSource replacements;
	
	/**
	 * Create a new SingleStreamRetriver that is useful for retrieving data from a single source, parsing it, and processing it for use.
	 *
	 * @param parserRoutineMap the ParserRoutineMapping instance for accessing ParserRoutineFactory instances
	 * @param dataSourceMap the DataSourceMapping instance for accessing DataSourceFactory instances
	 * @param config the Preferences node containing the configuration for the retriever
	 * @param replacements the svariable source containing local variable replacement values
	 */
	@AssistedInject
	public SingleStreamRetriever(ParserRoutineMap parserRoutineMap, DataSourceMap dataSourceMap, @Assisted("config") Preferences config, @Assisted("localVars") SubstitutionVariableSource replacements) {
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
		try {
			log.info("Preparing to process single stream retrieval");
			log.debug("Processing stream using Retriever configuration node {}", config);
			long start = System.currentTimeMillis();
			
			//create an input stream from the data source and process it into a data set
			InputStream source = getDataSourceInputStream();
			setDataSet(getParserDataSets(source));
			
			long end = System.currentTimeMillis();
			log.info("Finished processing single stream retrieval in {} ms", end - start);
		}catch(Exception e) {
			log.error("Exception encountered during stream retrieval", e);
		}
	}
	
	/**
	 * Build and execute the DataSource based on the stored configuration
	 *
	 * @return the InputStream corresponding to the DataSource
	 * @throws Exception if there is an issue building or executing the DataSource
	 */
	private InputStream getDataSourceInputStream() throws Exception {		
		try {
			//Find the data source configuration node
			String dataSourceConfigNode = config.get(DATA_SOURCE_CONFIG_NODE_PROPERTY, null);
			log.debug("Using child node {} for data source configuration", dataSourceConfigNode);
			
			//Access the data source configuration node
			Preferences dataSourceConfig = config.node(dataSourceConfigNode);
			log.debug("Using Data Source configuration node {}", dataSourceConfig);
			
			//Retrieve the data source implementation internal name
			String dataSourceImplementation = dataSourceConfig.get(IMPLEMENTATION_KEY_PROPERTY, null);
			log.debug("Found data source implementation key ({}): {}", IMPLEMENTATION_KEY_PROPERTY, dataSourceImplementation);
			
			//Get a DataSourceFactory
			DataSourceFactory dataSourceFactory = dataSourceMap.getDataSourceFactory(dataSourceImplementation);
			log.debug("Retrieved DataSourceFactory {} using implementation key {}", dataSourceFactory, dataSourceImplementation);
			
			//Get the DataSource instance
			DataSource source = dataSourceFactory.createDataSource(dataSourceConfig, replacements);
			log.debug("Built Data Source using configuration node {} and local variable source {}", dataSourceConfig, replacements);
			
			//Execute the DataSource
			log.debug("Invoking execution of DataSource {}", source);
			long start = System.currentTimeMillis();
			source.fork();
			
			while(!source.isDataSourceInputStreamReady());	//TODO sleep for small amount of time?
			
			long end = System.currentTimeMillis();
			log.info("DataSource {} ready in {} ms", source, end - start);
			
			//Retrieve InputStream from the data source
			InputStream dataSourceStream = source.getDataSourceAsInputStream();
			log.debug("Retrieved input stream {} from data source", dataSourceStream);

			return dataSourceStream;
		} catch(Exception e) {
			log.error("Exception encountered during data source creation or execution", e);
			throw e;
		}
	}
	
	/**
	 * Build and execute the ParserRoutine based on the stored configuration using the provided
	 * data source stream
	 *
	 * @param source the data source stream to use as the parser routine source
	 * 
	 * @return the data sets built as a result of parsing the input stream
	 */
	private Map<String, Map<String, String>> getParserDataSets(InputStream source){
		try {
			//Find the parser configuration node
			String parserRoutineConfigNode = config.get(PARSE_ROUTINE_CONFIG_NODE_PROPERTY, null);
			log.debug("Using child node {} for parse routine configuration", parserRoutineConfigNode);
			
			//Access the parser configuration node
			Preferences parserRoutineConfig = config.node(parserRoutineConfigNode);
			log.debug("Using Parse Routine configuration node {}", parserRoutineConfig);
			
			//Retrieve the parser implementation internal name
			String parserRoutineImplementation = parserRoutineConfig.get(IMPLEMENTATION_KEY_PROPERTY, null);
			log.debug("Found parse routine implementation key ({}): {}", IMPLEMENTATION_KEY_PROPERTY, parserRoutineImplementation);
			
			//Get a ParserRoutineFactory
			StreamParserRoutineFactory parserFactory = parseRoutineMap.getStreamParserRoutineFactory(parserRoutineImplementation);
			log.debug("Retrieved StreamParserRoutineFactory {} using implementation key {}", parserFactory, parserRoutineImplementation);
			
			//Get the Parser instance
			ParserRoutine parser = parserFactory.createParserRoutine(source,parserRoutineConfig);
			log.debug("Built Parser Routine using configuration node {} and data source input stream {}", parserRoutineConfig, source);
			
			//Execute the ParserRoutine
			log.debug("Invoking execution of ParserRoutine {}", parser);
			long start = System.currentTimeMillis();
			parser.invoke();
			long end = System.currentTimeMillis();
			log.info("Finished executing ParserRoutine {} in {} ms", parser, end-start);
			
			//Retrieve the data sets from the parser
			Map<String, Map<String, String>> dataSets = parser.getDataSets();
			log.debug("Retrieved data sets {} from parser routine", dataSets);
			
			return dataSets;
		}catch(Exception e) {
			log.error("Exception encountered during parser routine creation or execution", e);
			throw e;
		}
	}	
}
