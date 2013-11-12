/**
  * @(#)DispatchingVariableSource.java
  *
  * A StrLookup class to dispatch to other StrLookup instances
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
package io.coursescheduler.util.variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.text.StrLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A StrLookup class to dispatch to other StrLookup instances
 *
 * @author Mike Reinhold
 *
 */
public class DispatchingVariableSource extends StrLookup<String> {
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	/**
	 * Set of StrLookup sources from which the variable lookup could be 
	 * fulfilled
	 */
	private Set<StrLookup<String>> sources;
	
	/**
	 * Cache of lookup sources so that repeated lookups of the same value
	 * need not traverse the entire set of sources
	 */
	private Map<String, StrLookup<String>> cache;
	
	/**
	 * Create a new Dispatching Variable Source that will dispatch variable
	 * lookups to the specified set of sources
	 *
	 * @param sources the set of StrLookup instances to use when looking up a
	 * variable
	 */
	public DispatchingVariableSource(Set<StrLookup<String>> sources) {
		super();
		
		this.sources = sources;
		
		cache = new HashMap<String, StrLookup<String>>();
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.lang3.text.StrLookup#lookup(java.lang.String)
	 */
	@Override
	public String lookup(String variable) {
		String value;
		log.debug("Looking up value for variable {}", variable);
		if(!cache.containsKey(variable)) {
			log.debug("Source for variable {} not found in cache", variable);
			value = fillCache(variable);
		}else {
			StrLookup<String> lookup = cache.get(variable);
			log.debug("Source for variable {} found in cache: {}", variable, lookup);
			value = lookup.lookup(variable);
		}
		log.debug("Lookup of {} yielded {}", variable, value);
		
		return value;
	}
	
	/**
	 * Lookup the variable in the internal StrLookup instances and add the source to the cache.
	 *
	 * @param variable the variable to find from the sources and cache the source
	 * @return the value found while filling the cache
	 */
	private String fillCache(String variable) {
		log.trace("Looking up variable {} in {} possible sources", variable, sources.size());
		for(StrLookup<String> source: sources) {
			String value = source.lookup(variable);
			
			if(value == null) {
				log.trace("Variable {} not found in source {}", variable, source);
			}else {
				log.debug("Variable {} found in source {}", variable, source);
				cache.put(variable, source);
				log.trace("Caching source {} for variable {}", source, variable);
				return value;	//exit early
			}
		}
		
		if(!cache.containsKey(variable)) {
			log.debug("Variable {} not found in sources", variable);
		}
		return null;
	}
	
}
