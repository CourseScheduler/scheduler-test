/**
  * @(#)ServiceLoaderModule.java
  *
  * A Guice module which finds and loads other Guice modules based on the service implementations available through
  * the JVM ServiceLoader. Only Guice modules which are present in the services entry for the type specified in this
  * instance will be loaded, built, and installed into the injector. Any other Guice modules will be ignored by this
  * loader.
  * 
  * This loader module is best fitted to the common scenario of application components that may have multiple
  * available implementations, where one or more of the implementations are allowed to be active in the application
  * at once (generally this comes in the form of Guice multi-bindings, though other forms can exist).
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
package io.coursescheduler.util.guice.service;

import io.coursescheduler.util.guice.ModuleLoaderModule;

import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

/**
 * A Guice module which finds and loads other Guice modules based on the service implementations available through
 * the JVM ServiceLoader. Only Guice modules which are present in the services entry for the type specified in this
 * instance will be loaded, built, and installed into the injector. Any other Guice modules will be ignored by this
 * loader.
 * 
 * This loader module is best fitted to the common scenario of application components that may have multiple
 * available implementations, where one or more of the implementations are allowed to be active in the application
 * at once (generally this comes in the form of Guice multi-bindings, though other forms can exist).
 *
 * @author Mike Reinhold
 *
 */
public class ServiceLoaderModule<M extends Module> extends ModuleLoaderModule {

	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * The parent class for which sub-types should be searched. This allows for more selective 
	 */
	private Class<M> serviceType;
	
	/**
	 * Create a new Guice Service Loader Module which will use the Java ServiceLoader to 
	 * find Guice modules for the corresponding service type. 
	 *
	 * @param serviceType the service type to use when searching for Guice modules
	 * @return the newly constructed ServiceLoaderModule
	 */
	public static <M extends Module> ServiceLoaderModule<M> of(Class<M> serviceType){
		return new ServiceLoaderModule<M>(serviceType);
	}
	
	/**
	 * Create a new Guice Service Loader Module which will use the Java ServiceLoader to 
	 * find Guice modules for the corresponding service type. 
	 *
	 * @param serviceType the service type to use when searching for Guice modules
	 */
	protected ServiceLoaderModule(Class<M> serviceType){
		super();
		
		this.serviceType = serviceType;
	}
	
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		long start = System.currentTimeMillis();
		log.debug("Processing service loaded Guice modules");
		ServiceLoader<M> modules = ServiceLoader.load(serviceType);
        for (Module module : modules) {
			log.info("Found module {} while processing service {}", module, serviceType);
            installModule(module);
        }
        long end = System.currentTimeMillis();
        log.info("Finished processing service loaded modules for type {} in {} milliseconds", serviceType, (end - start));
	}
	
}
