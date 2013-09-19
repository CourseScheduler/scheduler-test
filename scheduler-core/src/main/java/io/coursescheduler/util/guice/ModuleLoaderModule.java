/**
  * @(#)ModuleLoaderModule.java
  *
  * An abstract boot-strapping Guice module that automatically loads other Guice modules based on the implementation
  * details. Implementations of this class are used to simplify the boot-strapping of a Guice Injector instance and
  * simplify application startup by allowing the modules to be discovered, installed, and configured at runtime
  * instead of compile-time.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * An abstract boot-strapping Guice module that automatically loads other Guice modules based on the implementation
 * details. Implementations of this class are used to simplify the boot-strapping of a Guice Injector instance and
 * simplify application startup by allowing the modules to be discovered, installed, and configured at runtime
 * instead of compile-time.
 *
 * @author Mike Reinhold
 *
 */
public abstract class ModuleLoaderModule extends AbstractModule {

	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Retrieve the class corresponding to the specified name, create an instance of the Guice module, and
	 * install that module into the injector in which this module is being installed.
	 *
	 * @param moduleClassName the fully qualified class name of the Guice module to install
	 */
	protected void installModule(String moduleClassName){
		try {
			log.debug("Preparing to build Guice Module: {}", moduleClassName);
			
			@SuppressWarnings("unchecked")		//we're catching the cast exception, so this should be safe
			Class<? extends Module> clazz = (Class<? extends Module>)Class.forName(moduleClassName);
			log.info("Successfully built Guice Module: {}", moduleClassName);
			
			installModule(clazz);
		} catch (ClassNotFoundException | ClassCastException e) {
			log.error("Unable to access class for Guice Module: " + moduleClassName,  e);
		}
	}
	
	/**
	 * Create an instance of the Guice module represented by the class parameter and install that module into
	 * the injector in which this loader module is being installed.
	 *
	 * @param moduleClass a class instance representing the Guice module
	 */
	protected void installModule(Class<? extends Module> moduleClass) {
		try {
			log.debug("Preparing to install Guice Module: {}", moduleClass);
			installModule(moduleClass.newInstance());
			log.info("Successfully installed Guice Module: {}", moduleClass);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("Unable to create a new instance of the Guice module: {}", moduleClass, e);
		}
	}
	
	/**
	 * Install the specified Guice Module into the injector in which this loader module is being installed.
	 *
	 * @param module the module to install into the injector
	 */
	protected void installModule(Module module) {
		log.debug("Preparing to install Guice Module: {}", module);
		install(module);
		log.info("Successfully installed Guice Module: {}", module);
	}
}
