/**
  * @(#)RandomVariableSource.java
  *
  * Variable source for random values
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
package io.coursescheduler.util.variable.random;

import java.math.BigInteger;
import java.security.SecureRandom;

import io.coursescheduler.util.variable.GlobalSubstitutionVariableSource;

/**
 * Variable source for random values
 *
 * @author Mike Reinhold
 *
 */
public class RandomVariableSource extends GlobalSubstitutionVariableSource {
	
	/**
	 * Maximmum number of bits that will be used when generating random strings. All other
	 * random variables use appropriate data type bit lengths.
	 *
	 * Value: {@value}
	 */
	public static final int MAX_BIT_SIZE = 130;
	
	/* (non-Javadoc)
	 * @see org.apache.commons.lang3.text.StrLookup#lookup(java.lang.String)
	 */
	@Override
	public String lookup(String key) {
		
		switch(key) {
			case "random.int":{
				return new BigInteger(Integer.SIZE, new SecureRandom()).toString();
			}
			case "random.byte":{
				return new BigInteger(Byte.SIZE, new SecureRandom()).toString();
			}
			case "random.long":{
				return new BigInteger(Long.SIZE, new SecureRandom()).toString();
			}
			case "random.string":{
				return new BigInteger(MAX_BIT_SIZE, new SecureRandom()).toString();
			}
			default:{
				return null;
			}
		}
	}
	
}
