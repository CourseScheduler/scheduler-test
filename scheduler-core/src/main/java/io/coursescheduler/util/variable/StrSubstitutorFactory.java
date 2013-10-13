/**
  * @(#)StrSubstitutorFactory.java
  *
  * Factory interface for StrSubstitution factory classes. Used to allow for multiple implementations of 
  * a StrSubstitutorFactory.
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

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * Factory interface for StrSubstitution factory classes. Used to allow for multiple implementations of 
 * a StrSubstitutorFactory.
 * 
 * Default implementation is {@link io.coursescheduler.util.variable.DefaultStrSubstitutionFactory}. This can
 * be overridden by specifying an alternate binding in a module.
 *
 * @author Mike Reinhold
 *
 */
@ImplementedBy(DefaultStrSubstitutorFactory.class)
public abstract class StrSubstitutorFactory {
	
	/**
	 * Component based logger
	 */
	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Map of the global variables that this factory will use when constructing Substitutors.
	 * This map is fixed at time of DefaultStrSubstitutorFactory creation.
	 */
	private Map<String, String> globalVars;
	
	/**
	 * Create a new StrSubstitutorFactory using the specified sources for global variables
	 *
	 * @param globalSources the set of sources for global variables
	 */
	@Inject
	public StrSubstitutorFactory(Set<GlobalSubstitutionVariableSource> globalSources) {
		super();

		globalVars = buildGlobalVars(globalSources);
	}
	
	/**
	 * Build the global variables from the set of global variable sources
	 *
	 * @param globalSources the set of sources for global variables
	 * 
	 * @return the map of global variables
	 */
	private Map<String, String> buildGlobalVars(Set<GlobalSubstitutionVariableSource> globalSources) {
		Map<String, String> vars = new HashMap<>();
		log.debug("Preparing to build global variables");
		for(GlobalSubstitutionVariableSource source : globalSources) {
			Map<String, String> sourceVars = source.getVariableMap();
			vars.putAll(sourceVars);
			log.trace("Found global var source {} with {} entries", source, sourceVars.size());
		}
		log.debug("Built global variable map with {} entries", vars.size());
		return vars;
	}
	
	/**
	 * A map of the global variables and their values
	 *
	 * @return a map of the global variables
	 */
	protected Map<String, String> getGlobalVariables(){
		return globalVars;
	}
	
	/**
	 * Create a new String Substitutor that uses only global variables for replacement
	 *
	 * @return a StrSubstitutor that has mappings for global variables 
	 */
	public abstract StrSubstitutor createSubstitutor();
	
	/**
	 * Create a new String Substitutor that uses both global and locally provided variables
	 * for replacement
	 *
	 * @param localVars the local variables that should be included
	 * @return a StrSubstitutor that has mappings for global and local variables
	 */
	public abstract StrSubstitutor createSubstitutor(Map<String, String> localVars);
}
