/**
  * @(#)ScriptParserTool.java
  *
  * A generic interface for parsing data using scripts. Most methods accept
  * a {@link java.util.prefs.Preferences} node containing implementation specific configuration
  * elements. Consult the documentation for the specific ScriptParserTool implementation for more
  * information on the content of the Preferences node. 
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

import java.util.Map;
import java.util.prefs.Preferences;

import io.coursescheduler.scheduler.parse.tools.ParserTool;

/**
 * A generic interface for parsing data using scripts. Most methods accept
 * a {@link java.util.prefs.Preferences} node containing implementation specific configuration
 * elements. Consult the documentation for the specific ScriptParserTool implementation for more
 * information on the content of the Preferences node. 
 * 
 * Implementations are not required to be thread-safe and as such should not be shared between threads.
 *
 * @author Mike Reinhold
 *
 */
public interface ScriptParserTool extends ParserTool {
	
	/**
	 * The substitution variable that the supplied source string will be substituted into
	 * 
	 * Value: {@value}
	 */
	public static final String SOURCE_STRING_VARIABLE = "source";
	
	/**
	 * Preferences node containing the scripts to execute.
	 * 
	 * Value: {@value}
	 */
	public static final String SCRIPT_PREFERENCES_NODE = "_script";
	
	/**
	 * Default script if no script is defined. Generally defined as a simple replacement of the source value
	 * 
	 * Value: {@value}
	 */
	public static final String SCRIPT_DEFAULT = "${" + SOURCE_STRING_VARIABLE + "}";
	
	/**
	 * Retrieve the script configured for the specified key from the Preferences node and execute it
	 *
	 * @param settings the preferences node containing the scripts to execute
	 * @param key the preferences property indicating the script to execute
	 * @return the result of the script execution as a string
	 */
	public String executeScript(Preferences settings, String key);
	
	/**
	 * Retrieve the script configured for the specified key from the Preferences node and execute it,
	 * replacing any substitution variables based on the specified map
	 *
	 * @param settings the preferences node containing the scripts to execute
	 * @param key the preferences property indicating the script to execute
	 * @param replacements map of variable names to values that can also be substituted into the script
	 * @return the result of the script execution as a string
	 */
	public String executeScript(Preferences settings, String key, Map<String, String> replacements);
	
	/**
	 * Retrieve the script configured for the specified key from the Preferences node and execute it using the
	 * specified source string
	 *
	 * @param source the source string to replace into the script wherever {@link #SOURCE_STRING_VARIABLE} is found
	 * @param settings the preferences node containing the scripts to execute
	 * @param key the preferences property indicating the script to execute
	 * @return the result of the script execution as a string
	 */
	public String executeScriptOnString(String source, Preferences settings, String key);

	/**
	 * Retrieve the script configured for the specified key from the Preferences node and execute it using the
	 * specified source string, also replacing other substitution variables based on the specified map
	 *
	 * @param source the source string to replace into the script wherever {@link #SOURCE_STRING_VARIABLE} is found
	 * @param settings the preferences node containing the scripts to execute
	 * @param key the preferences property indicating the script to execute
	 * @param replacements map of variable names to values that can also be substituted into the script
	 * @return the result of the script execution as a string
	 */
	public String executeScriptOnString(String source, Preferences settings, String key, Map<String, String> replacements);
}
