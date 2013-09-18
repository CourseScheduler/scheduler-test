/**
  * @(#)TextCourseParserRoutineFactory.java
  *
  * Factory interface for Text Course parser routines
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
package io.coursescheduler.scheduler.parse.routines.course.text;

import java.io.InputStream;
import java.util.prefs.Preferences;

import com.google.inject.assistedinject.Assisted;

import io.coursescheduler.scheduler.parse.routines.CourseParserRoutineFactory;

/**
 * Factory interface for Text Course parser routines
 *
 * @author Mike Reinhold
 *
 */
public interface TextCourseParserRoutineFactory extends	CourseParserRoutineFactory {

	/**
	 * Parser Routine internal name used in configuration and in binding to uniquely identify
	 * the parser module. It must be unique among all other Parser Routine modules or else it
	 * will not be available for use by data retrieval routines.
	 * 
	 * Value: {@value}
	 */
	public static final String PARSER_ROUTINE_INTERNAL_NAME = "course-text";
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.CourseParserRoutineFactory#createSectionBasedParser(java.io.InputStream, java.util.prefs.Preferences)
	 */
	@Override
	public TextCourseParserRoutine createParserRoutine(@Assisted("source") InputStream input, @Assisted("profile") Preferences profile);
	
}
