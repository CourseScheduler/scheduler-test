/**
  * @(#)ScriptEngineMap.java
  *
  * ScriptEngine mapping interface for retrieving registered ScriptEngine instances based on
  * the implementation key. This allows for other classes that need a ScriptEngine instance to be
  * decoupled from the specific binding organization of the ScriptEngines. This adds a ScriptEngine
  * specific retrieval method.
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

import java.util.prefs.Preferences;

import com.google.inject.ImplementedBy;

/**
 * ScriptEngine mapping interface for retrieving registered ScriptEngine instances based on
 * the implementation key. This allows for other classes that need a ScriptEngine instance to be
 * decoupled from the specific binding organization of the ScriptEngines. This adds a ScriptEngine
 * specific retrieval method.
 * 
 * Default implementation is {@link MapBoundScriptEngineMap} however
 * this can be overridden in a module by binding an alternative implementation
 *
 * @author Mike Reinhold
 *
 */
@ImplementedBy(MapBoundScriptEngineMap.class)
public interface ScriptEngineMap {
	
	/**
	 * Get a ScriptEngine for executing scripts
	 *
	 * @param key the engine internal name
	 * @param settings the preferences node containing the configuration for the script engine
	 * @return a script engine whose type is determined by the engine key
	 */
	public ScriptEngine getScriptEngine(String key, Preferences settings);
	
	/**
	 * Get a ScriptEngine factory for creating ScriptEngines
	 *
	 * @param key the engine internal name
	 * @return a script engine whose type is determined by the engine key
	 */
	public ScriptEngineFactory getScriptEngineFactory(String key);
}
