/**
  * @(#)XPathParser.java
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
package io.coursescheduler.scheduler.parse.xml.xpath;

import io.coursescheduler.scheduler.parse.ParseException;
import io.coursescheduler.scheduler.parse.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML Parser that performs the heavy lifting of parsing out data elements based on a Preferences node that 
 * contains data element names mapped to XPath expressions.
 *
 * @author Mike Reinhold
 *
 */
public class XPathParser extends XMLParser {

	/**
	 * TODO Describe this field
	 */
	private static final String CODES_PREFERENCES_NODE = "codes";
	
	/**
	 * Instance specific logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * TODO Describe this field
	 */
	private XPath xPath;
	
	/**
	 * TODO Describe this constructor
	 *
	 */
	public XPathParser() {
		super();
		
		this.xPath = XPathFactory.newInstance().newXPath();
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParser#retrieveNodeList(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String)
	 */
	@Override
	public NodeList retrieveNodeList(Node node, Preferences settings, String key) throws ParseException {
		String query = settings.get(key, null);
		return retrieveNodeList(node, query);
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParser#retrieveNodeList(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.util.Map)
	 */
	@Override
	public NodeList retrieveNodeList(Node node, Preferences settings, String key, Map<String, String> replacements) throws ParseException {
		String query = settings.get(key, null);
		
		for(Entry<String, String> replacement: replacements.entrySet()) {
			query = query.replaceAll(Pattern.quote(replacement.getKey()), replacement.getValue());
		}
		
		return retrieveNodeList(node, query);
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParser#retrieveNodes(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String)
	 */
	@Override
	public List<Node> retrieveNodes(Node node, Preferences settings, String key) throws ParseException {
		NodeList list = retrieveNodeList(node, settings, key);
		
		List<Node> nodes = new ArrayList<Node>();
		for(int item = 0; item < list.getLength(); item++) {
			nodes.add(list.item(item));
		}
		return nodes;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParser#retrieveNodes(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.util.Map)
	 */
	@Override
	public List<Node> retrieveNodes(Node node, Preferences settings, String key, Map<String, String> replacements) throws ParseException {
		NodeList list = retrieveNodeList(node, settings, key, replacements);
		
		List<Node> nodes = new ArrayList<Node>();
		for(int item = 0; item < list.getLength(); item++) {
			nodes.add(list.item(item));
		}
		return nodes;
	}
	
	protected NodeList retrieveNodeList(Node node, String query) throws ParseException{
		try {
			return (NodeList)xPath.evaluate(query, node, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			log.error("Exception retrieving node set from query {} starting at node {}", query, node, e);
			throw new ParseException(e);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParser#retrieveData(org.w3c.dom.Node, java.util.prefs.Preferences, java.util.Map)
	 */
	@Override
	public void retrieveData(Node node, Preferences codes, Map<String, String> data) throws ParseException{
		retrieveData( node, codes, "", "", data);
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParser#retrieveData(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public void retrieveData(Node node, Preferences settings, String attributePath, String nodePath, Map<String, String> data) throws ParseException{
		Preferences codes = settings.node(CODES_PREFERENCES_NODE);
		try {
			for(String key: codes.keys()){
				String query = codes.get(key, null);
				String newPath = (attributePath == null || attributePath.compareTo("") == 0) ? key : attributePath + "." + key;
				String newKey = (nodePath == null || nodePath.compareTo("") == 0) ? key : nodePath + "." + key;
							
				retrieveDataElement(node, settings, newPath, newKey, key, query, data);
			}
		} catch (BackingStoreException e) {
			log.error("Exception while reading profile entries for profile node {}: {}", codes, e);
			throw new ParseException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParser#retrieveDataElement(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public void retrieveDataElement(Node node, Preferences settings, String attributePath, String keyPath, String key, String query, Map<String, String> data) throws ParseException{
		try{
			NodeList children = retrieveNodeList(node, query);
			
			//write the number of values
			String count = Integer.toString(children.getLength());
			data.put(keyPath, count);
			log.trace("Element: {} ( \" {} \" ) = {}", new Object[] {keyPath, query, count});
						
			//process each item
			for(int item = 0; item < children.getLength(); item++){
				Node child = children.item(item).cloneNode(true);
				retrieveDataElement(child, settings, attributePath, keyPath, key, item, data);
			}
		} catch(ParseException e){
			log.error("Exception retrieving data element for attribute {} at keypath {}", attributePath, keyPath, e);
			throw e;
		} catch(BackingStoreException e) {
			log.error("Exception reading profile entries for profile node {}: {}", settings, e);
			throw new ParseException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.xml.XMLParser#retrieveDataElement(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.lang.String, java.lang.String, int, java.util.Map)
	 */
	@Override
	public void retrieveDataElement(Node child, Preferences settings, String attributePath, String keyPath, String key, int item, Map<String, String> data) throws ParseException, BackingStoreException {
		log.debug("Getting code subnode: {}", key);
		Preferences codes = settings.node(CODES_PREFERENCES_NODE); 
		Preferences subCodes = codes.node(key);
		int keyCount = 0;
		
		try {
			keyCount = subCodes.keys().length;
			
			if(keyCount == 0) {
				log.debug("No sub code entries exist for node {}, removing", key);
				subCodes.removeNode();
			}
		} catch(IllegalStateException e) {
			//node previously removed - ok for us since we have to load (or "create") a node 
			//in order to check for sub keys.
			log.trace("No sub code entries exist for node {}, previously removed", key);
		}
		
		if(keyCount > 0){
			log.debug("Sub codes exist for node {}, processing", key);
			retrieveData(child, subCodes, attributePath, keyPath + "." + item, data);
		}else{					
			String itemKey = keyPath + "." + item;
			String value = child.getTextContent();
			data.put(itemKey, value);
			log.trace("Element: {} ( \" text() \" ) = {}", itemKey, value);
		}
	}
}
