/**
 * TOJSON.java
 * Copyright 2009 Michael Gottesman
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * The Software shall be used for Good, not Evil.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/

package agilejson;

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
 * Was prefixEndIndex
 * and contentEndIndex
 */

  @Target({ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface TOJSON {
    int prefixLength() default 3;
    int contentLength() default -1;
    String fieldName() default "";
  }