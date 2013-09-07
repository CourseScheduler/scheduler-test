/**
  * @(#)ScanningLoaderModule.java
  *
  * A Guice module which finds and loads other Guice modules based on a base class which extends {@link com.google.inject.Module}
  * and a given set of package names. Only Guice modules which are subclasses of the specified class will be loaded
  * and installed into the injector. Any other Guice modules found within the package will be ignored by this loader.
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
package io.coursescheduler.util.guice.scan;

import java.util.concurrent.ForkJoinPool;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

import io.coursescheduler.util.guice.ModuleLoaderModule;

/**
 * A Guice module which finds and loads other Guice modules based on a base class which extends {@link com.google.inject.Module}
 * and a given set of package names. Only Guice modules which are subclasses of the specified class will be loaded
 * and installed into the injector. Any other Guice modules found within the package will be ignored by this loader.
 * 
 * This loader module is best fitted to the common scenario of application components that may have multiple
 * available implementations, where one or more of the implementations are allowed to be active in the application
 * at once (generally this comes in the form of Guice multi-bindings, though other forms can exist).
 *
 * @author Mike Reinhold
 *
 */
public class ScanningLoaderModule<M extends Module> extends ModuleLoaderModule {
	
	/**
	 * Instance specific logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * The package names which should be searched for implementations of {@link #parentType}
	 */
	private String[] packages;
	
	/**
	 * The parent class for which sub-types should be searched. This allows for more selective 
	 */
	private Class<M> parentType;
	
	/**
	 * Construct a new Guice Scanning Loader Module which will scan the specified packages for subtypes
	 * of the Guice Module class (or subclass) corresponding to the generic type M
	 *
	 * @param parentType the class to use as the parent type for modules loaded from the specified packages
	 * @param packages one or more package names as strings in which to search
	 * @return the newly constructed ScanningLoaderModule
	 */
	public static <M extends Module> ScanningLoaderModule<M> of(Class<M> parentType, String...packages){
		return new ScanningLoaderModule<M>(parentType, packages);
	}
	
	/**
	 * Create a new Guice Scanning Loader Module which will scan the specified packages for subtypes
	 * of the Guice Module class (or subclass) corresponding to the generic type M 
	 *
	 * @param parentType the class to use as the parent type for modules loaded from the specified packages
	 * @param packages one or more package names as strings in which to search
	 */
	protected ScanningLoaderModule(Class<M> parentType, String... packages) {
		super();
		
		this.parentType = parentType;
		this.packages = packages;
	}
	
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		long outerStart = System.currentTimeMillis();
		log.debug("Processing package scanned Guice modules");
		for(String pack: packages) {
			long start = System.currentTimeMillis();
			log.debug("Searching package {} for implementations of {}", pack, parentType.getName());
			Reflections reflections = new Reflections(new ConfigurationBuilder()
				.filterInputsBy(new FilterBuilder().includePackage(pack))
				.setUrls(ClasspathHelper.forPackage(pack))
				.setScanners(new SubTypesScanner())
				.setExecutorService(new ForkJoinPool())
			);
			
			for(Class<? extends M> clazz: reflections.getSubTypesOf(parentType)) {
				log.info("Found module {} while scanning package {}", clazz, pack);
				installModule(clazz);
			}

			long end = System.currentTimeMillis();
			log.info("Finished processing package {} modules is {} milliseconds", pack, (end - start));
		}
		long end = System.currentTimeMillis();
		log.info("Finished processing package scanned modules is {} milliseconds", (end - outerStart));
	}
}
