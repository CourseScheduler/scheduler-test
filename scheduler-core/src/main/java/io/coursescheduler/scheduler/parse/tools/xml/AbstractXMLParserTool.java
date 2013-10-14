/**
  * @(#)XMLParserTool.java
  *
  *  A base XML parser class that provides basic implementations of the XMLParserTool. Most methods accept
  * a {@link java.util.prefs.Preferences} node containing implementation specific configuration
  * elements. Consult the documentation for the specific XMLParserTool implementation for more
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

import io.coursescheduler.scheduler.parse.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A base XML parser class that provides basic implementations of the XMLParserTool. Most methods accept
 * a {@link java.util.prefs.Preferences} node containing implementation specific configuration
 * elements. Consult the documentation for the specific XMLParserTool implementation for more
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
public abstract class AbstractXMLParserTool implements XMLParserTool {
	
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.xml.XMLParserTool#retrieveNodeList(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String)
	 */
	public abstract NodeList retrieveNodeList(Node node, Preferences settings, String key) throws ParseException;	
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.xml.XMLParserTool#retrieveNodeList(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.util.Map)
	 */
	public abstract NodeList retrieveNodeList(Node node, Preferences settings, String key, Map<String, String> replacements) throws ParseException;

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.xml.XMLParserTool#retrieveNodes(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String)
	 */
	public List<Node> retrieveNodes(Node node, Preferences settings, String key) throws ParseException {
		NodeList list = retrieveNodeList(node, settings, key);
		
		List<Node> nodes = new ArrayList<Node>();
		for(int item = 0; item < list.getLength(); item++) {
			nodes.add(list.item(item));
		}
		return nodes;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.xml.XMLParserTool#retrieveNodes(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.util.Map)
	 */
	public List<Node> retrieveNodes(Node node, Preferences settings, String key, Map<String, String> replacements) throws ParseException {
		NodeList list = retrieveNodeList(node, settings, key, replacements);
		
		List<Node> nodes = new ArrayList<Node>();
		for(int item = 0; item < list.getLength(); item++) {
			nodes.add(list.item(item));
		}
		return nodes;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.xml.XMLParserTool#retrieveData(org.w3c.dom.Node, java.util.prefs.Preferences, java.util.Map)
	 */
	public void retrieveData(Node node, Preferences settings, Map<String, String> data) throws ParseException{
		retrieveData(node, settings, "", "", data);
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.xml.XMLParserTool#retrieveData(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.lang.String, java.util.Map)
	 */
	public abstract void retrieveData(Node node, Preferences settings, String attributePath, String nodePath, Map<String, String> data) throws ParseException;
}
