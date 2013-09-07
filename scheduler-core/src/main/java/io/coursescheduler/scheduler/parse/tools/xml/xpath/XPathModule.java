/**
  * @(#)XPathModule.java
  *
  * Guice module for binding the XPathParserTool as an optional ParserTool plugin
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
package io.coursescheduler.scheduler.parse.tools.xml.xpath;

import io.coursescheduler.scheduler.parse.tools.ParserTool;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

/**
 * Guice module for binding the XPathParserTool as an optional ParserTool plugin
 *
 * @author Mike Reinhold
 *
 */
public class XPathModule extends AbstractModule {
		
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		//get a map binder from the internal name to the ParserTool class
		MapBinder<String, ParserTool> parserBinder = MapBinder.newMapBinder(binder(), String.class, ParserTool.class);
		
		//set the binding for the XPathParserTool
		parserBinder.addBinding(XPathParserTool.PARSER_INTERNAL_NAME).to(XPathParserTool.class);
	}
	
}