/**
  * @(#)MapBoundScriptParserToolMap.java
  *
  * Default ScriptParserTool mapping class for retrieving registered ParserTool instances based on
  * the implementation key. This implementation uses the MapBinding characteristics of the ParserTools and  
  * their providers in order to provide the keyed retrieval methods.
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

import com.google.inject.Inject;
import com.google.inject.Provider;

import io.coursescheduler.scheduler.parse.tools.MapBoundParserToolMap;
import io.coursescheduler.scheduler.parse.tools.ParserTool;

/**
 * Default ScriptParserTool mapping class for retrieving registered ParserTool instances based on
 * the implementation key. This implementation uses the MapBinding characteristics of the ParserTools and  
 * their providers in order to provide the keyed retrieval methods.
 *
 * @author Mike Reinhold
 *
 */
public class MapBoundScriptParserToolMap extends MapBoundParserToolMap implements ScriptParserToolMap {

	/**
	 * Map of the Parser internal name to a Provider for the ScriptParserTool
	 */
	private Map<String, Provider<ScriptParserTool>> scriptParserProviders;
	
	/**
	 * Create a new MapBoundParserToolMap instance containing maps of the ParserTool internal names
	 * to the Guice Providers that create those ParserTools
	 *
	 * @param parserProviders the map of ParserTool internal names to ParserTool Providers
	 * @param scriptParserProviders the map of ScriptParserTool internal names to ScriptParserTool Providers
	 */
	@Inject
	public MapBoundScriptParserToolMap(Map<String, Provider<ParserTool>> parserProviders, Map<String, Provider<ScriptParserTool>> scriptParserProviders) {
		super(parserProviders);
		
		this.scriptParserProviders = scriptParserProviders;
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.script.ScriptParserToolMap#getScriptParserTool(java.lang.String)
	 */
	@Override
	public ScriptParserTool getScriptParserTool(String key) {
		return scriptParserProviders.get(key).get();
	}
	
}
