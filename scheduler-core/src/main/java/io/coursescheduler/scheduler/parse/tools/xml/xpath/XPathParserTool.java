/**
  * @(#)XPathParserTool.java
  *
  * XML ParserTool that performs the heavy lifting of parsing out data elements based on a Preferences node that 
  * contains data element names mapped to XPath expressions.
  *
  * The methods of this XML ParserTool takes a {@link java.util.prefs.Prefenences} node that contains the 
  * invocation specific configuration elements. In particular, this XML ParserTool implementation requires
  * a sub-node at {@value #QUERY_PREFERENCES_NODE} where the key is the data element name and the value
  * is the XPath expression that is used to retrieve the data for that element.
  *
  * This class is not thread safe and instances should not be shared between threads. Create a new instance
  * for each thread that processes XML DOM Nodes. 
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
package io.coursescheduler.scheduler.parse.tools.xml.xpath;

import io.coursescheduler.scheduler.parse.ParseException;
import io.coursescheduler.scheduler.parse.tools.xml.AbstractXMLParserTool;
import io.coursescheduler.util.variable.StrSubstitutorFactory;
import io.coursescheduler.util.variable.preferences.PreferencesBasedVariableFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Inject;

/**
 * XML ParserTool that performs the heavy lifting of parsing out data elements based on a Preferences node that 
 * contains data element names mapped to XPath expressions.
 *
 * The methods of this XML ParserTool takes a {@link java.util.prefs.Prefenences} node that contains the 
 * invocation specific configuration elements. In particular, the data retrieval methods of this XML 
 * ParserTool implementation requires a sub-node at {@value #QUERY_PREFERENCES_NODE} where the key is the 
 * data element name and the value is the XPath expression that is used to retrieve the data for that 
 * element.
 *  
 * This class is not thread safe and instances should not be shared between threads. Create a new instance
 * for each thread that processes XML DOM Nodes. 
 * 
 * Additionally, the W3C DOM specification does not require that the DOM implementation be thread-safe. 
 * Provide external synchronization of DOM objects, avoid processing the same DOM objects in multiple 
 * threads, or use a DOM implementation that does provide thread-safety to ensure correct functioning.
 * 
 * @author Mike Reinhold
 *
 */
public class XPathParserTool extends AbstractXMLParserTool {

	/**
	 * ParserTool internal name used in configuration and in binding to uniquely identify
	 * the parser module. It must be unique among all other ParserTool modules or else it
	 * will not be available for use by data retrieval routines.
	 * 
	 * Value: {@value}
	 */
	public static final String PARSER_INTERNAL_NAME = "xml-xpath";
	
	/**
	 * ParserTool external name which is displayed to the end user. This must be unique
	 * among all other ParserTool modules or else the user will not be able to properly
	 * differentiate between parser modules.
	 * 
	 * Value: {@value}
	 */
	public static final String PARSER_FRIENDLY_NAME = "XPath XML ParserTool";
	
	/**
	 * Short description of the parser module. 
	 * 
	 * Value: {@value}
	 */
	public static final String PARSER_SHORT_DESCRIPTION = "An XML parser that uses XPath 1.0 expressions";
	
	/**
	 * Long description of the parser module.
	 * 
	 * Value: {@value}
	 */
	public static final String PARSER_LONG_DESCRIPTION = "An XML parser that uses XPath 1.0 expressions "
			+ "against well formed XML documents. This XPath parser uses the Oracle XPath implementation "
			+ "native to the JVM to pull data elements out of the XML document efficiently using highly"
			+ "targeted statements.";
	
	/**
	 * The preferences configuration node used by the XPath ParserTool to retrieve data elements. Every entry
	 * in the preferences node should map from a data element name to an XPath query that retrieves that
	 * element.
	 */
	private static final String QUERY_PREFERENCES_NODE = "_query";
	
	/**
	 * Component based logger
	 */
	private transient Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * The XPath instance used for this XPath XML ParserTool. XPath instances are inherently not thread-
	 * safe, as such the XPathParserTool class in not thread-safe.
	 */
	private XPath xPath;
	
