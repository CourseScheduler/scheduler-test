/**
  * @(#)XMLCourseParserBySection.java
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
package io.coursescheduler.scheduler.parse.xml;

import io.coursescheduler.scheduler.parse.xml.xpath.XPathParser;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RecursiveAction;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * TODO Describe this type
 *
 * @author Mike Reinhold
 *
 */
public class XMLCourseParserBySection extends RecursiveAction {

	/**
	 * TODO Describe this field
	 */
	private static final String COURSE_CODES_NODE = "course_codes";
	
	/**
	 * TODO Describe this field
	 */
	private static final String SECTION_CODES_NODE = "section_codes";
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instance specific logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * TODO Describe this field
	 */
	private List<Node> nodeList;
	
	/**
	 * TODO Describe this field
	 */
	private ConcurrentMap<String, String> data;
	
	/**
	 * TODO Describe this field
	 */
	private Preferences retrievalSettings;
	
	/**
	 * TODO Describe this field
	 */
	private XPathParser parser;
	
	/**
	 * TODO Describe this field
	 */
	private String id;
	
	public XMLCourseParserBySection(List<Node> nodeList, Preferences settings, String courseID, ConcurrentMap<String, String> data) {
		super();
		
		this.nodeList = nodeList;
		this.data = data;
		this.id = courseID;
		this.retrievalSettings = settings;
		this.parser = new XPathParser();
	}

	@Override
	protected void compute() {
		log.info("Processing course: {}", id);
		long start = System.currentTimeMillis();
		
		Node node = nodeList.get(0);
		captureCourseData(retrievalSettings, node, id, data);
		
		for(int item = 0; item < nodeList.size(); item++){
			node = nodeList.get(item);
			
			captureSectionData(retrievalSettings, node, item, data);
		}

		long end = System.currentTimeMillis();
		log.info("Finished processing course {} in {} milliseconds", id, (end - start));
	}
	
	private void captureCourseData(Preferences settings, Node node, String courseID, Map<String, String> courseData) {
		log.debug("Capturing course data for {}", courseID);
		long start = System.currentTimeMillis();
		parser.retrieveData(node, settings.node(COURSE_CODES_NODE), data);
		long end = System.currentTimeMillis();
		log.debug("Finished processing course data for {} in {} milliseconds", courseID, (end - start));
	}
	
	private void captureSectionData(Preferences settings, Node node, int sectionIndex, Map<String, String> data) {
		log.debug("Capturing course data for section index {}", sectionIndex);
		long start = System.currentTimeMillis();
		parser.retrieveData(node, settings.node(SECTION_CODES_NODE), "course.sections", "course.sections." + sectionIndex, data);
		long end = System.currentTimeMillis();
		log.debug("Capturing course data for section index {} in {} milliseconds", sectionIndex, (end - start));
	}
}
