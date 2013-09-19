/**
  * @(#)DocumentBuilderProvider.java
  *
  * Provider interface for DocumentBuilders which allow throwing of 
  * ParserConfigurationExceptions which can be the result of the 
  * {@link javax.xml.parsers.DocumentBuilderFactory#newDocumentBuilder} method 
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
package io.coursescheduler.scheduler.parse.tools.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import com.google.inject.throwingproviders.CheckedProvider;

/**
 * Provider interface for DocumentBuilders which allow throwing of 
 * ParserConfigurationExceptions which can be the result of the 
 * {@link javax.xml.parsers.DocumentBuilderFactory#newDocumentBuilder} method 
 * 
 * @author Mike Reinhold
 *
 */
public interface DocumentBuilderProvider extends CheckedProvider<DocumentBuilder> {
		
	/**
	 * Get a DocumentBuilder for the purposes of creating and parsing XML documents
	 *
	 * @return a DocumentBuilder
	 * @throws ParserConfigurationException if there is a configuration error with the parser
	 */
	@Override
	DocumentBuilder get() throws ParserConfigurationException;
}
