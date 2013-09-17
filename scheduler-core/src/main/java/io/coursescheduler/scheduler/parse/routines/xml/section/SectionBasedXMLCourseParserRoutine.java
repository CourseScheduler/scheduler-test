/**
  * @(#)SectionBasedXMLCourseParserRoutine.java
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
package io.coursescheduler.scheduler.parse.routines.xml.section;

import io.coursescheduler.scheduler.parse.ParseException;
import io.coursescheduler.scheduler.parse.routines.SectionBasedCourseParserRoutine;
import io.coursescheduler.scheduler.parse.tools.xml.XMLParserTool;
import io.coursescheduler.scheduler.parse.tools.xml.xpath.XPathParserTool;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
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
public class SectionBasedXMLCourseParserRoutine extends SectionBasedCourseParserRoutine {
	
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
	private XMLParserTool parser;
	
	/**
	 * TODO Describe this field
	 */
	private String id;
	
	public SectionBasedXMLCourseParserRoutine(List<Node> nodeList, Preferences settings, String courseID, ConcurrentMap<String, String> data) {
		super();
		
		this.nodeList = nodeList;
		this.data = data;
		this.id = courseID;
		this.retrievalSettings = settings;
		this.parser = new XPathParserTool();
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
	
	private void captureCourseData(Preferences settings, Node node, String courseID, Map<String, String> courseData) throws ParseException {
		log.debug("Capturing course data for {}", courseID);
		long start = System.currentTimeMillis();
		parser.retrieveData(node, settings.node(SectionBasedCourseParserRoutine.COURSE_SETTINGS_NODE), data);
		long end = System.currentTimeMillis();
		log.debug("Finished processing course data for {} in {} milliseconds", courseID, (end - start));
	}
	
	private void captureSectionData(Preferences settings, Node node, int sectionIndex, Map<String, String> data) throws ParseException {
		log.debug("Capturing course data for section index {}", sectionIndex);
		long start = System.currentTimeMillis();
		parser.retrieveData(node, settings.node(SectionBasedCourseParserRoutine.SECTION_SETTINGS_NODE), "course.sections", "course.sections." + sectionIndex, data);
		long end = System.currentTimeMillis();
		log.debug("Capturing course data for section index {} in {} milliseconds", sectionIndex, (end - start));
	}
}
