/**
  * @(#)XMLHelperModule.java
  *
  * Guice module for XML Parser Tool helper components and bindings
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
package io.coursescheduler.scheduler.parse.tools.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

/**
 * Guice module for XML Parser Tool helper components and bindings
 *
 * @author Mike Reinhold
 *
 */
public class XMLHelperModule extends AbstractModule {
	
	/**
	 * Component based logger
	 */
	Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
	    // create & install a module that uses the @CheckedProvides methods
		log.debug("Installing ThrowingProviderBinder for module {}", this.getClass().getName());
	    install(ThrowingProviderBinder.forModule(this)); 
	}
	
	/**
	 * Get a DocumentBuilderFactory for the purposes of creating a DocumentBuilder for
	 * creating and parsing XML documents
	 *
	 * Default configuration:
	 *   namespace aware: true
	 *
	 * @return a DocumentBuilderFactory with the default configuration
	 */
	@Provides
	DocumentBuilderFactory getDocumentBuilderFactory() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // always want the document builder to be namespace aware
		return factory;
	}
	
	/**
	 * Get a DocumentBuilder for the purposes of creating and parsing XML documents
	 *
	 * @param factory the DocumentBuilderFactory that will be used to create the DocumentBuilder
	 * @return a DocumentBuilder using the default configuration coming from the DocumentBuilderFactory
	 * @throws ParserConfigurationException if there is a configuration error with the builder
	 */
	@CheckedProvides(DocumentBuilderProvider.class) 
	DocumentBuilder provideWorld(DocumentBuilderFactory factory) throws ParserConfigurationException {
		return factory.newDocumentBuilder();
	}

}
