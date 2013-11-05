/**
  * @(#)PreferencesBasedVariableSource.java
  *
  * Use a Preferences node as the variable source
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.coursescheduler.util.variable.SubstitutionVariableSource;

import static io.coursescheduler.util.variable.preferences.PreferencesSourceConstants.*;

/**
 * Use a Preferences node as the variable source
 *
 * @author Mike Reinhold
 *
 */
public class PreferencesBasedVariableSource extends SubstitutionVariableSource {
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Preferences node containing the variables
	 */
	private Preferences node;
	
	/**
	 * Create a new Preferences Variable Source based on the specified preferences node
	 *
	 * @param node the backing preferences node
	 */
	@AssistedInject
	public PreferencesBasedVariableSource(@Assisted("config") Preferences node) {
		super();
		
		this.node = node;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.lang3.text.StrLookup#lookup(java.lang.String)
	 */
	@Override
	public String lookup(String variable) {
		String value;
		
		if(variable.contains(PREFERENCES_NODE_PATH_SEPARATOR)) {
			log.debug("Checking for variable {} relative to Preferences node {}", variable, node.absolutePath());
			String path = variable.substring(0, variable.lastIndexOf(PREFERENCES_NODE_PATH_SEPARATOR));
			log.trace("Found node path {} relative to {}", path, node.absolutePath());
			String key = variable.substring(variable.lastIndexOf(PREFERENCES_NODE_PATH_SEPARATOR)+1);
			log.trace("Found variable name {} to be searched in relative node", key);
			
			Preferences relative = node.node(path);
			log.trace("Found relative node {}", relative);
			value = relative.get(key, null);
			log.debug("Preferences node {} contained {} for variable {}", new Object[] {relative, value, key});
		}else {
			log.debug("Checking for local variable {} in Preferences node {}", variable, node.absolutePath());
			value = node.get(variable, null);
			log.debug("Preferences node contained {} for variable {}", value, variable);
		}
		
		return value;
	}
	
}
