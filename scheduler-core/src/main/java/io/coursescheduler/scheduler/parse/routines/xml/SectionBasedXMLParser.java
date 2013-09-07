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
package io.coursescheduler.scheduler.parse.routines.xml;


import io.coursescheduler.scheduler.parse.ParseActionBatch;
import io.coursescheduler.scheduler.parse.ParseException;
import io.coursescheduler.scheduler.parse.routines.SectionBasedParser;
import io.coursescheduler.scheduler.parse.tools.xml.XMLParserTool;
import io.coursescheduler.scheduler.parse.tools.xml.xpath.XPathParserTool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RecursiveAction;
import java.util.prefs.Preferences;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SectionBasedXMLParser extends SectionBasedParser {
	
	/**
	 * TODO Describe this field
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * TODO Describe this field
	 */
	private static final String BATCH_SIZE_PROPERTY = "batch.size";
	
	/**
	 * TODO Describe this field
	 */
	private static final String COURSE_NAME_FULL_LIST_PROPERTY = "query-all";
	
	/**
	 * TODO Describe this field
	 */
	private static final String COURSE_NAME_SINGLE_PROPERTY = "query-single";
	
	/**
	 * TODO Describe this field
	 */
	private static final String COURSE_ID_VARIABLE = "${course.id}";
	
	/**
	 * TODO Describe this field
	 */
	private static final String GENERAL_SETTINGS_NODE = "general";
	
	
	/**
	 * Instance specifice logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private Document doc;
	
	private Map<String, Map<String, String>> courseDataSets;

	private Preferences profile;
	
	private XMLParserTool parser;
	
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
		parser = new XPathParserTool();
	}

	@Override
	protected void compute() {
		log.info("Starting to parse the XML input");
		long start = System.currentTimeMillis();
		try {
			executeBatches(profile, getCourseNames(profile));
		} catch (Exception e) {
			log.error("Exception retrieving course names", e);
		}
		
		long end = System.currentTimeMillis();
		log.info("Retrieved course data for {} courses in {} milliseconds", courseDataSets.size(), (end - start));
	}
	
	private Set<String> getCourseNames(Preferences settings) throws ParseException{
		log.info("Retrieving course IDs from source data set");
		Set<String> courses = new TreeSet<String>();
		NodeList list = parser.retrieveNodeList(doc, settings.node(SectionBasedParser.COURSE_SETTINGS_NODE), COURSE_NAME_FULL_LIST_PROPERTY);
		
		for(int item = 0; item < list.getLength(); item++){
			Node node = list.item(item).cloneNode(true);
			String courseID = node.getTextContent();
			courses.add(courseID);
			log.debug("Found section belonging to {}", courseID);
		}

		log.debug("Finished retrieving course IDs from source data set");
		return courses;
	}
	
	private RecursiveAction createCourseTask(Preferences settings, String courseID) throws ParseException{
		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put(COURSE_ID_VARIABLE, courseID);
		NodeList list = parser.retrieveNodeList(doc, settings.node(SectionBasedParser.COURSE_SETTINGS_NODE), COURSE_NAME_SINGLE_PROPERTY, replacements);
		
		log.info("Found {} section elements for {}", list.getLength(), courseID);
		
		Node node;
		ConcurrentMap<String, String> courseData = new ConcurrentHashMap<>();
		List<Node> nodeList = new ArrayList<Node>();
		
		courseDataSets.put(courseID, courseData);
		for(int item = 0; item < list.getLength(); item++){
			node = list.item(item).cloneNode(true);
			nodeList.add(node);
		}
		
		return new XMLCourseParserBySection(nodeList, settings, courseID, courseData);
	}
	
	private void executeBatches(Preferences settings, Set<String> courses) {
		Preferences generalSettings = settings.node(GENERAL_SETTINGS_NODE);
		int batchSize = generalSettings.getInt(BATCH_SIZE_PROPERTY, Integer.MAX_VALUE);
		log.info("Using batch size of {}", batchSize);

		List<RecursiveAction> batches = new ArrayList<RecursiveAction>();
		List<RecursiveAction> coursesBatch = new ArrayList<RecursiveAction>();
		int coursesBatched = 0;
		
		for(String courseID: courses) {
			RecursiveAction task;
			try {
				task = createCourseTask(settings, courseID);
				coursesBatch.add(task);
			} catch (Exception e) {
				log.error("Unable to create background task for processing course {}", courseID);
			}

			//increment this separately from the task creation in case there is an issue with that step
			//this needs to be incremented for each course so that we can be sure to initiate the last 
			//batch in the instance that courses.size() % batchSize != 0 (which will be often)
			coursesBatched++;	
			
			//check if batch size is met or if all
			if(coursesBatch.size() == batchSize || coursesBatched == courses.size()) {
				RecursiveAction batch = new ParseActionBatch(coursesBatch);
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
	
	/**
	 * @return the courseDataSets
	 */
	public Map<String, Map<String, String>> getCourseDataSets() {
		return courseDataSets;
	}
}