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

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;

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
	 * The map of namespaces to sources that provide access to Global Variables for substitution purposes
	 */
	private Set<GlobalSubstitutionVariableSource> globalSources;

	/**
	 * Create a new StrSubstitutorFactory using the specified sources for global variables
	 *
	 */
	@Inject
	public StrSubstitutorFactory(Set<GlobalSubstitutionVariableSource> globalSources) {
		super();

		this.globalSources = globalSources;
	}
	
	/**
	 * Return the map of sources for global variables.
	 * 
	 * @return the global variable sources
	 */
	protected Set<GlobalSubstitutionVariableSource> getGlobalSources() {
		return globalSources;
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
	
	/**
	 * Create a new String Substitutor that uses both global and locally provided variables
	 * for replacement
	 *
	 * @param localSources sources for local variables that should be included
	 * @return a StrSubstitutor that has mappings for global and local variables
	 */
	public abstract StrSubstitutor createSubstitutor(Set<StrLookup<String>> localSources);
	
}
