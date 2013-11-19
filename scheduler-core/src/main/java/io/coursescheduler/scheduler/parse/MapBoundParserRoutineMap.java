/**
  * @(#)ParserRoutineMap.java
  *
  * Default ParserRoutineFactory mapping class for retrieving registered ParserRoutineFactory instances based on
  * the implementation key. This implementation uses the MapBinding characteristics of the ParserRoutines and  
  * their factories in order to provide the keyed factory retrieval methods.
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
package io.coursescheduler.scheduler.parse;

import io.coursescheduler.scheduler.parse.course.CourseParserRoutineFactory;
import io.coursescheduler.scheduler.parse.stream.StreamParserRoutineFactory;

import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Default ParserRoutineFactory mapping class for retrieving registered ParserRoutineFactory instances based on
 * the implementation key. This implementation uses the MapBinding characteristics of the ParserRoutines and  
 * their factories in order to provide the keyed factory retrieval methods.
 *
 * @author Mike Reinhold
 *
 */
public class MapBoundParserRoutineMap implements ParserRoutineMap {
	
	/**
	 * Map of ParserRoutineFactories based on the ParserRoutine internal name
	 */
	private Map<String, Provider<ParserRoutineFactory>> parserFactories; 
	
	/**
	 * Map of the CourseParserRoutineFactories based on the ParserRoutine internal name
	 */
	private Map<String, Provider<CourseParserRoutineFactory>> courseParserFactories;
	
	/**
	 * Map of the StreamParserRoutineFactories based on the ParserRoutine internal name
	 */
	private Map<String, Provider<StreamParserRoutineFactory>> streamParserFactories;
	
	/**
	 * Create a new ParserRoutineMap containing Maps of the parser routine names to the factory instances that
	 * create parser routine instances of the specified type
	 *
	 * @param parserFactories map of internal names to factories for general purpose parser routines
	 * @param courseParserFactories map of internal names to factories for course data parser routines
	 * @param streamParserFactories map of internal names to factories for stream data parser routines
	 */
	@Inject
	public MapBoundParserRoutineMap(Map<String, Provider<ParserRoutineFactory>> parserFactories, Map<String, Provider<CourseParserRoutineFactory>> courseParserFactories, Map<String, Provider<StreamParserRoutineFactory>> streamParserFactories) {
		super();
		
		this.parserFactories = parserFactories;
		this.courseParserFactories = courseParserFactories;
		this.streamParserFactories = streamParserFactories;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.ParserRoutineMap#getParserRoutineFactory(java.lang.String)
	 */
	@Override
	public ParserRoutineFactory getParserRoutineFactory(String key) {
		return parserFactories.get(key).get();
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.ParserRoutineMap#getCourseParserRoutineFactory(java.lang.String)
	 */
	@Override
	public CourseParserRoutineFactory getCourseParserRoutineFactory(String key) {
		return courseParserFactories.get(key).get();
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.ParserRoutineMap#getStreamParserRoutineFactory(java.lang.String)
	 */
	@Override
	public StreamParserRoutineFactory getStreamParserRoutineFactory(String key) {
		return streamParserFactories.get(key).get();
	}
	
}
