/**
 * @(#)XPathParserRoutine.java
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
package io.coursescheduler.scheduler.parse.query.xpath;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RecursiveAction;
import java.util.prefs.Preferences;

import org.w3c.dom.Node;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.coursescheduler.scheduler.parse.query.QueryBasedParserRoutine;
import io.coursescheduler.scheduler.parse.query.QueryBasedParserToolMap;
import io.coursescheduler.util.script.engine.ScriptEngineMap;

/**
 * TODO Describe this type
 *
 * @author Mike Reinhold
 *
 */
public class XPathParserRoutine extends QueryBasedParserRoutine<Node> {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	@AssistedInject
	public XPathParserRoutine(QueryBasedParserToolMap toolMap, ScriptEngineMap scriptMap, @Assisted("profile") Preferences profile) {
		super(toolMap, scriptMap, profile);
		// TODO CONSTRUCTOR STUB
	}

	@Override
	public Node prepareInput() throws Exception {
		// TODO METHOD STUB
		return null;
	}

	@Override
	protected RecursiveAction createBackgroundTaskImpl(String group, List<Node> elements, ConcurrentMap<String, String> data, Preferences profile) {
		// TODO METHOD STUB
		return null;
	}
	
}
