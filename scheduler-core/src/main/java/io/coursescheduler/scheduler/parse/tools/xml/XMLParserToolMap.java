/**
  * @(#)XMLParserToolMap.java
  *
  * Extended ParserTool mapping interface for retrieving registered ParserTool instances based on
  * the implementation key. This allows for other classes that need a ParserTool instance to be
  * decoupled from the specific binding organization of the ParserTools. This adds a XMLParserTool
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
package io.coursescheduler.scheduler.parse.tools.xml;

import com.google.inject.ImplementedBy;

import io.coursescheduler.scheduler.parse.ParserToolMap;
import io.coursescheduler.scheduler.parse.query.QueryBasedParserToolMap;

/**
 * Extended ParserTool mapping interface for retrieving registered ParserTool instances based on
 * the implementation key. This allows for other classes that need a ParserTool instance to be
 * decoupled from the specific binding organization of the ParserTools. This adds a XMLParserTool
 * specific retrieval method.
 * 
 * Default implementation is {@link MapBoundXMLParserToolMap} however
 * this can be overridden in a module by binding an alternative implementation
 *
 * @author Mike Reinhold
 *
 */
@ImplementedBy(MapBoundXMLParserToolMap.class)
public interface XMLParserToolMap extends QueryBasedParserToolMap {

	/**
	 * Get a XML ParserTool for extracting data from XML based on the ParserTool internal name
	 *
	 * @param key the internal name of the ParserTool 
	 * @return a ParserTool instance for the internal name provided
	 */
	public XMLParserTool getXMLParserTool(String key);
}
