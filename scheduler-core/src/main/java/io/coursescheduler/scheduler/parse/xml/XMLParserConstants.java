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
package io.coursescheduler.scheduler.parse.xml;

/**
 * This class contains a number of XML parser constants that can be used by 
 * the xml parser component implementation.
 *
 * @author Mike Reinhold
 *
 */
public class XMLParserConstants {
	
	/**
	 * Element name query to find all element groups in the source document
	 * 
	 * Value: {@value}
	 */
	public static final String GROUP_LIST_PROPERTY = "group-list";
	
	/**
	 * Element name query to find all nodes in the source document that match
	 * the element ID passed into the query via the Element ID substitution 
	 * placeholder ({@link #ELEMENT_ID_VARIABLE}
	 * 
	 * Value: {@value}
	 */
	public static final String GROUP_ELEMENT_PROPERTY = "group-element";
	
	/**
	 * Element ID placeholder used in the {@link #GROUP_ELEMENT_PROPERTY} expression
	 * to substitute the correct element id into the XML search expression
	 * 
	 * Value: {@value}
	 */
	public static final String ELEMENT_ID_VARIABLE = "element.id";

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
	
	/**
	 * Property for retrieving the XML element that will encapsulate data rows when regrouped
	 * 
	 * Value: {@value}
	 */
	public static final String XML_GROUPING_ELEMENT_PROPERTY = "element-top";
	
	/**
	 * Default value for the XML Grouping Element used to encapsulate data rows when regrouped
	 * 
	 * Value: {@value}
	 */
	public static final String XML_GROUPING_ELEMENT_DEFAULT = "Group";
	
	/**
	 * Property for retrieving if the XML helper should save a copy of the group XML document
	 * 
	 * Value: {@value}
	 */
	public static final String XML_GROUP_SAVE_PROPERTY = "group-doc.save";
	
	/**
	 * Property for retrieving where the XML helper should save a copy of the group XML document
	 * 
	 * Value: {@value}
	 */
	public static final String XML_GROUP_FILE_PROPERTY = "group-doc.file";
	
	/**
	 * Default value for where the XML helper should save a copy of the group XML document
	 * 
	 * Value: {@value}
	 */
	public static final String XML_GROUP_FILE_DEFAULT = "${dir.tmp}/${random.string}";
}
