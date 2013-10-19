/**
  * @(#)RetrieverMap.java
  *
  * RetrieverFactory mapping class that allows access to a RetrieverFactory based on the
  * internal name of the Retriever implementation.
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

import com.google.inject.ImplementedBy;

/**
 * RetrieverFactory mapping class that allows access to a RetrieverFactory based on the
 * internal name of the Retriever implementation.
 * 
 * Default implementation is {@link io.coursescheduler.retrieval.MapBoundRetrieverMap}. This can be
 * overridden by binding an alternate implementation in a Guice module.
 *
 * @author Mike Reinhold
 *
 */
@ImplementedBy(MapBoundRetrieverMap.class)
public interface RetrieverMap {
	
	/**
	 * Create a RetrieverFactory based on the internal Retriever name corresponding to the Retriever.
	 *
	 * @param key the internal name of the Retriever class for which a factory instance should be returned
	 * @return a RetrieverFactory for the class specified by the internal name
	 */
	public RetrieverFactory getRetrieverFactory(String key);
	
	/**
	 * Create an EphemeralRetrieverFactory based on the internal Retriever name corresponding to the Retriever.
	 * Ephemeral Retrievers do not perform any persisting of the retrieved data, but instead provide a method 
	 * for subsequent tasks to access the retrieved data.
	 *
	 * @param key the internal name of the Retriever class for which a factory instance should be returned
	 * @return an EphemeralRetrieverFactory for the class specified by the internal name
	 */
	public EphemeralRetrieverFactory getEphemeralRetrieverFactory(String key);
	
	/**
	 * Create a PersistentRetrieverFactory based on the internal Retriever name corresponding to the Retriever.
	 * Persistent Retrievers persist the retrieved data using on of several persisting options.
	 *
	 * @param key the internal name of the Retriever class for which a factory instance should be returned
	 * @return a PersistentRetrieverFactory for the class specified by the internal name
	 */
	public PersistentRetrieverFactory getPersistentRetrieverFactory(String key);
}
