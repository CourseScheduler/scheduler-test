/**
  * @(#)XMLCourseParserHelperRoutine.java
  *
  * An XML parsing routine for extracting course data from XML formatted documents. This
  * is the abstract base class for helper XML parsing routine that performs the work of
  * parsing one or more XML document nodes corresponding to a single course
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

import io.coursescheduler.scheduler.parse.routines.course.CourseParserRoutine;

/**
 * An XML parsing routine for extracting course data from XML formatted documents. This
 * is the abstract base class for helper XML parsing routine that performs the work of
 * parsing one or more XML document nodes corresponding to a single course
 *
 * @author Mike Reinhold
 *
 */
public abstract class XMLCourseParserHelperRoutine extends CourseParserRoutine {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	
}
