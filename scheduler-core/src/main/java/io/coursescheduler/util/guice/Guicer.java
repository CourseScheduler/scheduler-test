/**
  * @(#)Guicer.java
  *
  * Guice bootstrap helper class to handle module loading based on Java system properties.
  * The application will provide a Map of module names and their default implementations. 
  * Java system properties will be checked for overrides to those defaults as well as for
  * custom modules in addition to the application standard modules.
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
package io.coursescheduler.util.guice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Guice bootstrap helper class to handle module loading based on Java system properties.
 * The application will provide a Map of module names and their default implementations. 
 * Java system properties will be checked for overrides to those defaults as well as for
 * custom modules in addition to the application standard modules.
 *
 * @author Mike Reinhold
 *
 */
public class Guicer {

	/**
	 * Java system property containing a comma separated list of fully qualified Class names corresponding
	 * to custom Guice modules that should be loaded by the application.
	 */
	public static final String CUSTOM_MODULES_PROPERTY = "io.coursescheduler.util.guice.modules.custom";
	
	/**
	 * Base component of the Java system property that is used to override the default implementation class
	 * provided by the application for a specific component. For instance, if the application has a component
	 * named "ui" that has a module loaded by Guice, this module could be overridden by supplying an alternate
	 * module implementation in the Java system property {@value}ui.
	 */
	public static final String STANDARD_MODULES_BASE_PROPERTY = "io.coursescheduler.util.guice.modules.owerride.";
	
	/**
	 * The map of application components to default implementation classes
	 */
	private Map<String, String> defaultModules;
	
	/**
	 * The map of application components to active implementation classes
	 */
	private Map<String, String> activeModules;
	
	/**
	 * Instance specific logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Create a Guice bootstrapper that loads Guice modules based on application specified defaults, system
	 * property specified overrides, and system property specified custom modules
	 *
	 * @param modules a map fo the application components to the default module implementation classes
	 */
	public Guicer(Map<String, String> modules){
		super();
		
		this.defaultModules = modules;
		this.activeModules = new HashMap<String, String>();
	}
	
	/**
	 * Initialize the Guice subsystem by retrieving the list of standard and custom application 
	 * modules and using them to create and configure an injector.
	 *
	 * @return the Guice injector
	 */
	public Injector initialize(){
		return Guice.createInjector(getModules());
	}
	
	/**
	 * Retrieve a list of built Guice modules that will be used during injector
	 * construction and Guice configuration
	 *
	 * @return the list list of Guice modules
	 */
	private List<Module> getModules(){
		log.info("Preparing to initialize Guice modules");
		List<Module> moduleList = new ArrayList<Module>();
		
		log.info("Building application standard modules");
		for(String moduleName: getStandardModuleNames()){
			buildModule(moduleList, moduleName);
		}
		
		log.info("Building custom modules");
		for(String moduleName: getCustomModuleNames()){
			buildModule(moduleList, moduleName);
		}
		
		log.info("Finished building Guice Modules");
		return moduleList;
	}
	
	/**
	 * Retrieve the list of application standard component modules that should be included by 
	 * Guice. These standard components are specified by the application in the construction of
	 * this instance and overridden via Java system properties that use the component name 
	 * prefixed by {@link #STANDARD_MODULES_BASE_PROPERTY}.
	 * 
	 *
	 * @return the list of module names
	 */
	private Collection<String> getStandardModuleNames(){
		log.debug("Processing standard modules");
		for(Entry<String, String> entry: defaultModules.entrySet()){
			String key = entry.getKey();
			String defaultModule = entry.getValue();
			String property = STANDARD_MODULES_BASE_PROPERTY + key;
			
			log.debug("Checking for standard component {} override module in {}", key, property);
			String newModule = System.getProperty(property); 
			if(newModule != null){
				log.debug("Default module {} has been overridden with {}", defaultModule, newModule);
				log.info("Found override module {} for standard component {}", newModule, key);
				activeModules.put(key, newModule);
			}else{
				log.debug("No override module found for standard module {}", key);
				log.info("Using default module {} for standard component {}", defaultModule, key);
				activeModules.put(key, defaultModule);
			}
		}
		log.info("Finished processing standard modules");
		return activeModules.values();
	}

	/**
	 * Retrieve the list of application custom modules that should be included by Guice. This
	 * is retrieved from the Java system property specified by {@ #CUSTOM_MODULES_PROPERTY}.
	 *
	 * @return the list of module names
	 */
	private Collection<String> getCustomModuleNames(){
		String moduleString = System.getProperty(CUSTOM_MODULES_PROPERTY);
		String[] modules;
		
		if(moduleString != null){
			modules = moduleString.split(",");
			log.info("Found custom module list: {}", moduleString);
		}else{
			modules = new String[]{};
			log.info("No custom modules found");
		}
		return Arrays.asList(modules);
	}
	
	/**
	 * Load the class implementation and construct an instance of the Guice module. Add it
	 * to the module list.
	 *
	 * @param moduleList the list of module names that will be configured by Guice
	 * @param moduleName the module to load and instantiate
	 */
	private void buildModule(List<Module> moduleList, String moduleName){
		try {
			log.debug("Preparing to build Guice Module: {}", moduleName);
			moduleList.add((Module)Class.forName(moduleName).newInstance());
			log.info("Successfully built Guice Module: {}", moduleName);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			log.error("Unable to build Guice Module: " + moduleName,  e);
		}
	}
}
