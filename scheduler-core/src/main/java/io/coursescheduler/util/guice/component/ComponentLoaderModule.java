/**
  * @(#)ComponentLoaderModule.java
  *
  * A Guice module which finds and loads other Guice modules based on a map of application components and default
  * implementation modules. These default implementations can be overridden by specifying a Java system property
  * ({@value #STANDARD_MODULES_BASE_PROPERTY}${COMPONENT} where ${COMPONENT} is the component name in the map to
  * override).
  * 
  * This loader module is best fitted to the common scenario of application components that may have multiple
  * available implementations, with one particular implementation being specified as the preferred or default.
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
package io.coursescheduler.util.guice.component;

import io.coursescheduler.util.guice.ModuleLoaderModule;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Guice module which finds and loads other Guice modules based on a map of application components and default
 * implementation modules. These default implementations can be overridden by specifying a Java system property
 * ({@value #STANDARD_MODULES_BASE_PROPERTY}${COMPONENT} where ${COMPONENT} is the component name in the map to
 * override).
 * 
 * This loader module is best fitted to the common scenario of application components that may have multiple
 * available implementations and only one implementation can be active within the application at a item.
 * Additionally, one particular implementation is usually specified as the preferred or default.
 *
 * @author Mike Reinhold
 *
 */
public class ComponentLoaderModule extends ModuleLoaderModule {

	/**
	 * Base component of the Java system property that is used to override the default implementation class
	 * provided by the application for a specific component. For instance, if the application has a component
	 * named "ui" that has a module loaded by Guice, this module could be overridden by supplying an alternate
	 * module implementation in the Java system property {@value}ui.
	 * 
	 * Value: {@value}
	 */
	public static final String STANDARD_MODULES_BASE_PROPERTY = "io.coursescheduler.util.guice.component.modules.";
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Map of component names to default implementation modules
	 */
	private Map<String, String> defaultModules;
	
	/**
	 * Create a new Guice module loader that builds and installs Guice modules based on the provided map of component
	 * names and default implementations. These implementations can be overridden by setting Java system properties as
	 * specified by the {@link #STANDARD_MODULES_BASE_PROPERTY} system property prefix. 
	 * 
	 * @see io.coursescheduler.util.guice.component.STANDARD_MODULES_BASE_PROPERTY
	 *
	 * @param defaultModules a map of the component names and the default implementation modules
	 * @return the newly constructed ComponentLoaderModule
	 */
	public static ComponentLoaderModule of(Map<String, String> defaultModules) {
		return new ComponentLoaderModule(defaultModules);
	}
	
	/**
	 * Create a new Guice module loader that builds and installs Guice modules based on the provided map of component
	 * names and default implementations. These implementations can be overridden by setting Java system properties as
	 * specified by the {@link #STANDARD_MODULES_BASE_PROPERTY} system property prefix. 
	 * 
	 * @see io.coursescheduler.util.guice.component.STANDARD_MODULES_BASE_PROPERTY
	 *
	 * @param defaultModules a map of the component names and the default implementation modules
	 */
	protected ComponentLoaderModule(Map<String, String> defaultModules) {
		super();
		
		this.defaultModules = defaultModules;
	}
	
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		long start = System.currentTimeMillis();
		log.debug("Processing application component modules");
		for(Entry<String, String> entry: defaultModules.entrySet()){
			String key = entry.getKey();
			String defaultModule = entry.getValue();
			String property = STANDARD_MODULES_BASE_PROPERTY + key;
			
			log.debug("Checking for standard component {} override module in {}", key, property);
			String newModule = System.getProperty(property); 
			if(newModule != null){
				log.debug("Default module {} has been overridden with {}", defaultModule, newModule);
				log.info("Found override module {} for standard component {}", newModule, key);
				installModule(newModule);
			}else{
				log.debug("No override module found for standard module {}", key);
				log.info("Using default module {} for standard component {}", defaultModule, key);
				installModule(defaultModule);
			}
		}
		long end = System.currentTimeMillis();
		log.info("Finished processing standard modules is {} milliseconds", (end - start));
	}	
}
