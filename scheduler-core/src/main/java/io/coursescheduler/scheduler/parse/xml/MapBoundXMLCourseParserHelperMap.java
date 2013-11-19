/**
  * @(#)MapBoundXMLCourseParserHelperMap.java
  *
  * Default ParserTool mapping class for retrieving registered ParserTool instances based on
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
package io.coursescheduler.scheduler.parse.xml;

import java.util.Map;

import com.google.inject.Inject;

/**
 * Default ParserTool mapping class for retrieving registered ParserTool instances based on
 * the implementation key. This implementation uses the MapBinding characteristics of the ParserTools and  
 * their providers in order to provide the keyed retrieval methods.
 *
 * @author Mike Reinhold
 *
 */
public class MapBoundXMLCourseParserHelperMap implements XMLParserHelperMap {
	
	/**
	 * Map of the parser routine internal name to the XML Course Parser Helper routine factories 
	 */
	Map<String, XMLParserHelperRoutineFactory> factories;
	
	/**
	 * Create a new MapBoundXMLCourseParserHelperMap instance containing maps of the XMLParserHelperRoutine
	 * internal names to the factory instances that create those Parser Helpers
	 *
	 * @param factories the map of internal names to 
	 */
	@Inject
	public MapBoundXMLCourseParserHelperMap(Map<String, XMLParserHelperRoutineFactory> factories) {
		super();
		
		this.factories = factories;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParserHelperMap#getXMLCourseParserHelperRoutineFactory(java.lang.String)
	 */
	@Override
	public XMLParserHelperRoutineFactory getXMLCourseParserHelperRoutineFactory(String key) {
		return factories.get(key);
	}
}
