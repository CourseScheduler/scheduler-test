/**
  * @(#)NodeGroupXMLCourseParserHelperRoutine.java
  *
  * Parser helper to perform organization specific extraction of element data from the XML document.
  * This ParserHelperRoutine is designed for XML documents where the data elements are grouped into
  * tasks (each handled by an instance of this class) based on a top level grouping. 
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

import io.coursescheduler.scheduler.parse.ParseException;
import io.coursescheduler.scheduler.parse.tools.xml.DocumentBuilderProvider;
import io.coursescheduler.scheduler.parse.tools.xml.XMLParserTool;
import io.coursescheduler.scheduler.parse.tools.xml.XMLParserToolMap;
import io.coursescheduler.util.variable.StrSubstitutorFactory;
import io.coursescheduler.util.variable.preferences.PreferencesBasedVariableFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.prefs.Preferences;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import static io.coursescheduler.scheduler.parse.xml.XMLParserConstants.*;

/**
 * Parser helper to perform organization specific extraction of element data from the XML document.
 * This ParserHelperRoutine is designed for XML documents where the data elements are grouped into
 * tasks (each handled by an instance of this class) based on a top level grouping. 
 *
 * @author Mike Reinhold
 *
 */
public class NodeGroupXMLCourseParserHelperRoutine extends XMLParserHelperRoutine {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Component based logger
	 */
	private transient Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * The list of document nodes to process
	 */
	private transient List<Node> nodeList;
	
	/**
	 * Thread safe map for the course data
	 */
	private ConcurrentMap<String, String> data;
	
	/**
	 * the Preferences node containing the settings for extracting data
	 */
	private transient Preferences retrievalSettings;
	
	/**
	 * The XML Parser Tool that will assist in extracting data from the XML document
	 */
	private transient XMLParserTool parser;
	
	/**
	 * Element ID that this instance is processing
	 */
	private String id;
	
	/**
	 * Context for the data elements that are under the element being processed
	 */
	private String context;
	
	/**
	 * DocumentBuilderProvider for getting Builders to build XML documents
	 */
	private transient DocumentBuilderProvider builderProvider;
	
	/**
	 * StrSubstitutorFactory for accessing string substitutors
	 */
	private transient StrSubstitutor substitutor;
	
	/**
	 * Create a new NodeGroupXMLCourseParserHelperRoutine instance
	 *
	 * @param substitutionFactor factory for accessing substitutor instances
	 * @param prefsFact preferences based variable source factory
	 * @param builderProvider provider for document builders to create XML documents
	 * @param toolMap XMLParserToolMap for retrieving the correct XML tool
	 * @param nodeList the list of XML document nodes to process
	 * @param settings the preferences node containing the configuration for the parsing
	 * @param elementID the element that is being retrieved
	 * @param context the context for the data values being retrieved
	 * @param data thread safe map for storing the course data
	 */
	@AssistedInject
	public NodeGroupXMLCourseParserHelperRoutine(StrSubstitutorFactory substitutionFactory, PreferencesBasedVariableFactory prefsFact, DocumentBuilderProvider builderProvider, XMLParserToolMap toolMap, @Assisted("nodes") List<Node> nodeList, @Assisted("settings") Preferences settings, @Assisted("elementid") String elementID, @Assisted("context") String context, @Assisted("data") ConcurrentMap<String, String> data) {
		super();
		
		this.nodeList = nodeList;
		this.data = data;
		this.id = elementID;
		this.context = context;
		this.retrievalSettings = settings;
		this.parser = toolMap.getXMLParserTool(
			settings.get(PARSER_TOOL_PROPERTY, null)
		);
		this.builderProvider = builderProvider;
		
		log.trace("Preparing to build lookup instances for variable substitution");
		Set<StrLookup<String>> localLookups = new HashSet<>();
		Map<String, String> locals = new HashMap<>();
		locals.put(ELEMENT_ID_VARIABLE, elementID);
		log.trace("Map bound {} with value {}", ELEMENT_ID_VARIABLE, elementID);
		
		localLookups.add(StrLookup.mapLookup(locals));
		localLookups.add(prefsFact.createPreferencesVariableSource(settings));
		log.trace("Preferences based lookup for {}", settings);
		
		this.substitutor = substitutionFactory.createSubstitutor(localLookups);
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		log.info("Processing element: {}", id);
		long start = System.currentTimeMillis();
		
		try {
			log.debug("Combining data elements into a single documunt");
			Node doc = combineGroupNodes();			
			captureData(retrievalSettings, doc, id, data);
		} catch(ParseException e) {
			log.error("Processing element {} failed - element may not be present or correct in format", id);
			log.error("Exception occurred processing element", e);
			completeExceptionally(e);
		} catch (ParserConfigurationException e) {
			log.error("Processing element {} failed - element may not be present or in correct format",  id);
			log.error("Exception occured processing element", e);
			completeExceptionally(e);
		} 

		long end = System.currentTimeMillis();
		log.info("Finished processing element {} in {} milliseconds", id, (end - start));
	}
	
