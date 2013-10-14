/**
  * @(#)BatchTask.java
  *
  *  A RecursiveAction that can be used to process other RecursiveActions in batch. This class invokes
  * its subtasks in the same thread pool in which it is executed. This class can be subclassed in order
  * to provide special handling for exceptions or error states encountered by the sub-actions. See 
  * {@link #compute()} and {@link #execute()} for details.
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
package io.coursescheduler.util.concurrent;

import java.util.Collection;
import java.util.concurrent.RecursiveAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RecursiveAction that can be used to process other RecursiveActions in batch. This class invokes
 * its subtasks in the same thread pool in which it is executed. This class can be subclassed in order
 * to provide special handling for exceptions or error states encountered by the sub-actions. See 
 * {@link #compute()} and {@link #execute()} for details.
 *
 * @author Mike Reinhold
 *
 */
public class RecursiveActionBatch extends RecursiveAction {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Component based logger
	 */
	private transient Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * The collection of actions which should be executed as a batch
	 */
	private Collection<RecursiveAction> actions;
	
	/**
	 * Create a new RecursiveActionBatch task which will execute the specified actions in the same ForkJoinPool
	 * in which this action is executed. 
	 *
	 * @param actions the collection of actions to invoke when this batch is run
	 */
	public RecursiveActionBatch(Collection<RecursiveAction> actions) {
		super();
		
		this.actions = actions;
	}
	
	/**
	 * @return the collection of actions to process in batch
	 */
	public Collection<RecursiveAction> getActions() {
		return actions;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		try {
			execute();
		}catch(Exception e) {
			log.error("Exception occurred while processing batch actions. Other actions may have been cancelled as a result", e);
		}	
	}
	
	/**
	 * Executes the batch actions of this RecursiveActionBatch instance by calling {@link java.util.concurrent.ForkJoinTask#invokeAll(Collection)}}. 
	 * This method does not catch any exceptions that may be thrown by {@link java.util.concurrent.ForkJoinTask#invokeAll(Collection)}}. 
	 * 
	 * Subclasses of RecursiveActionBatch can use this property to provide custom error handling by overriding {@link #compute()} and
	 * invoking this method with the appropriate exception handling in place. 
	 *
	 */
	protected final void execute() {
		log.info("Initiating batch processing of {} tasks", actions.size());
		invokeAll(actions);
		log.info("Batch processing of {} tasks completed", actions.size());
	}
	
	/**
	 * Returns the number of tasks in this batch. 
	 *
	 * @return the batch size
	 */
	public int getBatchSize() {
		return actions.size();
	}
	
}
