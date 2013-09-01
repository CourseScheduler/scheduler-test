/**
  * @(#)XMLParser.java
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
package io.coursescheduler.scheduler.parse.xml;

import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import org.w3c.dom.Node;

/**
 * TODO Describe this type
 *
 * @author Mike Reinhold
 *
 */
public abstract class XMLParser {
	
	public abstract List<Node> retrieveNodes(Node node, String query);
	
	public abstract void retrieveData(Node node, Preferences codes, Map<String, String> data);
	
	public abstract void retrieveData(Node node, Preferences codes, String attributePath, String nodePath, Map<String, String> data);
	
	public abstract void retrieveDataElement(Node node, Preferences codes, String attributePath, String keyPath, String key, String query, Map<String, String> data);
}
