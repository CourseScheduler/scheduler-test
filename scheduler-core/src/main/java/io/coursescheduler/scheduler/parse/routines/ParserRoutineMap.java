/**
  * @(#)ParserRoutineMap.java
  *
  * TODO FILE PURPOSE
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
package io.coursescheduler.scheduler.parse.routines;

import java.util.Map;

import com.google.inject.Inject;

/**
 * TODO Describe this type
 *
 * @author Mike Reinhold
 *
 */
public class ParserRoutineMap {
	
	private Map<String, ParserRoutineFactory> parserFactories; 
	
	@Inject
	public ParserRoutineMap(Map<String, ParserRoutineFactory> parserFactories) {
		super();
		
		this.parserFactories = parserFactories;
	}
	
	public ParserRoutineFactory getFactory(String key) {
		return parserFactories.get(key);
	}
	
}
