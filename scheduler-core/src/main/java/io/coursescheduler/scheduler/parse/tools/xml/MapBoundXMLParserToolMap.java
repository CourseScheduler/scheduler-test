/**
  * @(#)MapBoundXMLParserToolMap.java
  *
  * Default XMLParserTool mapping class for retrieving registered ParserTool instances based on
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
package io.coursescheduler.scheduler.parse.tools.xml;

import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;

import io.coursescheduler.scheduler.parse.tools.MapBoundParserToolMap;
import io.coursescheduler.scheduler.parse.tools.ParserTool;

/**
 * Default XMLParserTool mapping class for retrieving registered ParserTool instances based on
 * the implementation key. This implementation uses the MapBinding characteristics of the ParserTools and  
 * their providers in order to provide the keyed retrieval methods.
 *
 * @author Mike Reinhold
 *
 */
public class MapBoundXMLParserToolMap extends MapBoundParserToolMap implements XMLParserToolMap{
	
	/**
	 * Map of ParseTool internal names to the Guice Providers used to create instances of the XMLParserTool
	 */
	private Map<String, Provider<XMLParserTool>> xmlParserProviders;
	
	/**
	 * Create a new MapBoundParserToolMap instance containing maps of the ParserTool internal names
	 * to the Guice Providers that create those ParserTools
	 *
	 * @param parserProviders the map of ParserTool internal names to ParserTool Providers
	 * @param xmlParserProviders the map of ParserTool internal names to XMLParserTool Providers
	 */
	@Inject
	public MapBoundXMLParserToolMap(Map<String, Provider<ParserTool>> parserProviders, Map<String, Provider<XMLParserTool>> xmlParserProviders) {
		super(parserProviders);
		this.xmlParserProviders = xmlParserProviders;
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.ParserToolMap#getXMLParserTool(java.lang.String)
	 */
	@Override
	public XMLParserTool getXMLParserTool(String key) {
		try{
			return xmlParserProviders.get(key).get();
		}catch(NullPointerException e) {
			return null;
		}
	}
}
