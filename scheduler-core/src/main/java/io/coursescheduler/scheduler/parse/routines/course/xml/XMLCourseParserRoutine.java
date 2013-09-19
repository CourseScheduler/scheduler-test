/**
  * @(#)XMLCourseParserRoutine.java
  *
  * A general XML parsing routine for extracting course data from XML formatted documents
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
package io.coursescheduler.scheduler.parse.routines.course.xml;


import io.coursescheduler.scheduler.parse.ParseActionBatch;
import io.coursescheduler.scheduler.parse.ParseException;
import io.coursescheduler.scheduler.parse.routines.course.CourseParserRoutine;
import io.coursescheduler.scheduler.parse.routines.course.SectionBasedCourseParserRoutine;
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

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * A general XML parsing routine for extracting course data from XML formatted documents
 *
 * @author Mike Reinhold
 *
 */
public class XMLCourseParserRoutine extends CourseParserRoutine {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Course name query to find all course names in the source document
	 */
	private static final String COURSE_NAME_FULL_LIST_PROPERTY = "query-all";
	
	/**
	 * Course name query to find all nodes in the source document that match
	 * the course ID passed into the query via the Course ID substitution 
	 * placeholder ({@link #COURSE_ID_VARIABLE}
	 */
	private static final String COURSE_NAME_SINGLE_PROPERTY = "query-single";
	
	/**
	 * Course ID placeholder used in the {@link #COURSE_NAME_SINGLE_PROPERTY} expression
	 * to substitute the correct course id into the XML search expression
	 */
	private static final String COURSE_ID_VARIABLE = "${course.id}";
	
	/**
	 * Instance specific logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * XML Document for this XMLCourseParserRoutine to process
	 */
	private Document doc;

	/**
	 * The {@link java.util.prefs.Preferences} node containing the configuration for this XMLCourseParserRoutine
	 */
	private Preferences profile;
	
	/**
	 * The XML Parser Tool that will be used to process the XML document to extract nodes
	 */
	private XMLParserTool parser;
	
	/**
	 * Create a new XMLCourseParserRoutine instance using the specified input stream and the preferences node
	 * containing the configuration necessary to process the course data from the XML document represented by
	 * the input stream
	 *
	 * @param input the input stream from which the XML document can be obtained
	 * @param profile the Preferences node that contains the configuration necessary to parse the xml document
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies the configuration requested.
	 * @throws SAXException if any parse error occurs
	 * @throws IOException if any io error occurs
	 */
	@AssistedInject
	public XMLCourseParserRoutine(@Assisted("source") InputStream input, @Assisted("profile") Preferences profile) throws ParserConfigurationException, SAXException, IOException{
		super();
				
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(input);
		this.profile = profile;
		parser = new XPathParserTool();
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		log.info("Starting to parse the XML input");
		long start = System.currentTimeMillis();
		try {
			executeBatches(profile, getCourseIDs(profile));
		} catch (Exception e) {
			log.error("Exception retrieving course names", e);
		}
		
		long end = System.currentTimeMillis();
		log.info("Retrieved course data for {} courses in {} milliseconds", getCourseDataSets().size(), (end - start));
	}
	
	/**
	 * Retrieve the set of course IDs found within the XML document
	 *
	 * @param settings the preferences node containing the parser routine configuration
	 * @return a set containing the course ids retrieved from the XML document
	 * @throws ParseException if there is an issue parsing the course IDs from the document
	 */
	private Set<String> getCourseIDs(Preferences settings) throws ParseException{
		log.info("Retrieving course IDs from source data set");
		Set<String> courses = new TreeSet<String>();
		NodeList list = parser.retrieveNodeList(doc, settings.node(SectionBasedCourseParserRoutine.COURSE_SETTINGS_NODE), COURSE_NAME_FULL_LIST_PROPERTY);
		
		for(int item = 0; item < list.getLength(); item++){
			Node node = list.item(item).cloneNode(true);
			String courseID = node.getTextContent();
			courses.add(courseID);
			log.debug("Found row belonging to {}", courseID);
		}

		log.debug("Finished retrieving course IDs from source data set");
		return courses;
	}
	
	/**
	 * Create a new sub-task for processing the specified course ID using the specified preferences node
	 *
	 * @param settings the preferences node containing the parser routine configuration
	 * @param courseID the course ID for which this task will retrieve data
	 * @return the sub-task which will process the course data 
	 * @throws ParseException if there is an issue retrieving the list of nodes
	 */
	private CourseParserRoutine createCourseTask(Preferences settings, String courseID) throws ParseException{
		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put(COURSE_ID_VARIABLE, courseID);
		NodeList list = parser.retrieveNodeList(doc, settings.node(SectionBasedCourseParserRoutine.COURSE_SETTINGS_NODE), COURSE_NAME_SINGLE_PROPERTY, replacements);
		
		log.info("Found {} rows for {}", list.getLength(), courseID);
		
		Node node;
		ConcurrentMap<String, String> courseData = new ConcurrentHashMap<>();
		List<Node> nodeList = new ArrayList<Node>();
		
		getCourseDataSets().put(courseID, courseData);
		for(int item = 0; item < list.getLength(); item++){
			node = list.item(item).cloneNode(true);
			nodeList.add(node);
		}
		
		return new SectionBasedXMLCourseParserRoutine(nodeList, settings, courseID, courseData);
	}
	
	/**
	 * Build and execute a sub-task per course ID, batching the sub-tasks into task groups based on 
	 * the configured batch size.
	 * 
	 * This method blocks until all batches have completed processing (in any completion state, including failure)
	 *
	 * @param settings the preferences node containing the parser routine configuration
	 * @param courses the set of course IDs in the document that should be processed in batch
	 */
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
}
