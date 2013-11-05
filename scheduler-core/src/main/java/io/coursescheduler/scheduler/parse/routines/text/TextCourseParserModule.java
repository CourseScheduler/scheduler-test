/**
  * @(#)TextCourseParserModule.java
  *
  * Guice module for the Text Course parser plugin binding
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
package io.coursescheduler.scheduler.parse.routines.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coursescheduler.scheduler.parse.routines.ParserRoutine;
import io.coursescheduler.scheduler.parse.routines.ParserRoutineFactory;
import io.coursescheduler.scheduler.parse.routines.course.CourseParserRoutineFactory;
import io.coursescheduler.scheduler.parse.routines.stream.StreamParserRoutineFactory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;

/**
 * Guice module for the Text Course parser plugin binding
 *
 * @author Mike Reinhold
 *
 */
public class TextCourseParserModule extends AbstractModule {

	/**
	 * Component based logger
	 */
	Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		//install a module indicating that TextCourseParserRoutine can be built from a factory with assisted inject
		log.debug("Installing FactoryModuleBuilder for {} with implementations {}",
			TextCourseParserRoutineFactory.class,
			TextCourseParserRoutine.class + " for " + ParserRoutine.class
		);
		install(new FactoryModuleBuilder()
			.implement(ParserRoutine.class, TextCourseParserRoutine.class)
			.build(TextCourseParserRoutineFactory.class)
		);
		
		//add a mapped binding from the ParseRouting class to the implementation classes
		log.debug("Creating MapBinder entry for {} to {} at key {}", new Object[] {
				ParserRoutineFactory.class,
				TextCourseParserRoutineFactory.class,
				TextCourseParserRoutineFactory.PARSER_ROUTINE_INTERNAL_NAME
		}); 
		MapBinder<String, ParserRoutineFactory> parseRoutineBinder = MapBinder.newMapBinder(binder(), String.class, ParserRoutineFactory.class);
		parseRoutineBinder.addBinding(TextCourseParserRoutineFactory.PARSER_ROUTINE_INTERNAL_NAME).toProvider(getProvider(TextCourseParserRoutineFactory.class));

		//add a mapped binding for the CourseParserRoutine class to the implementation class
		log.debug("Creating MapBinder entry for {} to {} at key {}", new Object[] {
				CourseParserRoutineFactory.class,
				TextCourseParserRoutineFactory.class,
				TextCourseParserRoutineFactory.PARSER_ROUTINE_INTERNAL_NAME
		});
		MapBinder<String, CourseParserRoutineFactory> courseParseRoutineBinder = MapBinder.newMapBinder(binder(), String.class, CourseParserRoutineFactory.class);
		courseParseRoutineBinder.addBinding(TextCourseParserRoutineFactory.PARSER_ROUTINE_INTERNAL_NAME).toProvider(getProvider(TextCourseParserRoutineFactory.class));
		
		//add a mapped binding for the StreamParserRoutine class to the implementation class
		log.debug("Creating MapBinder entry for {} to {} at key {}", new Object[] {
				StreamParserRoutineFactory.class,
				TextCourseParserRoutineFactory.class,
				TextCourseParserRoutineFactory.PARSER_ROUTINE_INTERNAL_NAME
		});
		MapBinder<String, StreamParserRoutineFactory> streamParseRoutineBinder = MapBinder.newMapBinder(binder(),  String.class, StreamParserRoutineFactory.class);
		streamParseRoutineBinder.addBinding(TextCourseParserRoutineFactory.PARSER_ROUTINE_INTERNAL_NAME).toProvider(getProvider(TextCourseParserRoutineFactory.class));
	}
	
}