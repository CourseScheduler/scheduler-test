/**
  * @(#)SectionBasedXMLParser.java
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
package io.coursescheduler.scheduler.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * TODO Describe this type
 *
 * @author Mike Reinhold
 *
 */
public class SectionBasedXMLParser {
	
	/**
	 * TODO Describe this field
	 */
	private static final String COURSE_NAME_XPATH_EXPRESSION_PROPERTY = "query-list-course.id";
	
	/**
	 * TODO Describe this field
	 */
	private static final String COURSE_DETAIL_XPATH_EXPRESSION_PROPERTY = "query-single-course.id";
	
	/**
	 * TODO Describe this field
	 */
	private static final String COURSE_ID_VARIABLE = "${course.id}";
	
	/**
	 * TODO Describe this field
	 */
	private static final String GENERAL_SETTINGS_NODE = "general";
	
	/**
	 * TODO Describe this field
	 */
	private static final String COURSE_CODES_NODE = "course_codes";
	
	/**
	 * TODO Describe this field
	 */
	private static final String SECTION_CODES_NODE = "section_codes";
	
	/**
	 * Instance specifice logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private Document doc;
	
	private Map<String, Map<String, String>> courseDataSets;
	
	private Preferences profile;
	
	private SectionBasedXMLParser(){
		super();
		courseDataSets = new HashMap<>();
	}
	
	public SectionBasedXMLParser(InputStream input, Preferences profile) throws ParserConfigurationException, SAXException, IOException{
		this();
				
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(input);
		this.profile = profile;
	}
	
	public void parse(){
		log.info("Starting to parse the XML input");
		long start = System.currentTimeMillis();		
		XPath xPath = XPathFactory.newInstance().newXPath();
		
		try {
			for(String courseID: getCourseNames(xPath, profile.node(GENERAL_SETTINGS_NODE))){
				try {

					//TODO remove this line
					System.out.println("\n----- Course: " + courseID + " -----");
					
					captureCourseData(xPath, profile.node(GENERAL_SETTINGS_NODE), courseID);
				} catch(XPathExpressionException e) {
					log.error("Exception capturing course data for " + courseID, e);
				}
			}			
		} catch (XPathExpressionException e) {
			log.error("Exception retrieving course names", e);
		}
		
		long end = System.currentTimeMillis();
		log.info("Retrieved course data for {} courses in {} milliseconds", courseDataSets.size(), (end - start));
		
		//TODO remove this line
		System.out.println("\nRetrieved course data for " + courseDataSets.size() + " courses in " + (end - start) + " milliseconds");
	}
	
	private Set<String> getCourseNames(XPath xPath, Preferences settings) throws XPathExpressionException{
		log.debug("Retrieving course IDs from source data set");
		Set<String> courses = new TreeSet<String>();
		XPathExpression expr = xPath.compile(settings.get(COURSE_NAME_XPATH_EXPRESSION_PROPERTY, null));
		NodeList list = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		for(int item = 0; item < list.getLength(); item++){
			Node node = list.item(item).cloneNode(true);
			String courseID = node.getTextContent();
			courses.add(courseID);
			log.trace("Found section belonging to {}", courseID);
		}

		log.debug("Finished retrieving course IDs from source data set");
		return courses;
	}
	
	private void captureCourseData(XPath xPath, Preferences settings, String courseID) throws XPathExpressionException {
		log.debug("Processing course: {}", courseID);
		long start = System.currentTimeMillis();
		
		String query = settings.get(COURSE_DETAIL_XPATH_EXPRESSION_PROPERTY, null);
		query = query.replaceAll(Pattern.quote(COURSE_ID_VARIABLE), courseID);
		XPathExpression expr = xPath.compile(query);
		NodeList list = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		log.debug("Found {} section elements for {}", list.getLength(), courseID);
		int item = 0;
		Node node = list.item(item).cloneNode(true);
		Map<String, String> courseData = captureCourseData(xPath, node, courseID);
		
		for(; item < list.getLength(); item++){
			node = list.item(item).cloneNode(true);
			
			captureSectionData(xPath, node, item, courseData);
		}
		long end = System.currentTimeMillis();
		log.debug("Finished processing course {} in {} milliseconds", courseID, (end - start));
	}
	
	private Map<String, String> captureCourseData(XPath xPath, Node node, String courseID) throws XPathExpressionException{
		log.debug("Capturing course data for {}", courseID);
		Map<String, String> data = courseDataSets.get(courseID);
		if(!courseDataSets.containsKey(courseID)){
			data = new HashMap<String, String>();
			courseDataSets.put(courseID, data);
			retrieveData(xPath, node, data, profile.node(COURSE_CODES_NODE));
		}
		return data;
	}
	
	private void captureSectionData(XPath xPath, Node node, int sectionIndex, Map<String, String> data) {
		log.debug("Capturing course data for section index {}", sectionIndex);
		retrieveData(xPath, node, "course.sections", "course.sections." + sectionIndex, profile.node(SECTION_CODES_NODE), data);
	}
	
	private void retrieveData(XPath xPath, Node node, Map<String, String> data, Preferences codes){
		retrieveData(xPath, node, "", "", codes, data);
	}
	
	private void retrieveData(XPath xPath, Node node, String attributePath, String nodePath, Preferences codes, Map<String, String> data){
		try {
			for(String key: codes.keys()){
				String query = codes.get(key, null);
				String newPath = (attributePath == null || attributePath.compareTo("") == 0) ? key : attributePath + "." + key;
				String newKey = (nodePath == null || nodePath.compareTo("") == 0) ? key : nodePath + "." + key;
							
				retrieveDataElement(xPath, node, newPath, newKey, key, query, codes, data);
			}
		} catch (BackingStoreException e) {
			log.error("Exception while reading profile entries for profile node {}: {}", codes, e);
		}
	}
	
	private void retrieveDataElement(XPath xPath, Node node, String attributePath, String keyPath, String key, String query, Preferences codes, Map<String, String> data){
		try{
			NodeList children = (NodeList)xPath.evaluate(query, node, XPathConstants.NODESET);
			
			//write the number of values
			String count = new Integer(children.getLength()).toString();
			data.put(keyPath, count);
			log.trace("Element: {} ( \" {} \" ) = {}", new Object[] {keyPath, query, count});
			
			//TODO remove this line
			System.out.println(keyPath + " ( \"" + query + "\" ) = " + count);
						
			//process each item
			for(int item = 0; item < children.getLength(); item++){
				Node child = children.item(item).cloneNode(true);
				
				log.debug("Getting code subnode: {}", key);
				Preferences subCodes = codes.node(key);
				
				if(subCodes.keys().length > 0){
					log.debug("Sub codes exist for node {}, processing", key);
					retrieveData(xPath, child, attributePath, keyPath + "." + item, subCodes, data);
				}else{					
					String itemKey = keyPath + "." + item;
					String value = child.getTextContent();
					data.put(itemKey, value);
					log.trace("Element: {} ( \" text() \" ) = {}", itemKey, count);
					
					//TODO remove this line
					System.out.println(itemKey + " ( \"text()\" ) = " + value);
				}
			}
		} catch(XPathExpressionException e){
			log.error("Exception retrieving data element for attribute {} at keypath {}", attributePath, keyPath, e);
		} catch (DOMException | BackingStoreException e) {
			log.error("Exception reading profile entries for profile node {}: {}", codes, e);
		}
	}
}