	/**
	 * Combine the Nodes that were grouped for this task into a single document for simpler parsing
	 *
	 * @return the combined XML document containing all of the nodes
	 * @throws ParserConfigurationException if there is a problem starting the XML parser
	 */
	private Node combineGroupNodes() throws ParserConfigurationException {
		String rowElement = retrievalSettings.get(XML_GROUPING_ELEMENT_PROPERTY, XML_GROUPING_ELEMENT_DEFAULT);
		log.debug("Preparing to combine {} elements under top level element {}", rowElement);
		
		Document doc = builderProvider.get().newDocument();
		Node rowGroup = doc.createElement(rowElement);
		doc.appendChild(rowGroup);
		log.trace("Created top level element {} and added to document {}", rowGroup, doc);
		
		for(int item = 0; item < nodeList.size(); item++){
			Node node = nodeList.get(item);
			
			Node replacement = doc.adoptNode(node);
			rowGroup.appendChild(replacement);
			
			log.trace("Migrated original node {} to new node {} and added to document", node, replacement);
		}
		
		log.debug("Checking if group document save is requested via {}", XML_GROUP_SAVE_PROPERTY);
		if(retrievalSettings.getBoolean(XML_GROUP_SAVE_PROPERTY, false)) {
			saveGroupXML(doc);
		} else {
			log.debug("No group document save requested");
		}
		
		return doc;
	}
	
	/**
	 * Save the Group XML document for debugging purposes
	 *
	 * @param doc the XML document to save
	 */
	private void saveGroupXML(Document doc) {
		long start = System.currentTimeMillis();
		log.debug("Preparing to save group document");
		try {
			String file = retrievalSettings.get(XML_GROUP_FILE_PROPERTY, XML_GROUP_FILE_DEFAULT);
			log.debug("File target for element {} is {}", id, file);
			file = substitutor.replace(file);
			log.debug("Variable substituted file target for {} is {}", id, file);
			
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");

            // send DOM to file
            tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));

			long end = System.currentTimeMillis();
			log.debug("Documment saved to {} in {} ms", file, end - start);
        } catch (TransformerException | IOException e) {
        	log.error("Unable to save group document for element {}", id);
            log.error("Exception occurred while saving group document", e);
        }
	}
	
	/**
	 * Extract data from the group XML document
	 *
	 * @param settings the preferences node with the parser tool configuration data
	 * @param node the XML node on which the parser tool will execute
	 * @param elementID the element id for which data will be retrieved
	 * @param data the thread safe map which will store the data
	 * @throws ParseException if there is an issue parsing the xml document 
	 */
	private void captureData(Preferences settings, Node node, String elementID, Map<String, String> data) throws ParseException {
		log.debug("Capturing element data for {}", elementID);
		long start = System.currentTimeMillis();
		parser.retrieveData(
				node, 
				settings, 
				context,
				(context.compareTo("") == 0) ? "" : context + ".", 
				data
		);
		long end = System.currentTimeMillis();
		log.debug("Finished processing element data for {} in {} milliseconds", elementID, (end - start));
	}
}
