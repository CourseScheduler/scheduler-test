/**
  * @(#)SingleStreamModule.java
  *
  * Guice module for binding the SingleStreamRetriever and Factory implementions
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
package io.coursescheduler.scheduler.retrieval.stream;

import io.coursescheduler.scheduler.retrieval.EphemeralRetriever;
import io.coursescheduler.scheduler.retrieval.EphemeralRetrieverFactory;
import io.coursescheduler.scheduler.retrieval.Retriever;
import io.coursescheduler.scheduler.retrieval.RetrieverFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;

/**
 * Guice module for binding the SingleStreamRetriever and Factory implementions
 *
 * @author Mike Reinhold
 *
 */
public class SingleStreamModule extends AbstractModule {
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		//install a module indicating that SingleStreamRetriever can be built from a factory with assisted inject
		log.debug("Installing FactoryModuleBuilder for {} with implementations {}",
			SingleStreamRetrieverFactory.class,
			SingleStreamRetriever.class + " for " + EphemeralRetriever.class,
			SingleStreamRetriever.class + " for " + Retriever.class
		);
		install(new FactoryModuleBuilder()
			.implement(Retriever.class, SingleStreamRetriever.class)
			.implement(EphemeralRetriever.class, SingleStreamRetriever.class)
			.build(SingleStreamRetrieverFactory.class)
		);
		
		//add a mapped binding from the Retriever class to the implementation classes
		log.debug("Creating MapBinder entry for {} to {} at key {}", new Object[] {
				RetrieverFactory.class,
				SingleStreamRetrieverFactory.class,
				SingleStreamRetrieverFactory.RETRIEVER_INTERNAL_NAME
		});
		MapBinder<String, RetrieverFactory> retrieverBinder = MapBinder.newMapBinder(binder(), String.class, RetrieverFactory.class );
		retrieverBinder.addBinding(SingleStreamRetrieverFactory.RETRIEVER_INTERNAL_NAME).toProvider(getProvider(SingleStreamRetrieverFactory.class));
		
		//add a mapped binding from the Retriever class to the implementation classes
		log.debug("Creating MapBinder entry for {} to {} at key {}", new Object[] {
				EphemeralRetrieverFactory.class,
				SingleStreamRetrieverFactory.class,
				SingleStreamRetrieverFactory.RETRIEVER_INTERNAL_NAME
		});
		MapBinder<String, EphemeralRetrieverFactory> ephemeralRetrieverBinder = MapBinder.newMapBinder(binder(), String.class, EphemeralRetrieverFactory.class );
		ephemeralRetrieverBinder.addBinding(SingleStreamRetrieverFactory.RETRIEVER_INTERNAL_NAME).toProvider(getProvider(SingleStreamRetrieverFactory.class));
	}
	
}
