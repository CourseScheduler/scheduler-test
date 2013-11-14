/**
  * @(#)XMLParserTool.java
  *
  * A generic interface for XML parsers that contains general XML processing methods. Most methods accept
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
import io.coursescheduler.scheduler.parse.tools.query.QueryBasedParserTool;

import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A generic interface for XML parsers that contains general XML processing methods. Most methods accept
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
public interface XMLParserTool extends QueryBasedParserTool{
	
	/**
	 * Retrieve a list of nodes based on the configuration stored in the {@link java.util.prefs.Preferences} node
	 * as determined by the key. This method uses the specified {@link org.w3c.dom.Node} as the starting point
	 * for the node retrieval.
	 * 
	 * The usage of the Preferences node and the configuration key are implementation specific. See 
	 * the implementation specific documentation for this method for the expected content. 
	 *
	 * @param node the DOM Node under which retrieval should occur
	 * @param settings the Preferences node specifying the configuration 
	 * @param key a configuration key used to identify the specific node retrieval configuration
	 * @return the node list retrieved as specified in the configuration
	 * @throws ParseException if there is an issue processing the node retrieval
	 */
	public abstract NodeList retrieveNodeList(Node node, Preferences settings, String key) throws ParseException;
	
	/**
	 * Retrieve a list of nodes based on the configuration stored in the {@link java.util.prefs.Preferences} node
	 * as determined by the key. This method uses the specified {@link org.w3c.dom.Node} as the starting point
	 * for the node retrieval. A Map of variable replacements is .used to substitute values in for placeholder strings
	 * 
	 * The usage of the Preferences node and the configuration key are implementation specific. See 
	 * the implementation specific documentation for this method for the expected content.
	 * 
	 * @param node the DOM Node under which retrieval should occur
	 * @param settings the Preferences node specifying the configuration 
	 * @param key a configuration key used to identify the specific node retrieval configuration
	 * @param replacements a map of placeholder strings to substitution values used in the node replacement
	 * @return the node list retrieved as specified in the configuration
	 * @throws ParseException if there is an issue processing the node retrieval
	 */
	public abstract NodeList retrieveNodeList(Node node, Preferences settings, String key, Map<String, String> replacements) throws ParseException;
	
	
	/**
	 * Retrieve a list of nodes based on the configuration stored in the {@link java.util.prefs.Preferences} node
	 * as determined by the key. This method uses the specified {@link org.w3c.dom.Node} as the starting point
	 * for the node retrieval.
	 * 
	 * The usage of the Preferences node and the configuration key are implementation specific. See 
	 * the implementation specific documentation for this method for the expected content. 
	 *
	 * @param node the DOM Node under which retrieval should occur
	 * @param settings the Preferences node specifying the configuration 
	 * @param key a configuration key used to identify the specific node retrieval configuration
	 * @return the node list retrieved as specified in the configuration
	 * @throws ParseException if there is an issue processing the node retrieval
	 */
	public List<Node> retrieveNodes(Node node, Preferences settings, String key) throws ParseException;

	/**
	 * Retrieve a list of nodes based on the configuration stored in the {@link java.util.prefs.Preferences} node
	 * as determined by the key. This method uses the specified {@link org.w3c.dom.Node} as the starting point
	 * for the node retrieval. A Map of variable replacements is .used to substitute values in for placeholder strings
	 * 
	 * The usage of the Preferences node and the configuration key are implementation specific. See 
	 * the implementation specific documentation for this method for the expected content.
	 * 
	 * @param node the DOM Node under which retrieval should occur
	 * @param settings the Preferences node specifying the configuration 
	 * @param key a configuration key used to identify the specific node retrieval configuration
	 * @param replacements a map of placeholder strings to substitution values used in the node replacement
	 * @return the node list retrieved as specified in the configuration
	 * @throws ParseException if there is an issue processing the node retrieval
	 */
	public List<Node> retrieveNodes(Node node, Preferences settings, String key, Map<String, String> replacements) throws ParseException;
	
	/**
	 * Bulk retrieve data under the node according to the configuration stored under the Preferences node and store it into
	 * the data Map. This is a root retrieval, equivalent to calling {@link #retrieveData(Node, Preferences, String, String, Map)} using
	 * the following parameters (node, settings, "", "", data)
	 * 
	 * The configuration in the Preferences node is implementation specific and varies.
	 *
	 * @param node the DOM Node under which retrieval should occur
	 * @param settings the Preferences node specifying the configuration 
	 * @param data the map of data element names to data element values in which to store the retrieved data
	 * @throws ParseException if there is an issue processing the data retrieval
	 */
	public void retrieveData(Node node, Preferences settings, Map<String, String> data) throws ParseException;

	/**
	 * Bulk retrieve data under the node according to the configuration stored under the Preferences node and store it into
	 * the data Map. Calling this with "" for the attributePath and nodePath parameters is equivalent to calling {@link #retrieveData(Node, Preferences, Map)}
	 * (the root retrieval method). This is the relative path data retrieval method and calls {@link #retrieveDataElement(Node, Preferences, String, String, String, Map)}
	 * for each data element code found in the Preferences sub-node specified by the {@link #CODES_PREFERENCES_NODE} constant.
	 *
	 * @param node the DOM Node under which retrieval should occur
	 * @param settings the Preferences node specifying the configuration 
	 * @param attributePath the general (non-indexed) attribute path under which the element configuration is found
	 * @param nodePath the specific (indexed) attribute path under which the data is stored
	 * @param data the map of data element names to data element values in which to store the retrieved data
	 * @throws ParseException if there is an issue processing the data retrieval
	 */
	public abstract void retrieveData(Node node, Preferences settings, String attributePath, String nodePath, Map<String, String> data) throws ParseException;
	

	/**
	 * Execute the script configured under the specified preferences node against the specified data element
	 *
	 * @param value the script source value that can be operated upon
	 * @param settings the configuration parent node of the query or script node
	 * @param key the local data element key to identify the script in configuration
	 * @return the result of the script evaluation
	 */
	public abstract String executeScript(String value, Preferences settings, String key);
	
	/**
	 * Execute the script configured under the specified preferences node against the specified data element, with the related data elements
	 *
	 * @param value the script source value that can be operated upon
	 * @param settings the configuration parent node of the query or script node
	 * @param key the local data element key to identify the script in configuration
	 * @param data  related data elements that can be substituted
	 * @return the result of the script evaluation
	 */
	public abstract String executeScript(String value, Preferences settings, String key, Map<String, String> data);
}
