/**
  * @(#)XMLCourseParserModule.java
  *
  * Guice module for the XML Course parser plugin binding
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

import io.coursescheduler.scheduler.parse.routines.CourseParserRoutine;
import io.coursescheduler.scheduler.parse.routines.ParserRoutine;
import io.coursescheduler.scheduler.parse.routines.ParserRoutineFactory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;

/**
 * Guice module for the XML Course parser plugin binding
 *
 * @author Mike Reinhold
 *
 */
public class XMLCourseParserModule extends AbstractModule {
	
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		//install a module indicating that XMLCourseParserRoutine can be built from a factory with assisted inject
		install(new FactoryModuleBuilder()
			.implement(ParserRoutine.class, XMLCourseParserRoutine.class)
			.implement(CourseParserRoutine.class, XMLCourseParserRoutine.class)
			.build(XMLCourseParserRoutineFactory.class)
		);
		
		//add a mapped binding from the ParseRouting class to the implementation classes 
		MapBinder<String, ParserRoutineFactory> routineBinder = MapBinder.newMapBinder(binder(), String.class, ParserRoutineFactory.class);
		routineBinder.addBinding(XMLCourseParserRoutineFactory.PARSER_ROUTINE_INTERNAL_NAME).toProvider(getProvider(XMLCourseParserRoutineFactory.class));
	}
	
}
