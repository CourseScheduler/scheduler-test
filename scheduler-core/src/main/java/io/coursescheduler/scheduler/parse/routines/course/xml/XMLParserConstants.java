/**
  * @(#)XMLParserConstants.java
  *
  * This class contains a number of XML parser constants that can be used by 
  * the xml parser component implementation.
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
package io.coursescheduler.scheduler.parse.routines.course.xml;

/**
 * This class contains a number of XML parser constants that can be used by 
 * the xml parser component implementation.
 *
 * @author Mike Reinhold
 *
 */
public class XMLParserConstants {
	
	/**
	 * Course name query to find all course names in the source document
	 * 
	 * Value: {@value}
	 */
	public static final String COURSE_NAME_FULL_LIST_PROPERTY = "query-all";
	
	/**
	 * Course name query to find all nodes in the source document that match
	 * the course ID passed into the query via the Course ID substitution 
	 * placeholder ({@link #COURSE_ID_VARIABLE}
	 * 
	 * Value: {@value}
	 */
	public static final String COURSE_NAME_SINGLE_PROPERTY = "query-single";
	
	/**
	 * Course ID placeholder used in the {@link #COURSE_NAME_SINGLE_PROPERTY} expression
	 * to substitute the correct course id into the XML search expression
	 * 
	 * Value: {@value}
	 */
	public static final String COURSE_ID_VARIABLE = "${course.id}";

	/**
	 * Property for retrieving the ParserTool to use for extracting data from the data source
	 * 
	 * Value: {@value}
	 */
	public static final String PARSER_TOOL_PROPERTY = "xml-parser";
	
	/**
	 * Property for retrieving the Parser Helper Routine that does the organization specific extraction
	 * 
	 * Value: {@value}
	 */
	public static final String PARSER_HELPER_PROPERTY = "xml-helper";
}
