/**
  * @(#)EphemeralRetriever.java
  *
  * Retriever classes that do not perform persistence of retrieved data. Instead, 
  * the retrieved data can be accessed directly.
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
package io.coursescheduler.scheduler.retrieval;

import java.util.Map;

/**
 * Retriever classes that do not perform persistence of retrieved data. Instead, 
 * the retrieved data can be accessed directly.
 *
 * @author Mike Reinhold
 *
 */
public abstract class EphemeralRetriever extends Retriever {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The retrieved data set. The concurrency semantics of this field is dependent upon
	 * the instances use of this field.
	 */
	private Map<String, Map<String, String>> dataSet;
	
	/**
	 * Set the data set instance. THis should be the result of the EphemeralRetriever's processing
	 * 
	 * @param dataSet the dataSet to store
	 */
	protected void setDataSet(Map<String, Map<String, String>> dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * Access the data set retrieved by the retriever. This method is not guaranteed to 
	 * return valid results until after the Retriever has finished processing.
	 * 
	 * Call {@link java.util.concurrent.RecursiveAction#isDone()} to determine if the instance has
	 * finished processing.
	 * 
	 * @return the dataSet retrieved by the EphemeralRetrieval
	 */
	public Map<String, Map<String, String>> getDataSet(){
		return dataSet;
	}
}
