/**
  * @(#)MapBoundRetrieverMap.java
  *
  * Default RetrieverMap implementation for retrieving registered RetrieverFactory implementations. The 
  * RetrieverFactory instance can then be used to build Retriever instances. This implementation uses the
  * Guice Map bound characteristics of the RetrieverFactory classes to return the keyed factory implementation.
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

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Default RetrieverMap implementation for retrieving registered RetrieverFactory implementations. The 
 * RetrieverFactory instance can then be used to build Retriever instances. This implementation uses the
 * Guice Map bound characteristics of the RetrieverFactory classes to return the keyed factory implementation.
 *
 * @author Mike Reinhold
 *
 */
public class MapBoundRetrieverMap implements RetrieverMap {

	/**
	 * Map of the Retriever internal name to a provider for the RetrieverFactory corresponding to the internal name
	 */
	private Map<String, Provider<RetrieverFactory>> retrieverFactoryMap;
	
	/**
	 * Create a new MapBoundRetrieverMap using the Guice provided binding map
	 *
	 * @param retrieverFactoryMap map of the Retriever internal name to the provider for the RetrieverFactory
	 */
	@Inject
	public MapBoundRetrieverMap(Map<String, Provider<RetrieverFactory>> retrieverFactoryMap) {
		super();
		
		this.retrieverFactoryMap = retrieverFactoryMap;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.retrieval.RetrieverMap#getRetrieverFactory(java.lang.String)
	 */
	@Override
	public RetrieverFactory getRetrieverFactory(String key) {
		return retrieverFactoryMap.get(key).get();
	}
	
}
