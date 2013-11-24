/**
  * @(#)AbstractScriptEngine.java
  *
  * Base class for script based parser tools that provides common functionality
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
package io.coursescheduler.util.script.engine;

import io.coursescheduler.util.variable.StrSubstitutorFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Base class for script based parser tools that provides common functionality
 *
 * @author Mike Reinhold
 *
 */
public abstract class AbstractScriptEngine implements ScriptEngine {
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * String substitution factory for creating substitutor instances
	 */
	private StrSubstitutorFactory substFactory;
	
	/**
	 * Create a new AbstractScriptEngine using the specified substitutor factory
	 *
	 * @param substFactory factory for creating StrSubstitutor instances
	 */
	@Inject
	public AbstractScriptEngine(StrSubstitutorFactory substFactory) {
		super();
		
		this.substFactory = substFactory;
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.util.script.engine.ScriptEngine#executeScript(java.util.prefs.Preferences, java.lang.String)
	 */
	@Override
	public String executeScript(Preferences settings, String key) {
		log.trace("Preparing to execute script for key {} from {}", key, settings);
		Map<String, String> replacements = new HashMap<>();
		String source = "";
		log.trace("Adding source string {} to replacements map at key {}", source, SOURCE_STRING_VARIABLE);
		replacements.put(SOURCE_STRING_VARIABLE, source);
		return executeScript(settings, key, replacements);
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.util.script.engine.ScriptEngine#executeScript(java.util.prefs.Preferences, java.lang.String, java.util.Map)
	 */
	@Override
	public String executeScript(Preferences settings, String key, Map<String, String> replacements) {
		log.debug("Preparing to execute script for key {} from {} using replacements map {}", new Object[] {key, settings, replacements});
		
		log.trace("Retrieving script for key {}", key);
		String script = settings.node(SCRIPT_PREFERENCES_NODE).get(key, getDefaultScript());
		log.trace("Found script for key {}: {}", key, script);
		
		log.trace("Preparing variable replacer");
		StrSubstitutor replacer = substFactory.createSubstitutor(replacements);
		log.trace("Replacing variables in script {}", script);
		script = replacer.replace(script);
		log.trace("Variable replacement updated script {}", script);
		
		long start = System.currentTimeMillis();
		log.debug("Executing script {}", script);
		Object result = executeScript(script);
		long end = System.currentTimeMillis();
		log.debug("Finished executing script in {} ms. Found result {}", end - start, result);
		
		return result.toString();		
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.util.script.engine.ScriptEngine#executeScriptOnString(java.lang.String, java.util.prefs.Preferences, java.lang.String)
	 */
	@Override
	public String executeScript(String source, Preferences settings, String key) {
		log.trace("Preparing to execute script for key {} from {} using source string {}", new Object[] {key, settings, source});
		Map<String, String> replacements = new HashMap<>();
		log.trace("Adding source string {} to replacements map at key {}", source, SOURCE_STRING_VARIABLE);
		replacements.put(SOURCE_STRING_VARIABLE, source);
		return executeScript(settings, key, replacements);
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.util.script.engine.ScriptEngine#executeScriptOnString(java.lang.String, java.util.prefs.Preferences, java.lang.String, java.util.Map)
	 */
	@Override
	public String executeScript(String source, Preferences settings, String key, Map<String, String> replacements) {
		log.trace("Preparing to execute script for key {} from {} using source string {} and replacements map {}", new Object[] {key, settings, source, replacements});
		Map<String, String> values = new HashMap<>();
		values.putAll(replacements);
		log.trace("Adding source string {} to replacements map at key {}", source, SOURCE_STRING_VARIABLE);
		values.put(SOURCE_STRING_VARIABLE, source);
		return executeScript(settings, key, values);
	}
	
	/**
	 * Execute the script using the script specific functions
	 *
	 * @param script script string to execute
	 * @return the result of the script execution
	 */
	protected abstract Object executeScript(String script);
	
	/**
	 * Get the implementation specific default script
	 *
	 * @return the default script if no other script is configured
	 */
	protected abstract String getDefaultScript();
	
}
