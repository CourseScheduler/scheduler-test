/**
  * @(#)GroovyParserTool.java
  *
  * Groovy script based parsing tool to perform transformations on data
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
package io.coursescheduler.scheduler.parse.tools.script.groovy;

import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import groovy.lang.GroovyShell;
import io.coursescheduler.scheduler.parse.tools.script.AbstractScriptParserTool;
import io.coursescheduler.util.script.groovy.GroovyShellFactory;
import io.coursescheduler.util.variable.StrSubstitutorFactory;

/**
 * Groovy script based parsing tool to perform transformations on data
 *
 * @author Mike Reinhold
 *
 */
public class GroovyParserTool extends AbstractScriptParserTool {
	
	/**
	 * ParserTool internal name used in configuration and in binding to uniquely identify
	 * the parser module. It must be unique among all other ParserTool modules or else it
	 * will not be available for use by data retrieval routines.
	 * 
	 * Value: {@value}
	 */
	public static final String PARSER_INTERNAL_NAME = "script-groovy";
	
	/**
	 * ParserTool external name which is displayed to the end user. This must be unique
	 * among all other ParserTool modules or else the user will not be able to properly
	 * differentiate between parser modules.
	 * 
	 * Value: {@value}
	 */
	public static final String PARSER_FRIENDLY_NAME = "Groovy Scripted Parser";
	
	/**
	 * Short description of the parser module. 
	 * 
	 * Value: {@value}
	 */
	public static final String PARSER_SHORT_DESCRIPTION = "A Groovy based parser that uses Groovy scripts";
	
	/**
	 * Long description of the parser module.
	 * 
	 * Value: {@value}
	 */
	public static final String PARSER_LONG_DESCRIPTION = "A Groovy based parser that uses Groovy "
			+ "scripts to parse data. The string value of the result of the Groovy script is used "
			+ "as the found value";
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
		
	/**
	 * GroovyShell factory for creating GroovyShell
	 */
	private GroovyShellFactory shellFactory;
	
	@Inject
	public GroovyParserTool(StrSubstitutorFactory substFactory, GroovyShellFactory shellFactory) {
		super(substFactory);
		
		this.shellFactory = shellFactory;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.ParserTool#getInternalName()
	 */
	@Override
	public String getInternalName() {
		return PARSER_INTERNAL_NAME;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.ParserTool#getUserFriendlyName()
	 */
	@Override
	public String getUserFriendlyName() {
		return PARSER_FRIENDLY_NAME;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.ParserTool#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return PARSER_SHORT_DESCRIPTION;
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.ParserTool#getLongDescription()
	 */
	@Override
	public String getLongDescription() {
		return PARSER_LONG_DESCRIPTION;
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.scheduler.parse.tools.script.AbstractScriptParserTool#executeScript(java.lang.String, java.util.prefs.Preferences)
	 */
	@Override
	protected Object executeScript(String script, Preferences settings) {
		log.trace("Preparing the GroovyShell");
		GroovyShell shell = shellFactory.getGroovyShell(settings);
		
		log.trace("Evaluating Groovy script {}", script);
		Object result;
		try {
			result = shell.evaluate(script);
		}catch(Exception e) {
			log.error("Exception caught while executing script {}, using \"\" as result", script);
			log.error("Exception during script evaluation", e);
			result = "";
		}
		
		log.trace("Script resulted in {}", result);
		return result;
	}
	
}
