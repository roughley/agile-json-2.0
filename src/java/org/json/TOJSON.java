/*
 * TOJSON.java
 * Copyright 2008 Michael Gottesman
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
 */

package org.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signals a method is a get-accessor and its result should be JSONified
 * Note that in the creation of the result value's key, it is assumed that the
 * method's name is of the standard java form getXYZ(). Thus by this
 * convention we can easily create a JSON key by first removing get
 * by using substring and then lowercasing (my own convention as well)
 * the method name.
 * 
 * Thus getObject() => { "object" : "method_result" }
 * And getName() => { "name" : "method_result" }
 */

  @Target({ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface TOJSON {
    int prefixEndIndex() default 3;
    int contentEndIndex() default -1;
  }