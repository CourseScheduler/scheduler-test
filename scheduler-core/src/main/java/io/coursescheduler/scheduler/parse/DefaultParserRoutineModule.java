/**
  * @(#)DefaultParserRoutineModule.java
  *
  * Guice module for ensuring that the binding type is properly configured. This prevents the scenario
  * where no implementations are bound, thus causing injector instantiation issues. Instead, the Set
  * bound by Guice will be empty.
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

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

/**
 * Guice module for ensuring that the binding type is properly configured. This prevents the scenario
 * where no implementations are bound, thus causing injector instantiation issues. Instead, the Set
 * bound by Guice will be empty.
 *
 * @author Mike Reinhold
 *
 */
public class DefaultParserRoutineModule extends AbstractModule {
	
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {

		/*
		 * Create the ParserRoutine map binder - this prevents binding errors if no implementations are defined in other modules
		 * 
		 * We can safely suppress the "unused" warning because we know that the binder will not be used in this context. Though 
		 * we don't use the binder, a side effect of having created the binder is that the binding is now valid (as an empty set)
		 * if no implementations are bound (instead of missing a binding)
		 */ 
		@SuppressWarnings("unused")
		MapBinder<String, ParserRoutine> routineBinder = MapBinder.newMapBinder(binder(),  String.class,  ParserRoutine.class);
	}
	
}
