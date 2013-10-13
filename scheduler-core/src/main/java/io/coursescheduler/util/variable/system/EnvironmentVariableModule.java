/**
  * @(#)EnvironmentVariableModule.java
  *
  * Guice module for loading the Environment Variable variable source
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
package io.coursescheduler.util.variable.system;

import io.coursescheduler.util.variable.GlobalSubstitutionVariableSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

/**
 * Guice module for loading the Environment Variable variable source
 *
 * @author Mike Reinhold
 *
 */
public class EnvironmentVariableModule extends AbstractModule {
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		log.debug("Creating MultiBinder for {} using implementation {}", GlobalSubstitutionVariableSource.class, EnviromentBasedVariableSource.class);
		Multibinder<GlobalSubstitutionVariableSource> globalVarBinder = Multibinder.newSetBinder(binder(), GlobalSubstitutionVariableSource.class);
		globalVarBinder.addBinding().to(EnviromentBasedVariableSource.class).in(Singleton.class);
	}
	
}
