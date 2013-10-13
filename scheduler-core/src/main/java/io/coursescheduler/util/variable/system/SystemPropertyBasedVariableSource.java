/**
  * @(#)SystemPropertyBasedVariableSource.java
  *
  * A Global Substitution Variable Source that retrieves system properties for use in substitution
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
package io.coursescheduler.util.variable.system;

import org.apache.commons.lang3.text.StrLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coursescheduler.util.variable.GlobalSubstitutionVariableSource;

/**
 * A Global Substitution Variable Source that retrieves system properties for use in substitution
 *
 * @author Mike Reinhold
 *
 */
public class SystemPropertyBasedVariableSource extends GlobalSubstitutionVariableSource {
	
	/**
	 * A string that is prepended to the property name in order to ensure that other global
	 * variables are not aliased or overwritten by these properties. 
	 */
	private static final String VARIABLE_PREFIX = "system" + NAMESPACE_SEPARATOR;
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Internal StrLookup that contains the non-prefixed lookup values for the system  
	 * properties
	 */
	StrLookup<String> properties = StrLookup.systemPropertiesLookup();

	/* (non-Javadoc)
	 * @see org.apache.commons.lang3.text.StrLookup#lookup(java.lang.String)
	 */
	@Override
	public String lookup(String variable) {
		String value;
		if(variable.startsWith(VARIABLE_PREFIX)) {
			log.debug("Variable {} uses system properties prefix", variable);
			String sysVariable = variable.substring(VARIABLE_PREFIX.length());
			log.debug("Searching system properties for variable {}", sysVariable);
			
			value = properties.lookup(sysVariable);
			log.debug("Found value {} for system properties variable {}", value, sysVariable);
		}else{
			log.debug("Variable {} does not begin with the system properties prefix {}, returning null", variable, VARIABLE_PREFIX);
			value = null;
		}
		
		return value;
	}
	
}
