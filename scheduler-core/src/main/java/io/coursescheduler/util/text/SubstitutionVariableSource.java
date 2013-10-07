/**
  * @(#)SubstitutionVariableSource.java
  *
  * Interface for classes that provide access to variables that can be used by the 
  * StrSubstitutor. Implementations of this class may provide access to global or
  * scope localized variables. 
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
package io.coursescheduler.util.text;

import java.util.Map;

/**
 * Interface for classes that provide access to variables that can be used by the 
 * StrSubstitutor. Implementations of this class may provide access to global or
 * scope localized variables. 
 *
 * @author Mike Reinhold
 *
 */
public interface SubstitutionVariableSource {
	
	/**
	 * Return a map of the substitution variables. 
	 *
	 * @return the Map of keys and values encapsulated by this SubstitutionVariableSource
	 */
	public Map<String, String> getVariableMap();
}
