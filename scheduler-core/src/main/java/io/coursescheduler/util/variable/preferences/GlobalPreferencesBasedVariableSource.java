/**
  * @(#)GlobalPreferencesBasedVariableSource.java
  *
  * GlobalPreferencesBasedVariableSource for retrieving values from  other preferences nodes. This
  * class only does absolute preferences node referencing (unlike the PreferencesBasedVariableSource
  * which must have the preferences node provided at runtime and only allows for local property
  * references).
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
package io.coursescheduler.util.variable.preferences;

import java.util.prefs.Preferences;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.coursescheduler.util.preferences.PreferencesFactory;
import io.coursescheduler.util.variable.GlobalSubstitutionVariableSource;

/**
 * GlobalPreferencesBasedVariableSource for retrieving values from  other preferences nodes. This
 * class only does absolute preferences node referencing (unlike the PreferencesBasedVariableSource
 * which must have the preferences node provided at runtime and only allows for local property
 * references).
 *
 * @author Mike Reinhold
 *
 */
public class GlobalPreferencesBasedVariableSource extends GlobalSubstitutionVariableSource {
	
	/**
	 * Preferences property prefix for identifying system preferences variables
	 * 
	 * Value: {@value}
	 */
	public static final String SYSTEM_PREFERENCES_VARIABLE_PREFIX = "prefs.system/";
	
	/**
	 * Preferences property prefix for identifying user preferences variables
	 * 
	 * Value: {@value}
	 */
	public static final String USER_PREFERENCES_VARIABLE_PREFIX = "prefs.user/";
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Preferences system root node
	 */
	private Preferences system;
	
	/**
	 * Preferences user root node
	 */
	private Preferences user;
	
	/**
	 * Create a new GlobalPreferencesBasedVariableSource that can perform global preferences 
	 * properties lookup
	 *
	 * @param prefsFact Preferences node factory
	 */
	@Inject
	public GlobalPreferencesBasedVariableSource(PreferencesFactory prefsFact) {
		super();
		
		system = prefsFact.getSystemNode("/");
		log.debug("Found root system preferences {}", system);
		
		user = prefsFact.getUserNode("/");
		log.debug("Found root user preferences {}", user);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.lang3.text.StrLookup#lookup(java.lang.String)
	 */
	@Override
	public String lookup(String key) {
		Preferences root;
		String relativePath;
		
		log.trace("Preparing to lookup value for key {}", key);
		
		//check if the variable is in the system or user space, or not a global preferences at all
		if(key.startsWith(SYSTEM_PREFERENCES_VARIABLE_PREFIX)) {
			log.trace("Variable key {} starts with system variable prefix {}", key, SYSTEM_PREFERENCES_VARIABLE_PREFIX);
			relativePath = key.replace(SYSTEM_PREFERENCES_VARIABLE_PREFIX, "");
			log.trace("Variable key {} uses relative path {}", key, relativePath);
			root = system;
			
		}else if(key.startsWith(USER_PREFERENCES_VARIABLE_PREFIX)){
			log.trace("Variable key {} starts with system variable prefix {}", key, USER_PREFERENCES_VARIABLE_PREFIX);
			relativePath = key.replace(USER_PREFERENCES_VARIABLE_PREFIX, "");
			log.trace("Variable key {} uses relative path {}", key, relativePath);
			root = user;
			
		}else {
			log.trace("Variable key {} does not start with either system ({}) or user ({}) preferences prefixes", new Object[] {
					key, SYSTEM_PREFERENCES_VARIABLE_PREFIX, USER_PREFERENCES_VARIABLE_PREFIX
			});
			log.debug("Variable key {} not found in lookup {}, using null", key, this);
			return null;
		}

		//split out the path into the node path and the variable name
		String nodeName = relativePath.substring(0, relativePath.lastIndexOf('/'));
		log.trace("Found node path {} for key {}", nodeName, key);
		String variableName = relativePath.substring(relativePath.lastIndexOf('/')+1);
		log.trace("Found variable name {} for key {}", variableName, key);
		
		//access the correct node and retrieve the variable value
		Preferences node = root.node(nodeName);
		log.trace("Accessing node {} to find key {}", node, key);
		String variable = node.get(variableName, null);
		log.debug("Found value {} for key {}", variable, key);
		
		//since the preferences variable may have a local preferences property reference 
		//(aka without an absolute node path, just the property name), we need to build
		//a local substitutor that only uses local properties for replacement. Other global
		//variables will be automatically filled in by the calling StrSubstitutor
		log.trace("Building local substitutor using {} to perform preferences node local replacements", node);
		StrSubstitutor local = new StrSubstitutor(new PreferencesBasedVariableSource(node));
		variable = local.replace(variable);
		log.debug("Found value {} for key {} after performing local replacements", variable, key);
		
		return variable;
	}
	
}
