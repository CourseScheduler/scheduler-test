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
import io.coursescheduler.scheduler.retrieval.Retriever;
import io.coursescheduler.util.script.engine.ScriptEngine;
import io.coursescheduler.util.script.engine.ScriptEngineMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Inject;

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
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Map of the script parser tools that can be used
	 */
	private ScriptEngineMap scriptToolMap;
	
	/**
	 * Create the XMLParserTool with the specified script tool map
	 *
	 * @param scriptToolMap the map of script parser tools
	 */
	@Inject
	public AbstractXMLParserTool(ScriptEngineMap scriptToolMap) {
		super();
		
		this.scriptToolMap = scriptToolMap;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParserTool#retrieveNodeList(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String)
	 */
	public abstract NodeList retrieveNodeList(Node node, Preferences settings, String key) throws ParseException;	
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParserTool#retrieveNodeList(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.util.Map)
	 */
	public abstract NodeList retrieveNodeList(Node node, Preferences settings, String key, Map<String, String> replacements) throws ParseException;

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParserTool#retrieveNodes(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String)
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
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParserTool#retrieveNodes(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.util.Map)
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
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParserTool#retrieveData(org.w3c.dom.Node, java.util.prefs.Preferences, java.util.Map)
	 */
	public void retrieveData(Node node, Preferences settings, Map<String, String> data) throws ParseException{
		retrieveData(node, settings, "", "", data);
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParserTool#retrieveData(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.lang.String, java.util.Map)
	 */
	public abstract void retrieveData(Node node, Preferences settings, String attributePath, String nodePath, Map<String, String> data) throws ParseException;
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.xml.XMLParserTool#executeScript(java.lang.String, java.util.prefs.Preferences, java.lang.String)
	 */
	@Override
	public String executeScript(String value, Preferences settings, String key) {
		return executeScript(value, settings, key, new HashMap<String, String>());
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.xml.XMLParserTool#executeScript(java.lang.String, java.util.prefs.Preferences, java.lang.String, java.util.Map)
	 */
	@Override
	public String executeScript(String value, Preferences settings, String key, Map<String, String> data) {
		log.debug("Preparing to execute script");
		Preferences scripts = settings.node(ScriptEngine.SCRIPT_PREFERENCES_NODE);
		String scriptToolKey = scripts.get(Retriever.IMPLEMENTATION_KEY_PROPERTY, "");
		log.debug("Found script implementation key: {}", key);
		
		ScriptEngine scriptTool = scriptToolMap.getScriptParserTool(scriptToolKey);
		String script = scripts.get(key, null);
		log.trace("Found script tool {} for script tool implementation key {} and element script {} for element id {}", new Object[] {
				scriptTool, scriptToolKey, script, key
		});
		
		String result;
		if(scriptTool != null && script != null) {
			result = scriptTool.executeScriptOnString(value, settings, key, data);
			log.debug("Executing script for key {}, value {} yielded {}", new Object[] {key, value, result});
		}else {
			result = value;
			log.debug("No script or script tool configured for {}, passing through {}", key, value);
		}
		
		return result;
	}
}
