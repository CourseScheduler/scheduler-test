/**
  * @(#)XMLParserMasterRoutine.java
  *
  * A general XML parsing routine for extracting course data from XML formatted documents. This
  * is the master XML parsing routine that performs the work of scheduling additional helper
  * routines for individual courses
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
import io.coursescheduler.scheduler.parse.routines.ParserRoutine;
import io.coursescheduler.scheduler.parse.routines.query.QueryBasedParserRoutine;
import io.coursescheduler.scheduler.parse.tools.xml.DocumentBuilderProvider;
import io.coursescheduler.scheduler.parse.tools.xml.XMLParserTool;
import io.coursescheduler.scheduler.parse.tools.xml.XMLParserToolMap;

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

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import static io.coursescheduler.scheduler.parse.routines.xml.XMLParserConstants.*;

/**
 * A general XML parsing routine for extracting course data from XML formatted documents. This
 * is the master XML parsing routine that performs the work of scheduling additional helper
 * routines for individual courses
 *
 * @author Mike Reinhold
 *
 */
public class XMLParserMasterRoutine extends QueryBasedParserRoutine<Node> {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Component based logger
	 */
	private transient Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private DocumentBuilderProvider builderProvider;

	private InputStream input;
	
	/**
	 * The XML Parser Tool that will be used to process the XML document to extract nodes
	 */
	private transient XMLParserTool parser;
	
	/**
	 * Parser Helper Routine Factory for creating parser routines
	 */
	private transient XMLParserHelperRoutineFactory parserHelperFactory;
	
	/**
	 * Create a new XMLParserMasterRoutine instance using the specified input stream and the preferences node
	 * containing the configuration necessary to process the data from the XML document represented by
	 * the input stream
	 * 
	 * @param helperMap the XMLParserHelperRoutine mapping instance to use for retrieving the helper parser routine
	 * @param toolMap the ParserTool mapping instance to use for retrieving a ParserTool
	 * @param builderProvider a provider for getting a DocumentBuilder instance which is used to create the XML document from the input stream
	 * @param input the input stream from which the XML document can be obtained
	 * @param profile the Preferences node that contains the configuration necessary to parse the xml document
	 *
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies the configuration requested.
	 * @throws SAXException if any parse error occurs
	 * @throws IOException if any io error occurs
	 */
	@AssistedInject
	public XMLParserMasterRoutine(XMLParserHelperMap helperMap, XMLParserToolMap toolMap, DocumentBuilderProvider builderProvider, @Assisted("source") InputStream input, @Assisted("profile") Preferences profile) throws ParserConfigurationException, SAXException, IOException{
		super(toolMap, profile);
		
		this.builderProvider = builderProvider;
		this.input = input;
		this.parser = toolMap.getXMLParserTool(profile.get(PARSER_TOOL_PROPERTY, null));
		parserHelperFactory = helperMap.getXMLCourseParserHelperRoutineFactory(
			profile.get(PARSER_HELPER_PROPERTY, null)
		); 
	}	
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.routines.query.QueryBasedParserRoutine#prepareInput()
	 */
	@Override
	protected Document prepareInput() throws Exception {
		return builderProvider.get().parse(input);
	}

	@Override
	protected String asString(Node item) {
		return item.getTextContent();
	}

}
