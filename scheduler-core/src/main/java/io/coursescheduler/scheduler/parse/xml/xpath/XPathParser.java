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

import io.coursescheduler.scheduler.parse.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
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
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private XPath xPath;
	
	public XPathParser() {
		super();
		
		this.xPath = XPathFactory.newInstance().newXPath();
	}
	
	@Override
	public List<Node> retrieveNodes(Node node, String query){
		try {
			NodeList nodes = (NodeList)xPath.evaluate(query, node, XPathConstants.NODESET);
			
			List<Node> nodeList = new ArrayList<>(nodes.getLength());
			for(int nodeIndex  = 0; nodeIndex < nodes.getLength(); nodeIndex++) {
				nodeList.add(nodes.item(nodeIndex));
			}
			return nodeList;
		} catch (XPathExpressionException e) {
			log.error("Exception retrieving node set from query {} starting at node {}", query, node, e);
			throw new RuntimeException(e);	// TODO rethrow a checked exception - maybe a io.coursescheduler.scheduler.parse.ParseException subclass
		}
	}
	
	@Override
	public void retrieveData(Node node, Preferences codes, Map<String, String> data){
		retrieveData( node, codes, "", "", data);
	}
	
	@Override
	public void retrieveData(Node node, Preferences codes, String attributePath, String nodePath, Map<String, String> data){
		try {
			for(String key: codes.keys()){
				String query = codes.get(key, null);
				String newPath = (attributePath == null || attributePath.compareTo("") == 0) ? key : attributePath + "." + key;
				String newKey = (nodePath == null || nodePath.compareTo("") == 0) ? key : nodePath + "." + key;
							
				retrieveDataElement(node, codes, newPath, newKey, key, query, data);
			}
		} catch (BackingStoreException e) {
			log.error("Exception while reading profile entries for profile node {}: {}", codes, e);
			// TODO rethrow something?
		}
	}
	
	@Override
	public void retrieveDataElement(Node node, Preferences codes, String attributePath, String keyPath, String key, String query, Map<String, String> data){
		try{
			List<Node> children = retrieveNodes(node, query);
			
			//write the number of values
			String count = Integer.toString(children.size());
			data.put(keyPath, count);
			log.trace("Element: {} ( \" {} \" ) = {}", new Object[] {keyPath, query, count});
						
			//process each item
			for(int item = 0; item < children.size(); item++){
				Node child = children.get(item).cloneNode(true);
				int keyCount = 0;
				
				log.debug("Getting code subnode: {}", key);
				Preferences subCodes = codes.node(key);
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
		//} catch(XPathExpressionException e){			// TODO catch a ParseException subclass - rethrow something?
		//	log.error("Exception retrieving data element for attribute {} at keypath {}", attributePath, keyPath, e);
		} catch (DOMException | BackingStoreException e) {
			log.error("Exception reading profile entries for profile node {}: {}", codes, e);
		}
	}
}
