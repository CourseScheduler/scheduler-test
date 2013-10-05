/**
  * @(#)StrSubstitutorFactory.java
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
package io.coursescheduler.util.text;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Describe this type
 *
 * @author Mike Reinhold
 *
 */
public class StrSubstitutorFactory {
	
	/**
	 * Component based logger
	 */
	private final Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Map of the global variables that this factory will use when constructing Substitutors.
	 * This map is fixed at time of StrSubstitutorFactory creation.
	 */
	private Map<String, String> globalVars;
	
	/**
	 * Create a new StrSubstitutorFactory that is capable of creating StrSubstitutors with both
	 * global and local variables for replacement
	 *
	 */
	//TODO inject this class and build the global vars
	public StrSubstitutorFactory() {
		super();
		
		//TODO add parameters for global variable sources
	}
	
	/**
	 * Create a new String Substitutor that uses only global variables for replacement
	 *
	 * @return a StrSubstitutor that has mappings for global variables 
	 */
	public StrSubstitutor createSubstitutor() {
		return createSubstitutor(null);
	}
	
	/**
	 * Create a new String Substitutor that uses both global and locally provided variables
	 * for replacement
	 *
	 * @param localVars the local variables that should be included
	 * @return a StrSubstitutor that has mappings for global and local variables
	 */
	public StrSubstitutor createSubstitutor(Map<String, String> localVars) {
		Map<String, String> vars = new HashMap<String, String>();
		
		log.debug("Adding {} global variables to the string substitutor", globalVars.size());
		vars.putAll(globalVars);
		
		log.debug("Adding {} local variables to the string substitutor", localVars.size());
		vars.putAll(localVars);
		
		StrSubstitutor replacer = new StrSubstitutor(vars);
		
		log.debug("Enabling recursive variable substitution");
		replacer.setEnableSubstitutionInVariables(true);
		
		return replacer;
	}
}
