/**
  * @(#)CourseParserRoutine.java
  *
  * Interface for describing Course Parser routines and the methods that must be supported
  * by said parse routines.
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
package io.coursescheduler.scheduler.parse.routines.course;

import io.coursescheduler.scheduler.parse.routines.StreamParserRoutine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interface for describing general Course parser routines. Parser routines that only work with
 * source data organized by a particular data type should subclass the correct child class based
 * on the expected data format. For instance, course parser routines that expect the input stream
 * to be formatted based on the meeting time information (where each row / line / element / 
 * organizational unit is a meeting time) should subclass  {@link MeetingBasedCourseParserRoutine}
 * while parser routiness that work with data organized by the course id should subclass 
 * {@link CourseBasedCourseParserRoutine}.
 * 
 * If the parser routine is capable of properly processing data organized by any level of the 
 * course data, course, section, or meeting, it should subclass this class ({@link CourseParserRoutine})
 * to indicate that any level of processing is allowed. This will be reflected in configuration that
 * the subclass is capable of processing an input stream formatted based on any level of the course
 * data.
 *
 * @author Mike Reinhold
 *
 */
public abstract class CourseParserRoutine extends StreamParserRoutine {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Map of the course id to the map of course data (represented by the course data element 
	 * indexed-name and the corresponding value)
	 */
	private Map<String, Map<String, String>> courseDataSets;
	
	/**
	 * Construct a new CourseParserRoutine and initialize the course data set map
	 *
	 */
	protected CourseParserRoutine() {
		courseDataSets = new ConcurrentHashMap<>();
	}

	/**
	 * Return the map of course data sets. This map is guaranteed to be thread safe, although
	 * it is up to the the users of the course data set map to ensure that the inner maps stored
	 * against the course ID is also thread safe.
	 * 
	 * @return the courseDataSets map of course IDs to map of course data elements and values
	 */
	public Map<String, Map<String, String>> getCourseDataSets() {
		return courseDataSets;
	}

}
