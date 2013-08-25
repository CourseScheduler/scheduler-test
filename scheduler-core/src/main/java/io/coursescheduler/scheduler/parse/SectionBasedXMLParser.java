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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

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

	
	public static final String sectionExpression = "//ROWSET/ROW";
	
	public static final String defaultQuery = "text()";
	
	public static final Map<String, Map<String, String>> subCodes;
	
	public static final Map<String, String> courseCodes;
	
	static {
		subCodes = new HashMap<String, Map<String, String>>();
		courseCodes = new HashMap<String, String>();
		courseCodes.put("course.department.code", "DeparmentCode");
		courseCodes.put("course.department.name", "Department");
		courseCodes.put("course.title", "Title");
		courseCodes.put("course.id", "CourseID");
		courseCodes.put("course.term", "AssociatedTerm");
		courseCodes.put("course.registration", "RegistrationDates");
		courseCodes.put("course.catalog.url", "CatalogEntryURL");
		courseCodes.put("course.credits", "Credits");
		courseCodes.put("course.description", "Description");
		courseCodes.put("course.standing.minimum", "MinimumClassStanding");
		//corequisites
		//prerequisites
		//required sections
		courseCodes.put("course.levels", "Levels/Levels_ROW/Student");
	}
	
	public static final Map<String, String> sectionCodes;
	
	static {
		sectionCodes = new HashMap<String, String>();
		sectionCodes.put("section.id", "SectionID");
		sectionCodes.put("section.crn", "CRN");
		sectionCodes.put("section.type", "ScheduleType");
		sectionCodes.put("section.capacity.total", "TotalCapacity");
		sectionCodes.put("section.capacity.registered", "RegisteredCapacity");
		sectionCodes.put("section.capacity.remaining", "RemainingCapacity");
		
		//campus
		
		sectionCodes.put("section.meetings", "WEEKDAYS/WEEKDAYS_ROW");
		Map<String, String> meetingsCodes = new HashMap<>();
		meetingsCodes.put("day", "Day");
		meetingsCodes.put("building.code", "BuildCode");
		meetingsCodes.put("building.name", "Building");
		meetingsCodes.put("room", "Room");
		meetingsCodes.put("time.start", "Stime");
		meetingsCodes.put("time.end", "Etime");
		subCodes.put("course.sections.section.meetings", meetingsCodes);
	}
	
	/**
	 * Instance specifice logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().toString());
	
	private Document doc;
	
	private Map<String, Map<String, String>> courseDataSets;
	
	private SectionBasedXMLParser(){
		courseDataSets = new HashMap<>();
	}
	
	public SectionBasedXMLParser(InputStream input) throws ParserConfigurationException, SAXException, IOException{
		this();
				
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(input);
	}
	
	public void parse(){
		long start = System.currentTimeMillis();
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		
		try {
			for(String courseID: getCourseNames(xPath)){
				try {
					captureCourseData(xPath, courseID);
				} catch(XPathExpressionException e) {
					//TODO CATCH STUB
					e.printStackTrace();
				}
			}			
		} catch (XPathExpressionException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("Execution took " + (end - start) + " milliseconds.");
	}
	
	private Set<String> getCourseNames(XPath xPath) throws XPathExpressionException{
		Set<String> courses = new TreeSet<String>();
		
		XPathExpression expr = xPath.compile("//ROWSET/ROW/CourseID");
		
		NodeList list = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		for(int item = 0; item < list.getLength(); item++){
			Node node = list.item(item);
			String courseID = node.getTextContent();
			
			System.out.println("Found Course: " + courseID);
			courses.add(courseID);
		}

		return courses;
	}
	
	private void captureCourseData(XPath xPath, String courseID) throws XPathExpressionException {
		System.out.println("\nProcessing course: " + courseID);
		
		XPathExpression expr = xPath.compile("//ROWSET/ROW[CourseID='" + courseID + "']");
		NodeList list = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		int item = 0;
		Node node = list.item(item).cloneNode(false);
		
		Map<String, String> courseData = captureCourseData(xPath, node, courseID);
		
		for(; item < list.getLength(); item++){
			node = list.item(item).cloneNode(false);
			
			captureSectionData(xPath, node, item, courseData);
		}
	}
	
	private Map<String, String> captureCourseData(XPath xPath, Node node, String courseID) throws XPathExpressionException{
		Map<String, String> data = courseDataSets.get(courseID);
		if(!courseDataSets.containsKey(courseID)){
			data = new HashMap<String, String>();
			courseDataSets.put(courseID, data);
			retrieveData(xPath, node, data, courseCodes);
		}else{
			System.out.println("Course data captured for " + courseID + " during parse of previous section");
		}
		return data;
	}
	
	private void captureSectionData(XPath xPath, Node node, int sectionIndex, Map<String, String> data) {
		retrieveData(xPath, node, "course.sections", "course.sections." + sectionIndex, sectionCodes, data);
	}
	
	private void retrieveData(XPath xPath, Node node, Map<String, String> data, Map<String, String> codes){
		retrieveData(xPath, node, "", "", codes, data);
	}
	
	private void retrieveData(XPath xPath, Node node, String attributePath, String nodePath, Map<String, String> codes, Map<String, String> data){
		for(Entry<String, String> entry: codes.entrySet()){
			String key = entry.getKey();
			String query = entry.getValue();
			String newPath = (attributePath == null || attributePath.compareTo("") == 0) ? key : attributePath + "." + key;
			String newKey = (nodePath == null || nodePath.compareTo("") == 0) ? key : nodePath + "." + key;
						
			retrieveDataElement(xPath, node, newPath, newKey, key, query, data);
		}
	}
	
	private void retrieveDataElement(XPath xPath, Node node, String attributePath, String keyPath, String key, String query, Map<String, String> data){
		try{
			NodeList children = (NodeList)xPath.evaluate(query, node, XPathConstants.NODESET);
			
			//write the number of values
			String count = new Integer(children.getLength()).toString();
			data.put(keyPath, count);
			System.out.println(keyPath + " ( \"" + query + "\" ) = " + count);
			
			//process each item
			for(int item = 0; item < children.getLength(); item++){
				Node child = children.item(item).cloneNode(false);
				
				if(subCodes.containsKey(attributePath)){
					
					//subvalues to retrieve
					retrieveData(xPath, child, attributePath, keyPath + "." + item, subCodes.get(attributePath), data);
				}else{
					String itemKey = keyPath + "." + item;
					String value = child.getTextContent();
					data.put(itemKey, value);
					System.out.println(itemKey + " ( \"text()\" ) = " + value);
				}
			}
		} catch(XPathExpressionException e){
			//TODO CATCH STUB
			e.printStackTrace();
		}
	}
}
