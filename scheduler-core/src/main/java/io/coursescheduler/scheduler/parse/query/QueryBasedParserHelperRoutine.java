/**
  * @(#)QueryBasedParserHelperRoutine.java
  *
  * TODO FILE PURPOSE
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
package io.coursescheduler.scheduler.parse.query;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.coursescheduler.scheduler.parse.ParseException;
import io.coursescheduler.scheduler.parse.ParserRoutine;
import io.coursescheduler.util.script.engine.ScriptEngine;
import static io.coursescheduler.scheduler.parse.query.QueryBasedParserRoutine.QUERY_PREFERENCES_NODE;

/**
 * TODO Describe this type
 *
 * @author Mike Reinhold
 *
 */
public abstract class QueryBasedParserHelperRoutine<N> extends ParserRoutine {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Component based logger
	 */
	private transient Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Retrieved data Map, connecting the element key path to the element data value
	 */
	private ConcurrentMap<String, String> data;

	/**
	 * The Parser Tool that will be used to assist with querying data out of the source data
	 */
	private QueryBasedParserTool<N> parser;
	
	private ScriptEngine script;
	
	@AssistedInject
	public QueryBasedParserHelperRoutine(QueryBasedParserToolMap toolMap, @Assisted("data") ConcurrentMap<String, String> data, @Assisted("key") String key) {
		super();

		this.data = data;
		this.parser = toolMap.getQueryBasedParserTool(key);
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		// TODO METHOD STUB
		
	}
	
	protected void retrieveDataRecursive(N top, Preferences settings, String attributePath, String nodePath, Map<String, String> replacements) throws ParseException {
		Preferences codes = settings.node(QUERY_PREFERENCES_NODE);
		try {
			for(String key: codes.keys()){
				String newPath = (attributePath == null || attributePath.compareTo("") == 0) ? key : attributePath + "." + key;
				String newKey = (nodePath == null || nodePath.compareTo("") == 0) ? key : nodePath + "." + key;
							
				retrieveDataSingle(top, settings, newPath, newKey, key, data);
			}
		} catch (BackingStoreException e) {
			log.error("Exception while reading profile entries for profile node {}: {}", codes, e);
			throw new ParseException(e);
		}
	}
	
	protected void retrieveDataSingle(N top, Preferences settings, String attributePath, String nodePath, String key, Map<String, String> replacements) throws ParseException {
		try{
			Preferences codes = settings.node(QUERY_PREFERENCES_NODE);
			String query = codes.get(key, null);
			List<N> children = parser.query(top, codes, key);
			
			//write the number of values
			String count = Integer.toString(children.size());
			data.put(nodePath, count);
			log.trace("Element: {} ( \" {} \" ) = {}", new Object[] {nodePath, query, count});
						
			//process each item
			for(int item = 0; item < children.size(); item++){
				N child = children.get(item);
				retrieveDataIndex(child, settings, attributePath, nodePath, key, item, data);
			}
		} catch(ParseException e){
			log.error("Exception retrieving data element for attribute {} at keypath {}", attributePath, nodePath, e);
			throw e;
		} catch(BackingStoreException e) {
			log.error("Exception reading profile entries for profile node {}: {}", settings, e);
			throw new ParseException(e);
		}
	}
	
	protected void retrieveDataIndex(N top, Preferences settings, String attributePath, String nodePath, String key, int index, Map<String, String> replacements)  throws ParseException, BackingStoreException {
		log.debug("Getting code subnode: {}", key);
		Preferences codes = settings.node(key); 
		Preferences subCodes = codes.node(QUERY_PREFERENCES_NODE);
		int subNodeCount = 0;
		
		try {
			subNodeCount = subCodes.keys().length;
			
			if(subNodeCount == 0) {
				log.debug("No sub code entries exist for node {}, removing", key);
				subCodes.removeNode();
				codes.removeNode();
			}
		} catch(IllegalStateException e) {
			//node previously removed - ok for us since we have to load (or "create") a node 
			//in order to check for sub keys.
			log.trace("No sub code entries exist for node {}, previously removed", key);
		}
		
		if(subNodeCount > 0){
			log.debug("Sub codes exist for node {}, processing", key);
			retrieveDataRecursive(top, codes, attributePath, nodePath + "." + index, data);
		}else{					
			String itemKey = nodePath + "." + index;
			String value = parser.asString(top);
			
			value = script.executeScript(value, settings, key, data);	//TODO handle script
		
			data.put(itemKey, value);
			log.trace("Element: {} ( \" text() \" ) = {}", itemKey, value);
		}
	}
	
}