	/**
	 * String substitution factory for creating String Substitutors
	 */
	private StrSubstitutorFactory subsFactory;
	
	/**
	 * Preferences based variable factory for creating Preferences based variable sources
	 */
	private PreferencesBasedVariableFactory prefFactory;
	
	/**
	 * Create a new XPath XML ParserTool for retrieving DOM nodes.
	 */
	@Inject
	public XPathParserTool(StrSubstitutorFactory subsFactory, PreferencesBasedVariableFactory prefFactory) {
		super();
		
		this.xPath = XPathFactory.newInstance().newXPath();
		this.subsFactory = subsFactory;
		this.prefFactory = prefFactory;
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.ParserTool#getInternalName()
	 */
	@Override
	public String getInternalName() {
		return PARSER_INTERNAL_NAME;
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.ParserTool#getUserFriendlyName()
	 */
	@Override
	public String getUserFriendlyName() {
		return PARSER_FRIENDLY_NAME;
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.ParserTool#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return PARSER_SHORT_DESCRIPTION;
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.ParserTool#getLongDescription()
	 */
	@Override
	public String getLongDescription() {
		return PARSER_LONG_DESCRIPTION;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.xml.XMLParserTool#retrieveNodeList(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String)
	 */
	@Override
	public NodeList retrieveNodeList(Node node, Preferences settings, String key) throws ParseException {
		String query = settings.get(key, null);
		return retrieveNodeList(node, query);
	}
	
	/**
	 * Execute the specified XPath expression on a DOM Node and return the resulting NodeList
	 *
	 * @param node the DOM node on which to execute the query
	 * @param query the XPath expression to execute
	 * @return the NodeList which is the result of the XPath Query
	 * @throws ParseException if there is an issue executing the XPath expression
	 */
	protected NodeList retrieveNodeList(Node node, String query) throws ParseException{
		try {
			return (NodeList)xPath.evaluate(query, node, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			log.error("Exception retrieving node set from query {} starting at node {}", query, node, e);
			throw new ParseException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.xml.XMLParserTool#retrieveNodeList(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.util.Map)
	 */
	@Override
	public NodeList retrieveNodeList(Node node, Preferences settings, String key, Map<String, String> replacements) throws ParseException {
		String query = settings.get(key, null);
		log.trace("Performing placeholder substitution on string: {}", query);
		
		//ANALYZE can we adjust the API so that we don't need to rebuild the replacer each time
		Set<StrLookup<String>> sources = new HashSet<>();
		log.trace("Creating MapLookup for {} variables", replacements.size());
		sources.add(StrLookup.mapLookup(replacements));
		log.trace("Creating Preferences Lookup for {}", settings);
		sources.add(prefFactory.createPreferencesVariableSource(settings));
		StrSubstitutor replacer = subsFactory.createSubstitutor(sources);
		
		query = replacer.replace(query);
		log.debug("Substituted string is: {}", query);
		
		return retrieveNodeList(node, query);
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.xml.XMLParserTool#retrieveData(org.w3c.dom.Node, java.util.prefs.Preferences, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public void retrieveData(Node node, Preferences settings, String attributePath, String nodePath, Map<String, String> data) throws ParseException{
		Preferences codes = settings.node(QUERY_PREFERENCES_NODE);
		try {
			for(String key: codes.keys()){
				String newPath = (attributePath == null || attributePath.compareTo("") == 0) ? key : attributePath + "." + key;
				String newKey = (nodePath == null || nodePath.compareTo("") == 0) ? key : nodePath + "." + key;
							
				retrieveDataElement(node, settings, newPath, newKey, key, data);
			}
		} catch (BackingStoreException e) {
			log.error("Exception while reading profile entries for profile node {}: {}", codes, e);
			throw new ParseException(e);
		}
	}

	/**
	 * Bulk retrieve data under a specific node, attribute path, key path, and key according to the configuration stored under
	 * the Preferences node, and store it into the data Map. This method queries a nodelist and processes each child result 
	 * by calling {@link #retrieveDataElementIndex(Node, Preferences, String, String, String, int, Map)} with the index of the child	 *
	 * 
	 * @param node the DOM Node under which retrieval should occur
	 * @param settings the Preferences node specifying the configuration 
	 * @param attributePath the general (non-indexed) attribute path under which the element configuration is found
	 * @param keyPath the specific (indexed) attribute path under which the data is stored
	 * @param key the specific key for which a retrieval should be performed
	 * @param data the map of data element names to data element values in which to store the retrieved data
	 * @throws ParseException if there is an issue processing the data retrieval
	 */
	protected void retrieveDataElement(Node node, Preferences settings, String attributePath, String keyPath, String key, Map<String, String> data) throws ParseException{
		try{
			Preferences codes = settings.node(QUERY_PREFERENCES_NODE);
			String query = codes.get(key, null);
			NodeList children = retrieveNodeList(node, query);
			
			//write the number of values
			String count = Integer.toString(children.getLength());
			data.put(keyPath, count);
			log.trace("Element: {} ( \" {} \" ) = {}", new Object[] {keyPath, query, count});
						
			//process each item
			for(int item = 0; item < children.getLength(); item++){
				Node child = children.item(item).cloneNode(true);
				retrieveDataElementIndex(child, settings, attributePath, keyPath, key, item, data);
			}
		} catch(ParseException e){
			log.error("Exception retrieving data element for attribute {} at keypath {}", attributePath, keyPath, e);
			throw e;
		} catch(BackingStoreException e) {
			log.error("Exception reading profile entries for profile node {}: {}", settings, e);
			throw new ParseException(e);
		}
	}
	

	/**
	 * Bulk retrieve data under a specific node-result index, attribute path, key path, and key according to the configuration
	 * stored under the Preferences node, and store it into the data Map. This method processes a single data element index 
	 * (and its children, if any), by recursively calling the {@link #retrieveData(Node, Preferences, String, String, Map)} method
	 * with appropriate key.
	 * 
	 * @param node the DOM Node under which retrieval should occur
	 * @param settings the Preferences node specifying the configuration 
	 * @param attributePath the general (non-indexed) attribute path under which the element configuration is found
	 * @param keyPath the specific (indexed) attribute path under which the data is stored
	 * @param key the specific key for which a retrieval should be performed
	 * @param item the data element index being processed
	 * @param data the map of data element names to data element values in which to store the retrieved data
	 * @throws ParseException if there is an issue processing the data retrieval
	 * @throws BackingStoreException if there is an issue accessing the configuration
	 */
	protected void retrieveDataElementIndex(Node child, Preferences settings, String attributePath, String keyPath, String key, int item, Map<String, String> data) throws ParseException, BackingStoreException {
		log.debug("Getting code subnode: {}", key);
		Preferences codes = settings.node(key); 
		Preferences subCodes = codes.node(QUERY_PREFERENCES_NODE);
		int subNodeCount = 0;
		
		try {
			subNodeCount = subCodes.keys().length;
			
			if(subNodeCount == 0) {
				log.debug("No sub code entries exist for node {}, removing", key);
				subCodes.removeNode();
				codes.removeNode();
			}
		} catch(IllegalStateException e) {
			//node previously removed - ok for us since we have to load (or "create") a node 
			//in order to check for sub keys.
			log.trace("No sub code entries exist for node {}, previously removed", key);
		}
		
		if(subNodeCount > 0){
			log.debug("Sub codes exist for node {}, processing", key);
			retrieveData(child, codes, attributePath, keyPath + "." + item, data);
		}else{					
			String itemKey = keyPath + "." + item;
			String value = child.getTextContent();
			data.put(itemKey, value);
			log.trace("Element: {} ( \" text() \" ) = {}", itemKey, value);
		}
	}
}
