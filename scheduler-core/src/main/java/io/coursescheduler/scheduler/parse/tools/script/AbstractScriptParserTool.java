/**
  * @(#)AbstractScriptParserTool.java
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
package io.coursescheduler.scheduler.parse.tools.script;

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
public abstract class AbstractScriptParserTool implements ScriptParserTool {
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * String substitution factory for creating substitutor instances
	 */
	private StrSubstitutorFactory substFactory;
	
	/**
	 * Create a new AbstractScriptParserTool using the specified substitutor factory
	 *
	 * @param substFactory factory for creating StrSubstitutor instances
	 */
	@Inject
	public AbstractScriptParserTool(StrSubstitutorFactory substFactory) {
		super();
		
		this.substFactory = substFactory;
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.script.ScriptParserTool#executeScript(java.util.prefs.Preferences, java.lang.String)
	 */
	@Override
	public String executeScript(Preferences settings, String key) {
		log.debug("Preparing to execute script for key {} from {}", key, settings);
		Map<String, String> replacements = new HashMap<>();
		String source = "";
		log.debug("Adding source string {} to replacements map at key {}", source, SOURCE_STRING_VARIABLE);
		replacements.put(SOURCE_STRING_VARIABLE, source);
		return executeScript(settings, key, replacements);
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.script.ScriptParserTool#executeScript(java.util.prefs.Preferences, java.lang.String, java.util.Map)
	 */
	@Override
	public String executeScript(Preferences settings, String key, Map<String, String> replacements) {
		log.debug("Preparing to execute script for key {} from {} using replacements map {}", new Object[] {key, settings, replacements});
		
		log.debug("Retrieving script for key {}", key);
		String script = settings.node(SCRIPT_PREFERENCES_NODE).get(key, SCRIPT_DEFAULT);
		log.debug("Found script for key {}: {}", key, script);
		
		log.debug("Preparing variable replacer");
		StrSubstitutor replacer = substFactory.createSubstitutor(replacements);
		log.debug("Replacing variables in script {}", script);
		script = replacer.replace(script);
		log.debug("Variable replacement updated script {}", script);
		
		long start = System.currentTimeMillis();
		log.info("Executing script {}");
		Object result = executeScript(script, settings);
		long end = System.currentTimeMillis();
		log.info("Finished executing script in {} ms. Found result {}", end - start, result);
		
		return result.toString();		
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.script.ScriptParserTool#executeScriptOnString(java.lang.String, java.util.prefs.Preferences, java.lang.String)
	 */
	@Override
	public String executeScriptOnString(String source, Preferences settings, String key) {
		log.debug("Preparing to execute script for key {} from {} using source string {}", new Object[] {key, settings, source});
		Map<String, String> replacements = new HashMap<>();
		log.debug("Adding source string {} to replacements map at key {}", source, SOURCE_STRING_VARIABLE);
		replacements.put(SOURCE_STRING_VARIABLE, source);
		return executeScript(settings, key, replacements);
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.script.ScriptParserTool#executeScriptOnString(java.lang.String, java.util.prefs.Preferences, java.lang.String, java.util.Map)
	 */
	@Override
	public String executeScriptOnString(String source, Preferences settings, String key, Map<String, String> replacements) {
		log.debug("Preparing to execute script for key {} from {} using source string {} and replacements map {}", new Object[] {key, settings, source, replacements});
		Map<String, String> values = new HashMap<>();
		values.putAll(replacements);
		log.debug("Adding source string {} to replacements map at key {}", source, SOURCE_STRING_VARIABLE);
		values.put(SOURCE_STRING_VARIABLE, source);
		return executeScript(settings, key, values);
	}
	
	/**
	 * Execute the script using the script specific functions
	 *
	 * @param script script string to execute
	 * @param settings configuration for the script parser tool
	 * @return the result of the script execution
	 */
	protected abstract Object executeScript(String script, Preferences settings);
	
}
