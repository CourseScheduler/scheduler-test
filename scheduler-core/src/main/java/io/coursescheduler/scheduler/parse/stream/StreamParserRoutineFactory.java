/**
  * @(#)StreamParserRoutineFactory.java
  *
  * Factory interface for creating Stream Parser routines
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
package io.coursescheduler.scheduler.parse.stream;

import java.io.InputStream;
import java.util.prefs.Preferences;

import com.google.inject.assistedinject.Assisted;

import io.coursescheduler.scheduler.parse.ParserRoutine;
import io.coursescheduler.scheduler.parse.ParserRoutineFactory;

/**
 * Factory interface for creating Stream Parser routines
 *
 * @author Mike Reinhold
 *
 */
public interface StreamParserRoutineFactory extends ParserRoutineFactory{

	/**
  	 * Create a new ParserRoutine intended to parse the specified input stream using settings contained
 	 * in the specified {@link java.util.prefs.Preferences} node corresponding to the ParserRoutine profile
 	 *
 	 * @param input the input stream containing the source data to parse
 	 * @param profile the Preferences node containing the settings for the ParserRoutine
	 */
	public ParserRoutine createParserRoutine(@Assisted("source") InputStream input, @Assisted("profile") Preferences profile);
}
