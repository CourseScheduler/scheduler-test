/**
  * @(#)ParserRoutine.java
  *
  * Parser routines are the high level organization for retrieving data from a source or type of source.
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;

/**
 * Parser routines are the high level organization for retrieving data from a source or type of source.
 *
 * @author Mike Reinhold
 *
 */
public abstract class ParserRoutine extends RecursiveAction {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Batch process size configuration property name
	 * 
	 * Value: {@value}
	 */
	public static final String BATCH_SIZE_PROPERTY = "batch-size";

	/**
	 * Map of the data parsed by the parse routine (represented by the data element 
	 * indexed-name and the corresponding value)
	 */
	private Map<String, Map<String, String>> dataSets;
	
	/**
	 * Construct a new ParserRoutine and initialize the data set map
	 *
	 */
	protected ParserRoutine() {
		dataSets = new ConcurrentHashMap<>();
	}

	/**
	 * Return the map of data sets. This map is guaranteed to be thread safe, although
	 * it is up to the the users of the data set map to ensure that the inner maps stored
	 * against the key is also thread safe.
	 * 
	 * Calling this method prior to execution of the parser routine may result in invalid results
	 * 
	 * @return the dataSets map of keys to map of data elements and values
	 */
	public Map<String, Map<String, String>> getDataSets() {
		return dataSets;
	}
}
