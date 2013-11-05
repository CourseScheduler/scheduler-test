/**
  * @(#)HtmlParserRoutine.java
  *
  * HTML parser routine for retrieving data from HTML documents
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
package io.coursescheduler.scheduler.parse.routines.html;

import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;

import io.coursescheduler.scheduler.parse.routines.ParserRoutine;

/**
 * HTML parser routine for retrieving data from HTML documents
 * 
 * @author Mike Reinhold
 *
 */
public class HtmlParserRoutine extends ParserRoutine {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Component based logger
	 */
	private transient Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Input stream that is the source for the HTML parser
	 */
	private InputStream source;
	
	/**
	 * Preferences node for the parser configuration
	 */
	private Preferences settings;

	/**
	 * 
	 * TODO Describe this constructor
	 *
	 * @param input
	 * @param profile
	 */
	public HtmlParserRoutine(@Assisted("source") InputStream input, @Assisted("profile") Preferences profile) {
		super();
		
		this.source = input;
		this.settings = profile;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		
		
		String charsetName = "UTF-8";	//TODO change to settings retrieved
		String baseUri = "https://jweb.kettering.edu/";	//TODO change to relative settings ${../datasource/uri.base}
		
		try {
			Jsoup.parse(source, charsetName, baseUri);
			
			
			
		} catch (IOException e) {
			log.error("Exception parsing input stream", e);
		}finally {
			try {
				log.debug("Attempting to close the input stream {}", source);
				source.close();
			} catch (IOException e) {
				log.error("Exception closing input data source", e);
			}
		}
	}
	
}
