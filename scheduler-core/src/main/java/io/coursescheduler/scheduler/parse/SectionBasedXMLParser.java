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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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
		
		sectionCodes.put("section.meetings", "WeekDays/WeekDays_ROW");
		Map<String, String> meetingsCodes = new HashMap<>();
		meetingsCodes.put("day", "Day");
		meetingsCodes.put("building.code", "BuildCode");
		meetingsCodes.put("building.name", "Building");
		meetingsCodes.put("room", "Room");
		meetingsCodes.put("time.start", "STime");
		meetingsCodes.put("time.end", "ETime");
		subCodes.put("section.meetings", meetingsCodes);
	}
	
	private Document doc;
	
	private Map<String, Map<String, String>> courseData;
	
	private Map<String, Map<String, Map<String, String>>> courseSectionData;
	
	private SectionBasedXMLParser(){
		courseData = new HashMap<>();
		courseSectionData = new HashMap<>();
	}
	
	public SectionBasedXMLParser(InputStream input) throws ParserConfigurationException, SAXException, IOException{
		this();
				
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(input);
	}
	
	public void parse(){
		XPath xPath = XPathFactory.newInstance().newXPath();
		
		try {
			XPathExpression expr = xPath.compile("//ROWSET/ROW");
			
			NodeList list = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			for(int item = 0; item < list.getLength(); item++){
				Node node = list.item(item);
				
				String courseID = captureCourseData(xPath, node);
				String sectionID = captureCourseSectionData(xPath, node, courseID);
			}
			
		} catch (XPathExpressionException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		}
		
	}
	
	private String captureCourseData(XPath xPath, Node node) throws XPathExpressionException{
		System.out.println("\n----ROW-------------");
		
		String courseID = (String)xPath.evaluate(courseCodes.get("course.id"), node, XPathConstants.STRING);
		if(!courseData.containsKey(courseID)){
			courseData.put(courseID, new HashMap<String, String>());
			retrieveData(xPath, node, courseData.get(courseID), courseCodes);
		}else{
			System.out.println("Course data captured for " + courseID + " during parse of previous section");
		}
		return courseID;
	}
	
	private String captureCourseSectionData(XPath xPath, Node node, String courseID) throws XPathExpressionException{
		String sectionID = (String)xPath.evaluate(sectionCodes.get("section.id"), node, XPathConstants.STRING);
		Map<String, Map<String, String>> course;
		
		if(!courseSectionData.containsKey(courseID)){
			course = new HashMap<String, Map<String, String>>();
			courseSectionData.put(courseID, course);
		}else{
			course = courseSectionData.get(courseID);
		}
		
		Map<String, String> sectionData = new HashMap<String, String>();
		course.put(sectionID, sectionData);
		retrieveData(xPath, node, sectionData, sectionCodes);
		
		return sectionID;
	}
	
	//TODO change retrieve data logic
	//			always retrieve multi
	//			write number of nodes into {key}
	//			for each node found by {key}
	//				if sub query exists
	//					retrieve multi for {key}.index.{subkey} using {key}.{subkey} as query
	//				else no subquery exists
	//					retrieve single for {key}.index using {key}.{subkey} as query (default to text() ?)
	
	private void retrieveData(XPath xPath, Node node, Map<String, String> data, Map<String, String> codes){
		for(Entry<String, String> entry: codes.entrySet()){
			String key = entry.getKey();
			if(subCodes.containsKey(entry.getKey())){
				getMultiValue(xPath, key, entry.getValue(), subCodes.get(key), node, data);
			}else{
				getSingleValue(xPath, key, entry.getValue(), node, data);
			}
		}
	}
	
	private void retrieveData(XPath xPath, Node node, String parentKey, Map<String, String> codes, Map<String, String> data){
		
	}
	
	private void getMultiValue(){
		
	}
	
	private void getSingleValueLegacy(XPath xPath, String key, String query, Node node, Map<String, String> data){
		String result;
		try {
			result = (String)xPath.evaluate(query, node, XPathConstants.STRING);
			data.put(key, result);
			System.out.println(key + " ( \"" + query + "\" ) = " + result);
		} catch (XPathExpressionException e) {
			// TODO CATCH STUB
			e.printStackTrace();
			System.err.println(key + " ( \"" + query + "\" )");
		}
	}
	
	private void getMultiValueLegacy(XPath xPath, String key, String query, String subQuery, Node node, Map<String, String> data){
		NodeList subNodes;
		try{
			subNodes = (NodeList)xPath.evaluate(query, node, XPathConstants.NODESET);
			
			for(int item = 0; item < subNodes.getLength(); item++){
				Node subNode = subNodes.item(item);
				getSingleValue(xPath, key+"."+item, subQuery, subNode, data);
			}
			
			
		} catch (XPathExpressionException e){
			e.printStackTrace();
			System.err.println(key + " ( \"" + query + "\" ) -> ( \"" + subQuery + "\")");
		}
	}
	
	private void printElement(Node node){
		String name = node.getLocalName();
		if(name != null && name.compareTo("#Text") != 0){
			System.out.println(node.getLocalName() + ":" + node.getTextContent());
		}
	}
	
	private void printChildElements(Node node){		
		NodeList children = node.getChildNodes();
		
		for(int child = 0; child < children.getLength(); child++){
			Node childNode = children.item(child);
			
			printElement(childNode);
		}
	}
}
