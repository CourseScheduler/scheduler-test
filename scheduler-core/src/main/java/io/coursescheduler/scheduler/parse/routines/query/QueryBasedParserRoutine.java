/**
  * @(#)QueryBasedParserRoutine.java
  *
  * TODO FILE PURPOSE
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
package io.coursescheduler.scheduler.parse.routines.query;

import static io.coursescheduler.scheduler.parse.routines.xml.XMLParserConstants.ELEMENT_ID_VARIABLE;
import static io.coursescheduler.scheduler.parse.routines.xml.XMLParserConstants.PARSER_TOOL_PROPERTY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RecursiveAction;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.coursescheduler.scheduler.parse.ParseActionBatch;
import io.coursescheduler.scheduler.parse.routines.ParserRoutine;
import io.coursescheduler.scheduler.parse.tools.query.QueryBasedParserTool;
import io.coursescheduler.scheduler.parse.tools.query.QueryBasedParserToolMap;

/**
 * TODO Describe this type
 *
 * @author Mike Reinhold
 *
 */
public abstract class QueryBasedParserRoutine<N> extends ParserRoutine {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Component based logger
	 */
	private transient Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * The {@link java.util.prefs.Preferences} node containing the configuration for this ParserRoutine
	 */
	private transient Preferences profile;
	
	private QueryBasedParserTool parser;

	@AssistedInject
	public QueryBasedParserRoutine(QueryBasedParserToolMap toolMap, @Assisted("profile") Preferences profile) {
		super();
		
		this.profile = profile;
		
		this.parser = toolMap.getQueryBasedParserTool(profile.get(PARSER_TOOL_PROPERTY, null));
	}

	/**
	 * @return the profile
	 */
	protected Preferences getProfile() {
		return profile;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		log.info("Preparing to query the input for data elements");
		long start = System.currentTimeMillis();
		
		try {
			N queryable = prepareInput();
			
			Set<String> groups = queryGroups(queryable);
			log.info("Found {} groups of elements to process", groups.size());
			
			List<RecursiveAction> batches = buildBatches(groups);		
			waitForBatches(batches);
		}catch(Exception e) {
			log.error("Exception querying input for data elements", e);
		}

		long end = System.currentTimeMillis();
		log.info("Retrieved data for {} elements in {} milliseconds", getDataSets().size(), (end - start));
	}

	protected List<RecursiveAction> buildBatches(Set<String> groups){
		long start = System.currentTimeMillis();
		log.info("Preparing to build background tasks for {} groups", groups.size());
		
		int batchSize = profile.getInt(BATCH_SIZE_PROPERTY, Integer.MAX_VALUE);
		log.info("Using batch size of {}", batchSize);
		
		List<RecursiveAction> batches = new ArrayList<>();
		List<RecursiveAction> elementsBatch = new ArrayList<>();
		int elementsBatched = 0;
		
		for(String group: groups) {
			RecursiveAction task;
			try {
				task = null;	//TODO create the background task for processing the groups
				elementsBatch.add(task);
				log.info("Finished creating background task for processing group {}", group);
			} catch (Exception e) {
				log.error("Unable to create background task for processing group {}", group);
			}

			//increment this separately from the task creation in case there is an issue with that step
			//this needs to be incremented for each course so that we can be sure to initiate the last 
			//batch in the instance that courses.size() % batchSize != 0 (which will be often)
			elementsBatched++;	
			
			//check if batch size is met or if all
			if(elementsBatch.size() == batchSize || elementsBatched == groups.size()) {
				log.debug("{} tasks ready to be batched ({} of {})", new Object[] {elementsBatch.size(), elementsBatched, groups.size()});
				RecursiveAction batch = new ParseActionBatch(elementsBatch);
				batches.add(batch);
				log.info("Forking background task batch {}", batch);
				batch.fork();
				elementsBatch = new ArrayList<>();
			}
		}
		long end = System.currentTimeMillis();
		log.info("Completed building background tasks for {} groups in {} batches in {} ms", new Object[] {groups.size(), batches.size(), end - start});
		
		return batches;
	}
	
	protected void waitForBatches(List<RecursiveAction> batches) {
		long start = System.currentTimeMillis();
		long end;
		log.info("Waiting for {} batches to finish", batches.size());
		for(RecursiveAction action: batches) {
			log.debug("Waiting for batch {} to finish processing", action);
			action.join();
			end = System.currentTimeMillis();
			log.debug("Batch {} finished after {} ms", action, end - start);
		}
		end = System.currentTimeMillis();
		log.info("All batches finished processing in {} ms", end - start);
	}
	
	protected RecursiveAction createBackgroundTask(String group, List<N> elements) {
		ConcurrentMap<String, String> data = new ConcurrentHashMap<>();
		getDataSets().put(group, data);
		
		RecursiveAction action = null; //TODO create the background task
		
		return action;
	}
	
	protected abstract N prepareInput() throws Exception;
	
	protected Set<String> queryGroups(N queryable) {
		long start = System.currentTimeMillis();
		log.info("Retrieving element identifiers from source data set");
		Set<String> elements = new TreeSet<String>();
		
		List<N> groupList = null; //TODO query the list of groups
		
		for(N element: groupList) {
			String groupName = asString(element);
			groupName = executeScript(groupName, profile, "" /* TODO group list property script */ );
			elements.add(groupName);
			log.debug("Found row belonging to {}", groupName);
		}

		long end = System.currentTimeMillis();
		log.debug("Finished retrieving element identifiers from source data in {} ms", end - start);
		return elements;
	}
	
	protected List<N> queryGroup(N queryable, Preferences settings, String group){
		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put("" /* TODO variable name */, group);

		List<N> groupElements = null;	//TODO query the group elements

		log.info("Found {} elements for group {}", groupElements.size(), group);
				
		return groupElements;
		
	}
	
	protected String executeScript(String value, Preferences settings, String key) {
		//TODO if a script parser is defined, get and execute the script
		return value;
	}
	
	protected abstract String asString(N item);
	
}
