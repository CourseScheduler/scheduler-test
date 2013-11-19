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

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.coursescheduler.scheduler.parse.ParserRoutine;

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
	
	protected abstract void retrieveDataRecursive(N top, Preferences settings, String attributePath, String nodePath, Map<String, String> replacements);
	
	protected abstract void retrieveDataSingle(N top, Preferences settings, String attributePath, String nodePath, String key, Map<String, String> replacements);
	
	protected abstract void retrieveDataIndex(N top, Preferences settings, String attributePath, String nodePath, String key, int index, Map<String, String> replacements);
	
}
