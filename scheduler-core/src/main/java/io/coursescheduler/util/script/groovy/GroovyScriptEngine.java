/**
  * @(#)GroovyScriptEngine.java
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
package io.coursescheduler.util.script.groovy;

import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import groovy.lang.GroovyShell;
import io.coursescheduler.util.script.engine.AbstractScriptEngine;
import io.coursescheduler.util.variable.StrSubstitutorFactory;

/**
 * Groovy script based parsing tool to perform transformations on data
 *
 * @author Mike Reinhold
 *
 */
public class GroovyScriptEngine extends AbstractScriptEngine {
	
	/**
	 * DEfault script that will be used in the event a script is not configured
	 * 
	 * Value: {@value}
	 */
	private static final String DEFAULT_SCRIPT = "return \"${" + SOURCE_STRING_VARIABLE + "}\";";
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
		
	/**
	 * GroovyShell factory for creating GroovyShell
	 */
	private GroovyShell shell;
	
	@AssistedInject
	public GroovyScriptEngine(StrSubstitutorFactory substFactory, GroovyShellFactory shellFactory, @Assisted("config") Preferences config) {
		super(substFactory);
		
		log.trace("Preparing the GroovyShell");
		this.shell = shellFactory.getGroovyShell(config);
	}

	/* (non-Javadoc)
	 * @see io.coursescheduler.util.script.engine.AbstractScriptEngine#executeScript(java.lang.String)
	 */
	@Override
	protected Object executeScript(String script) {
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

	/* (non-Javadoc)
	 * @see io.coursescheduler.util.script.engine.AbstractScriptEngine#getDefaultScript()
	 */
	@Override
	protected String getDefaultScript() {
		return DEFAULT_SCRIPT;
	}
	
}
