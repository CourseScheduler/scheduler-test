/**
  * @(#)ParseException.java
  *
  * Exception class for catching different parser errors.
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
package io.coursescheduler.scheduler.parse;


/**
 * Exception class for catching different parser errors.
 *
 * @author Mike Reinhold
 *
 */
public class ParseException extends Exception {

	/**
	 * Constructs a new exception with the specified cause and a detail message of (cause==null ? null : cause.toString())
	 * (which typically contains the class and detail message of cause). This constructor is useful for exceptions that are
	 * little more than wrappers for other throwables (for example, java.security.PrivilegedActionException).
	 *
	 * @param cause the cause (which is saved for later retrieval by the getCause() method). (A null value is permitted, 
	 * and indicates that the cause is nonexistent or unknown.)
	 */
	public ParseException(Throwable cause) {
		super(cause);
	}

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
}
