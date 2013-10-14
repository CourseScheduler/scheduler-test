/**
  * @(#)PreferencesBasedVariableModule.java
  *
  * Guice module for binding the PreferencesBasedVariableSource and Factory
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
package io.coursescheduler.util.variable.preferences;

import io.coursescheduler.scheduler.datasource.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Guice module for binding the PreferencesBasedVariableSource and Factory
 *
 * @author Mike Reinhold
 *
 */
public class PreferencesBasedVariableModule extends AbstractModule {

	/**
	 * Component based logger
	 */
	Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		//install a module indicating that FileDataSource can be built from a factory with assisted inject
		log.debug("Installing FactoryModuleBuilder for {} with implementations {}",
				PreferencesBasedVariableFactory.class,
			PreferencesBasedVariableSource.class + " for " + DataSource.class
		);
		install(new FactoryModuleBuilder()
			.implement(PreferencesBasedVariableSource.class, PreferencesBasedVariableSource.class)
			.build(PreferencesBasedVariableFactory.class)
		);
	}
	
}
