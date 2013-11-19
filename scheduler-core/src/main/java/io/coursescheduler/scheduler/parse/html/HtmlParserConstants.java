/**
  * @(#)HtmlParserConstants.java
  *
  * This class contains a number of constants that are used by HTML parser routines and helpers
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
package io.coursescheduler.scheduler.parse.html;

/**
 * This class contains a number of constants that are used by HTML parser routines and helpers 
 *
 * @author Mike Reinhold
 *
 */
public class HtmlParserConstants {

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
	 * to substitute the correct element id into the HTML search expression
	 * 
	 * Value: {@value}
	 */
	public static final String ELEMENT_ID_VARIABLE = "element.id";
}
