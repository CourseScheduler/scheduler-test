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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RecursiveAction;
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
public class SectionBasedXMLParser extends RecursiveAction {
	
	/**
	 * TODO Describe this field
	 */
	private static final String BATCH_SIZE_PROPERTY = "batch.size";
	
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
		courseDataSets = new ConcurrentHashMap<>();
	}
	
	public SectionBasedXMLParser(InputStream input, Preferences profile) throws ParserConfigurationException, SAXException, IOException{
		this();
				
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(input);
		this.profile = profile;
	}

	@Override
	protected void compute() {
		log.info("Starting to parse the XML input");
		long start = System.currentTimeMillis();		
		XPath xPath = XPathFactory.newInstance().newXPath();

		try {
			executeBatches(xPath, profile, getCourseNames(xPath, profile));
		} catch (XPathExpressionException e) {
			log.error("Exception retrieving course names", e);
		}
		
		long end = System.currentTimeMillis();
		log.info("Retrieved course data for {} courses in {} milliseconds", courseDataSets.size(), (end - start));
	}
	
	private Set<String> getCourseNames(XPath xPath, Preferences settings) throws XPathExpressionException{
		log.info("Retrieving course IDs from source data set");
		Set<String> courses = new TreeSet<String>();
		XPathExpression expr = xPath.compile(settings.node(GENERAL_SETTINGS_NODE).get(COURSE_NAME_XPATH_EXPRESSION_PROPERTY, null));
		NodeList list = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		for(int item = 0; item < list.getLength(); item++){
			Node node = list.item(item).cloneNode(true);
			String courseID = node.getTextContent();
			courses.add(courseID);
			log.debug("Found section belonging to {}", courseID);
		}

		log.debug("Finished retrieving course IDs from source data set");
		return courses;
	}
	
	private RecursiveAction createCourseTask(XPath xPath, Preferences settings, String courseID) throws XPathExpressionException {
		String query = settings.node(GENERAL_SETTINGS_NODE).get(COURSE_DETAIL_XPATH_EXPRESSION_PROPERTY, null);
		query = query.replaceAll(Pattern.quote(COURSE_ID_VARIABLE), courseID);
		XPathExpression expr = xPath.compile(query);
		NodeList list = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		log.info("Found {} section elements for {}", list.getLength(), courseID);
		
		Node node;
		ConcurrentMap<String, String> courseData = new ConcurrentHashMap<>();
		List<Node> nodeList = new ArrayList<Node>();
		
		courseDataSets.put(courseID, courseData);
		for(int item = 0; item < list.getLength(); item++){
			node = list.item(item).cloneNode(true);
			nodeList.add(node);
		}
		
		return new CourseParserBySectionXMLTask(nodeList, settings, courseID, courseData);
	}
	
	private void executeBatches(XPath xPath, Preferences settings, Set<String> courses) {
		Preferences generalSettings = settings.node(GENERAL_SETTINGS_NODE);
		int batchSize = generalSettings.getInt(BATCH_SIZE_PROPERTY, Integer.MAX_VALUE);
		log.info("Using batch size of {}", batchSize);

		List<RecursiveAction> batches = new ArrayList<RecursiveAction>();
		List<RecursiveAction> coursesBatch = new ArrayList<RecursiveAction>();
		int coursesBatched = 0;
		
		for(String courseID: courses) {
			RecursiveAction task;
			try {
				task = createCourseTask(xPath, settings, courseID);
				coursesBatch.add(task);
			} catch (XPathExpressionException e) {
				log.error("Unable to creat background task for processing course {}", courseID);
			}

			//increment this separately from the task creation in case there is an issue with that step
			//this needs to be incremented for each course so that we can be sure to initiate the last 
			//batch in the instance that courses.size() % batchSize != 0 (which will be often)
			coursesBatched++;	
			
			//check if batch size is met or if all
			if(coursesBatch.size() == batchSize || coursesBatched == courses.size()) {
				RecursiveAction batch = createBatch(coursesBatch);
				batches.add(batch);
				batch.fork();
				coursesBatch = new ArrayList<>();
			}
		}
		
		//wait for the batches to finish processing
		log.info("Waiting for batches to finish");
		for(RecursiveAction action: batches) {
			action.join();
		}
		log.info("All batches finished");
	}
	
	private RecursiveAction createBatch(List<RecursiveAction> actions) {
		return new ActionBatch(actions);
	}
	
	
	
	private static class ActionBatch extends RecursiveAction {
		
		/**
		 * TODO Describe this field
		 */
		private Logger log = LoggerFactory.getLogger(getClass().getName());

		/**
		 * TODO Describe this field
		 */
		private List<RecursiveAction> actions;

		public ActionBatch(List<RecursiveAction> actions) {
			super();
			
			this.actions = actions;
		}
		
		@Override
		protected void compute() {
			log.info("Initiating batch processing of {} tasks", actions.size());
			invokeAll(actions);
			log.info("Batch processing of {} tasks completed", actions.size());
		}

		/**
		 * TODO Describe this method
		 *
		 * @return
		 */
		public List<RecursiveAction> getActions() {
			return actions;
		}
	}
	
	
	private static class CourseParserBySectionXMLTask extends RecursiveAction {
		
		private Logger log = LoggerFactory.getLogger(getClass().getName());
		private List<Node> nodeList;
		private ConcurrentMap<String, String> data;
		private Preferences retrievalSettings;
		private String id;
		
		public CourseParserBySectionXMLTask(List<Node> nodeList, Preferences settings, String courseID, ConcurrentMap<String, String> data) {
			super();
			
			this.nodeList = nodeList;
			this.data = data;
			this.id = courseID;
			this.retrievalSettings = settings;
		}

		@Override
		protected void compute() {
			log.info("Processing course: {}", id);
			long start = System.currentTimeMillis();
			
			XPath xPath = XPathFactory.newInstance().newXPath();
			Node node = nodeList.get(0);
			captureCourseData(xPath, retrievalSettings, node, id, data);
			
			for(int item = 0; item < nodeList.size(); item++){
				node = nodeList.get(item);
				
				captureSectionData(xPath, retrievalSettings, node, item, data);
			}

			long end = System.currentTimeMillis();
			log.info("Finished processing course {} in {} milliseconds", id, (end - start));
		}
		
		private void captureCourseData(XPath xPath, Preferences settings, Node node, String courseID, Map<String, String> courseData) {
			log.debug("Capturing course data for {}", courseID);
			long start = System.currentTimeMillis();
			retrieveData(xPath, node, data, settings.node(COURSE_CODES_NODE));
			long end = System.currentTimeMillis();
			log.debug("Finished processing course data for {} in {} milliseconds", courseID, (end - start));
		}
		
		private void captureSectionData(XPath xPath, Preferences settings, Node node, int sectionIndex, Map<String, String> data) {
			log.debug("Capturing course data for section index {}", sectionIndex);
			long start = System.currentTimeMillis();
			retrieveData(xPath, node, "course.sections", "course.sections." + sectionIndex, settings.node(SECTION_CODES_NODE), data);
			long end = System.currentTimeMillis();
			log.debug("Capturing course data for section index {} in {} milliseconds", sectionIndex, (end - start));
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
				String count = Integer.toString(children.getLength());
				data.put(keyPath, count);
				log.trace("Element: {} ( \" {} \" ) = {}", new Object[] {keyPath, query, count});
							
				//process each item
				for(int item = 0; item < children.getLength(); item++){
					Node child = children.item(item).cloneNode(true);
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
						retrieveData(xPath, child, attributePath, keyPath + "." + item, subCodes, data);
					}else{					
						String itemKey = keyPath + "." + item;
						String value = child.getTextContent();
						data.put(itemKey, value);
						log.trace("Element: {} ( \" text() \" ) = {}", itemKey, count);
					}
				}
			} catch(XPathExpressionException e){
				log.error("Exception retrieving data element for attribute {} at keypath {}", attributePath, keyPath, e);
			} catch (DOMException | BackingStoreException e) {
				log.error("Exception reading profile entries for profile node {}: {}", codes, e);
			}
		}
	}
}
