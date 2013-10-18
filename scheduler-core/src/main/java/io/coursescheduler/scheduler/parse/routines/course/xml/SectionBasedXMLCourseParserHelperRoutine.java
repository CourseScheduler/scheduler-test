/**
  * @(#)SectionBasedXMLCourseParserHelperRoutine.java
  *
  * Parser helper to perform organization specific extraction of course data from the XML document.
  * This ParserHelperRoutine is designed for XML documents where the data elements are unique based
  * on the section and each section element contains all the course data (duplicated per section).
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

import io.coursescheduler.scheduler.parse.ParseException;
import io.coursescheduler.scheduler.parse.ParseConstants;
import io.coursescheduler.scheduler.parse.tools.xml.XMLParserTool;
import io.coursescheduler.scheduler.parse.tools.xml.XMLParserToolMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Parser helper to perform organization specific extraction of course data from the XML document.
 * This ParserHelperRoutine is designed for XML documents where the data elements are unique based
 * on the section and each section element contains all the course data (duplicated per section).
 *
 * @author Mike Reinhold
 *
 */
public class SectionBasedXMLCourseParserHelperRoutine extends XMLCourseParserHelperRoutine {
	
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
	 * Course ID that this instance is processing
	 */
	private String id;
	
	/**
	 * Create a new SectionBasedXMLCourseParserHelperRoutine instance
	 *
	 * @param toolMap XMLParserToolMap for retrieving the correct XML tool
	 * @param nodeList the list of XML document nodes to process
	 * @param settings the preferences node containing the configuration for the parsing
	 * @param courseID the course that is being retrieved
	 * @param data thread safe map for storing the course data
	 */
	@AssistedInject
	public SectionBasedXMLCourseParserHelperRoutine(XMLParserToolMap toolMap, @Assisted("nodes") List<Node> nodeList, @Assisted("settings") Preferences settings, @Assisted("courseid") String courseID, @Assisted("data") ConcurrentMap<String, String> data) {
		super();
		
		this.nodeList = nodeList;
		this.data = data;
		this.id = courseID;
		this.retrievalSettings = settings;
		this.parser = toolMap.getXMLParserTool(
			settings.get(XMLParserConstants.PARSER_TOOL_PROPERTY, null)
		);
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		log.info("Processing course: {}", id);
		long start = System.currentTimeMillis();
		
		try {
			Node node = nodeList.get(0);
			captureCourseData(retrievalSettings, node, id, data);
			
			for(int item = 0; item < nodeList.size(); item++){
				node = nodeList.get(item);
				
				captureSectionData(retrievalSettings, node, item, data);
			}
		} catch(ParseException e) {
			log.error("Processing course {} failed - course may not be present or correct in output", id, e);
			completeExceptionally(e);
		} 

		long end = System.currentTimeMillis();
		log.info("Finished processing course {} in {} milliseconds", id, (end - start));
	}
	
	/**
	 * Retrieve course related data from the XML node 
	 *
	 * @param settings the preferences node with the parser tool configuration data
	 * @param node the XML node on which the parser tool will execute
	 * @param courseID the course id for which data will be retrieved
	 * @param courseData the thread safe map which will store the course data
	 * @throws ParseException if there is an issue parsing the xml document 
	 */
	private void captureCourseData(Preferences settings, Node node, String courseID, Map<String, String> courseData) throws ParseException {
		log.debug("Capturing course data for {}", courseID);
		long start = System.currentTimeMillis();
		parser.retrieveData(node, settings.node(ParseConstants.COURSE_SETTINGS_NODE), courseData);
		long end = System.currentTimeMillis();
		log.debug("Finished processing course data for {} in {} milliseconds", courseID, (end - start));
	}
	
	/**
	 * Retrieve the section data from the XML node
	 *
	 * @param settings the preferences node with the parser tool configuration data
	 * @param node the XML node on which the parser tool will execute
	 * @param sectionIndex the index of this section in the course
	 * @param courseData the thread safe map which will store the course data
	 * @throws ParseException Factory interface for creating Stream Parser routines
	 */
	private void captureSectionData(Preferences settings, Node node, int sectionIndex, Map<String, String> courseData) throws ParseException {
		log.debug("Capturing course data for section index {}", sectionIndex);
		long start = System.currentTimeMillis();
		parser.retrieveData(node, settings.node(ParseConstants.SECTION_SETTINGS_NODE), "course.sections", "course.sections." + sectionIndex, courseData);
		long end = System.currentTimeMillis();
		log.debug("Capturing course data for section index {} in {} milliseconds", sectionIndex, (end - start));
	}
}
