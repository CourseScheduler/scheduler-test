/**
  * @(#)DefaultStrSubstitutorFactory.java
  *
  * Factory class for creating StrSubstitution instances
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Factory class for creating StrSubstitution instances
 *
 * @author Mike Reinhold
 *
 */
public class DefaultStrSubstitutorFactory extends StrSubstitutorFactory {
	
	/**
	 * Component based logger
	 */
	private final Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Create a new DefaultStrSubstitutorFactory that is capable of creating StrSubstitutors with both
	 * global and local variables for replacement
	 *
	 */
	@Inject
	public DefaultStrSubstitutorFactory(Set<GlobalSubstitutionVariableSource> globalSources) {
		super(globalSources);
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.util.variable.StrSubstitutorFactory#createSubstitutor()
	 */
	@Override
	public StrSubstitutor createSubstitutor() {
		return createSubstitutor(new HashMap<String, String>());
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.util.variable.StrSubstitutorFactory#createSubstitutor(java.util.Map)
	 */
	@Override
	public StrSubstitutor createSubstitutor(Map<String, String> localVars) {
		Set<StrLookup<String>> sources = new HashSet<>();
		
		log.debug("Using {} global variable sources for variable lookup", getGlobalSources().size());
		sources.addAll(getGlobalSources());
		
		log.debug("Using MapLookup instance for access to {} local variables", localVars.size());
		sources.add(StrLookup.mapLookup(localVars));
		
		StrSubstitutor replacer = new StrSubstitutor(new DispatchingVariableSource(sources));
		
		log.debug("Enabling recursive variable substitution");
		replacer.setEnableSubstitutionInVariables(true);
		
		return replacer;
	}
}
