/**
  * @(#)ThreadBasedDiscriminator.java
  *
  * Logback Discriminator to discriminate between logging events and sift them to separate logs
  * based on the thread from which the log event was triggered.
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
package io.coursescheduler.util.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;

/**
 * Logback Discriminator to discriminate between logging events and sift them to separate logs
 * based on the thread from which the log event was triggered.
 *
 * @author Mike Reinhold
 *
 */
public class ThreadNameBasedLoggingEventDiscriminator implements Discriminator<ILoggingEvent> {
	
	/**
	 * Discriminator Key used in configuration
	 */
	private static final String KEY = "threadName";
	 
    /**
     * Flag to indicate if this discriminator has been started
     */
    private boolean started;
 
    /* (non-Javadoc)
     * @see ch.qos.logback.core.sift.Discriminator#getDiscriminatingValue(java.lang.Object)
     */
    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        return Thread.currentThread().getName();
    }
 
    /* (non-Javadoc)
     * @see ch.qos.logback.core.sift.Discriminator#getKey()
     */
    @Override
    public String getKey() {
        return KEY;
    }
 
    /* (non-Javadoc)
     * @see ch.qos.logback.core.spi.LifeCycle#start()
     */
    @Override
    public void start() {
        started = true;
    }
 
    /* (non-Javadoc)
     * @see ch.qos.logback.core.spi.LifeCycle#stop()
     */
    @Override
    public void stop() {
        started = false;
    }
 
    /* (non-Javadoc)
     * @see ch.qos.logback.core.spi.LifeCycle#isStarted()
     */
    @Override
    public boolean isStarted() {
    	return started;
    }
}
