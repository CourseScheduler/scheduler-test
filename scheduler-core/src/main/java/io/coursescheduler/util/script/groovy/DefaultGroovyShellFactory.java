/**
  * @(#)DefaultGroovyShellFactory.java
  *
  * Default GroovyShell factory class for retrieving a configured GroovyShell for executing
  * Groovy scripts
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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.prefs.Preferences;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Default GroovyShell factory class for retrieving a configured GroovyShell for executing
 * Groovy scripts
 *
 * @author Mike Reinhold
 *
 */
public class DefaultGroovyShellFactory implements GroovyShellFactory {
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Create a new Default GroovyShellFactory for creating GroovyShells
	 */
	@Inject
	public DefaultGroovyShellFactory() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see io.coursescheduler.util.script.groovy.GroovyShellFactory#getGroovyShell(java.util.prefs.Preferences)
	 */
	@Override
	public GroovyShell getGroovyShell(Preferences settings) {
			log.debug("Preparing to build Groovy compiler configuration");
			
			CompilerConfiguration configuration = new CompilerConfiguration();
			
			log.debug("Setting compiler target version based on running JVM version {}", CompilerConfiguration.currentJVMVersion);
			configuration.setTargetBytecode(CompilerConfiguration.currentJVMVersion);
			
			log.debug("Checking for script base class in {}", SCRIPT_BASE_CLASS_PROPERTY);
			String baseClass = settings.get(SCRIPT_BASE_CLASS_PROPERTY, SCRIPT_BASE_CLASS_MISSING);
			
			if(baseClass.compareTo(SCRIPT_BASE_CLASS_MISSING) != 0) {
				log.debug("{} configured as base class for script execution", baseClass);
				configuration.setScriptBaseClass(baseClass);
			}else {
				log.debug("No base class configured for script execution");
			}
			log.debug("Groovy compiler configuration ready");
			
			Binding binding = new Binding();
			log.debug("Preparing to build Groovy shell using class loader {}, binding {}, and configuration {}", new Object[] {
					getClass().getClassLoader(), binding, configuration 
			});
		    GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding, configuration);
		    
		    return shell;
	}
	
}
