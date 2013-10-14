/**
  * @(#)DataSource.java
  *
  * Base class for data source implementations to access data for processing
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
package io.coursescheduler.scheduler.datasource;

import io.coursescheduler.util.variable.StrSubstitutorFactory;
import io.coursescheduler.util.variable.preferences.PreferencesBasedVariableFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Base class for data source implementations to access data for processing
 *
 * @author Mike Reinhold
 *
 */
public abstract class DataSource extends RecursiveAction {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Component based logger
	 */
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Preferences node containing the data source configuration
	 */
	private Preferences settings;
	
	/**
	 * Map of variable names and values
	 */
	private Map<String, String> replacements;

	/**
	 * String substitutor that will perform variable replacement in configuration elements 
	 */
	private StrSubstitutor replacer;
	
	/**
	 * The substitutor factory which can be used to create new StrSubstitutor instances
	 */
	private StrSubstitutorFactory substitutorFactory;
	
	/**
	 * Factory instance for creating Preferences based variable sources
	 */
	private PreferencesBasedVariableFactory prefSourceFactory;
	
	/**
	 * Create a new DataSource using the specified Preferences node and map of placeholders
	 * and replacement values
	 * @param substitutionFactory factory instance for creating StrSubstitution instances
	 * @param prefSourceFactory factory instance for creating PreferencesBasedVariableSource instances
	 * @param settings the Preferences node containing the configuration for the data source access
	 * @param replacements map of substitution placeholders to values
	 */
	@AssistedInject
	public DataSource(StrSubstitutorFactory substitutionFactory, PreferencesBasedVariableFactory prefSourceFactory, @Assisted("config") Preferences settings, @Assisted("localVars") Map<String, String> replacements) {
		super();
		
		this.settings = settings;
		this.substitutorFactory = substitutionFactory;
		this.prefSourceFactory = prefSourceFactory;
		this.replacements = replacements;
		
		setReplacerPreferencesNode(settings);
	}
	
	/**
	 * Return the input stream that is a result of processing the data source.
	 * 
	 * Calling this method prior to execution of the DataSource may result in invalid results.
	 *
	 * @return the input stream resulting from processing the input stream
	 * @throws IOException if there is an error performing IO on the data source
	 */
	public abstract InputStream getDataSourceAsInputStream() throws IOException;
	
	/**
	 * Update the preferences node used by the current string substituter instance
	 *
	 * @param node the new preferences node for variable reference
	 */
	protected void setReplacerPreferencesNode(Preferences node) {
		Set<StrLookup<String>> sources = new HashSet<>();
		log.debug("Creating Preferences Variable Source from preferences node {}", node);
		sources.add(prefSourceFactory.createPreferencesVariableSource(node));
		log.debug("Creating MapLookup Variable Source from map {}", replacements);
		sources.add(StrLookup.mapLookup(replacements));
		
		this.replacer = substitutorFactory.createSubstitutor(sources);
	}
	
	/**
	 * Retrieve the current Map of variable names and values used by the replacer. 
	 * Warning: modifications to htis map will be reflected by the string substitutor
	 * 
	 * @return the map of substitutions
	 */
	protected Map<String, String> getReplacements() {
		return replacements;
	}
	
	/**
	 * Perform the string replacements and return the resultant value
	 *
	 * @param input the string upon which to perform the replacements
	 * @return the updated string value
	 */
	protected String performReplacements(String input) {
		String value = input;
		log.trace("Performing placeholder substitution on string: {}", input);
		value = replacer.replace(value);		
		log.debug("Substituted string is: {}", value);
		return value;
	}
	
	/**
	 * Return the Preferences node corresponding to the DataSource configuration
	 *
	 * @return the configuration node
	 */
	protected Preferences getSettings() {
		return settings;
	}
}
