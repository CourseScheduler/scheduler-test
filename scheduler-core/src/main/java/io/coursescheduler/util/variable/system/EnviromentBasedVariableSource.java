/**
  * @(#)EnviromentBasedVariableSource.java
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
package io.coursescheduler.util.variable.system;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coursescheduler.util.variable.GlobalSubstitutionVariableSource;

/**
 * A Global Substitution Variable Source that retrieves environment variables for use in substitution
 *
 * @author Mike Reinhold
 *
 */
public class EnviromentBasedVariableSource implements GlobalSubstitutionVariableSource {
	
	/**
	 * A string that is prepended to the property name in order to ensure that other global
	 * variables are not aliased or overwritten by these properties. 
	 */
	private static final String VARIABLE_PREFIX = "env";
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.util.variable.SubstitutionVariableSource#getVariableMap()
	 */
	@Override
	public Map<String, String> getVariableMap() {
		Map<String, String> variables = new HashMap<>();
		Map<String, String> env = System.getenv();
		
		log.debug("Building System Property variable map");
		for(String property : env.keySet()) {
			String newKey = VARIABLE_PREFIX + "." + property;
			String value = env.get(property);
			
			log.trace("Adding Environment Variable {} to the system global variable map as {} with value {}", new Object[] {
				property, newKey, value
			});
			variables.put(newKey, value);
		}
		log.debug("Environment Variable map built with {} entries", variables.size());
		
		return variables;
	}
	
}
