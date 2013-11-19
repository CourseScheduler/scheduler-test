/**
  * @(#)XMLParserHelperRoutineFactory.java
  *
  * Factory class for building helper XML parsing routines
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

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.prefs.Preferences;

import org.w3c.dom.Node;

import com.google.inject.assistedinject.Assisted;

import io.coursescheduler.scheduler.parse.ParserRoutineFactory;

/**
 * Factory class for building helper XML parsing routines
 *
 * @author Mike Reinhold
 *
 */
public interface XMLParserHelperRoutineFactory extends ParserRoutineFactory {
	
	/**
	 * Create a new XMLParserHelperRoutine which will process the specified list of nodes,
	 * using the Preferences node, for the identified element, and place the results into the data
	 * map provided
	 *
	 * @param nodeList the list of XML Document nodes to process
	 * @param settings the Preferences node corresponding to this parser routine's configuration
	 * @param elementid the element ID of the element being processed
	 * @param context the data value name context
	 * @param data the thread safe map in which the routine will place the data
	 * @return the Helper XML Parser Routine
	 */
	public XMLParserHelperRoutine createParserRoutine(@Assisted("nodes") List<Node> nodeList, @Assisted("settings")Preferences settings, @Assisted("elementid") String courseID, @Assisted("context") String context, @Assisted("data") ConcurrentMap<String, String> data);
}
