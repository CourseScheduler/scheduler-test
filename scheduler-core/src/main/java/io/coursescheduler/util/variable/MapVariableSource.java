/**
  * @(#)MapVariableSource.java
  *
  * Use a Map as a variable data source
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
package io.coursescheduler.util.variable;

import java.util.Map;

/**
 * Use a Map as a variable data source
 *
 * @author Mike Reinhold
 *
 */
public class MapVariableSource extends SubstitutionVariableSource {
	
	/**
	 * The map of variable names and values to use as a variable source
	 */
	private Map<String, String> map;
	
	/**
	 * Create a new MapVariableSource that uses the specified map as a variable source
	 *
	 * @param map the backing map to use as a variable source
	 */
	public MapVariableSource(Map<String, String> map) {
		super();
		
		this.map = map;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.lang3.text.StrLookup#lookup(java.lang.String)
	 */
	@Override
	public String lookup(String key) {
		return map.get(key);
	}
	
}
