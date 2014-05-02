/**
  * @(#)HtmlParserTool.java
  *
  * A generic interface for HTML parsers that contains general HTML processing methods. Most methods accept
  * a {@link java.util.prefs.Preferences} node containing implementation specific configuration
  * elements. Consult the documentation for the specific HtmlParserTool implementation for more
  * information on the content of the Preferences node. 
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
package io.coursescheduler.scheduler.parse.tools.html;

import io.coursescheduler.scheduler.parse.ParserTool;

/**
 * A generic interface for HTML parsers that contains general HTML processing methods. Most methods accept
 * a {@link java.util.prefs.Preferences} node containing implementation specific configuration
 * elements. Consult the documentation for the specific HtmlParserTool implementation for more
 * information on the content of the Preferences node. 
 * 
 * Implementations are not required to be thread-safe and as such should not be shared between threads.
 * 
 * Additionally, the W3C DOM specification does not require that the DOM implementation be thread-safe. 
 * Provide external synchronization of DOM objects, avoid processing the same DOM objects in multiple 
 * threads, or use a DOM implementation that does provide thread-safety to ensure correct functioning. 
 *
 * @author Mike Reinhold
 *
 */
public interface HtmlParserTool extends ParserTool {
	
	//No common HTML parser data model is currently viable.... Routines will need to use a specific instance
	
}
