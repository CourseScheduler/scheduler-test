/**
  * @(#)GroovyShellFactory.java
  *
  * GroovyShellFactory interface for creating GroovyShells based on Preferences node
  * configuration.
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

import groovy.lang.GroovyShell;

import java.util.prefs.Preferences;

import com.google.inject.ImplementedBy;

/**
 * GroovyShellFactory interface for creating GroovyShells based on Preferences node
 * configuration.
 * 
 * Default implementation is {@link DefaultGroovyShellFactory} however
 * this can be overridden in a module by binding an alternative implementation
 *
 * @author Mike Reinhold
 *
 */
@ImplementedBy(DefaultGroovyShellFactory.class)
public interface GroovyShellFactory {

	/**
	 * Preferences property containing the base class for scripts that will be processed by the 
	 * Groovy parser tool.
	 * 
	 * Value: {@value}
	 */
	public static final String SCRIPT_BASE_CLASS_PROPERTY = "script.base";
	
	/**
	 * Default value for checks against {@link #SCRIPT_BASE_CLASS_PROPERTY}. If the base class is
	 * not specified, then no base class will be used.
	 * 
	 * Value: {@value}
	 */
	public static final String SCRIPT_BASE_CLASS_MISSING = "";
	
	/**
	 * Create a new GroovyShell based on the specified configuration node.
	 * Configuration node should contain the necessary configuration properties.
	 *
	 * @param settings the Preferences node containing the configuration
	 * @return the GroovyShell ready to execute scripts
	 */
	public GroovyShell getGroovyShell(Preferences settings);
	
}
