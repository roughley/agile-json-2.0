/*
 * JSON.java
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.annotation.*;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Importand Notes on usage:
 * 2. If you use primitives and do not assign a value to them,
 * they will take a default value i.e. 0 for integer.
 * 3. If on the other hand you use the object version of those primitives,
 * (i.e. Integer vs int, Double vs double), the resulting JSON
 * translation will be null, not 0 (or whatever the default value is
 * @author gottesmm
 */
public class JSON {

  /**
   * This method takes in a primitive that has been converted to an object
   * and creates a copy of it so that .equals results in different objects.
   * @param v
   * @throws java.lang.Exception
   */
  private static Object getObjectForPrimitive(Object v) throws Exception {
    Class c = v.getClass();
    if (c == Byte.class) {
      return new String(new byte[]{((Byte) v).byteValue()});
    } else if (c == Boolean.class) {
      return new Boolean((Boolean) v);
    } else if (c == Character.class) {
      return new Character((Character) v);
    } else if (c == Short.class) {
      return new Short((Short) v);
    } else if (c == Integer.class) {
      return new Integer((Integer) v);
    } else if (c == Long.class) {
      return new Long((Long) v);
    } else if (c == Float.class) {
      return new Float((Float) v);
    } else if (c == Double.class) {
      return new Double((Double) v);
    } else {
      throw new Exception("Unknown Primitive");
    }
  }

  /**
   * This class is a JSONStringer which does not escape any data.
   * Instead it outputs the json unescaped and allows for all of the completed
   * json to be escaped together at the end.
   * 
   * This stops multiple JSON escapes from occuring resulting in nastiness like:
   * Hello, \\\\\\t how are you doing?\\\\\\n.
   * 
   * This makes it much easier to have multiple levels of JSON
   */
  protected static class NoEscapesStringer extends JSONStringer {

    @Override
    public JSONWriter value(Object o) throws JSONException {
      return this.append(o.toString());
    }

    /**
     * Append a key. The key will be associated with the next value. In an
     * object, every value must be preceded by a key.
     * @param s A key string.
     * @return this
     * @throws JSONException If the key is out of place. For example, keys
     *  do not belong in arrays or if the key is null.
     */
    @Override
    public JSONWriter key(String s) throws JSONException {
      if (s == null) {
        throw new JSONException("Null key.");
      }
      if (this.mode == 'k') {
        try {
          if (this.comma) {
            this.writer.write(',');
          }
          this.writer.write('"' + s + '"');
          this.writer.write(':');
          this.comma = false;
          this.mode = 'o';
          return this;
        } catch (IOException e) {
          throw new JSONException(e);
        }
      }
      throw new JSONException("Misplaced key.");
    }
  }
  private static Class[] _primitives = {Object.class, Short.class, Byte.class, Character.class, Boolean.class, Integer.class, Float.class, Double.class, Long.class};
  protected static HashSet PRIMITIVES = new HashSet(Arrays.asList(_primitives));
  private static Class[] _primitivearrays = {short[].class, byte[].class, char[].class, boolean[].class, int[].class, float[].class, double[].class, long[].class};
  protected static HashSet PRIMITIVEARRAYS = new HashSet(Arrays.asList(_primitivearrays));

  /**
   * Public interface to protected toJSON method.
   * @param o
   * @throws org.json.JSONException
   * @throws java.lang.IllegalAccessException
   */
  public static String toJSON(Object o) throws JSONException, IllegalAccessException {
    HashSet alreadyVisited = new HashSet();
    return JSON.toJSON(o, alreadyVisited);
  }

  /**
   * Escapes all of the characters in the string that according
   * to the javascript standard are able to be escaped.
   * WARNING:
   * If you have already js-escaped a string before, 
   * you will have the dreaded \\\\\\ problem where your
   * escapes are themselves escaped. This is needless waste
   * of space so be forewarned.
   * @param string
   * @return escaped string
   */
  protected static String escape(String string) {
    if (string == null || string.length() == 0) {
      return "";
    }

    char b;
    char c = 0;
    int i;
    int len = string.length();
    StringBuffer sb = new StringBuffer(len);
    String t;

    for (i = 0; i < len; i += 1) {
      b = c;
      c = string.charAt(i);
      switch (c) {
        case '\\':
        case '"':
          sb.append('\\');
          sb.append(c);
          break;
        case '/':
          if (b == '<') {
            sb.append('\\');
          }
          sb.append(c);
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\t':
          sb.append("\\t");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\f':
          sb.append("\\f");
          break;
        case '\r':
          sb.append("\\r");
          break;
        default:
          if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
            (c >= '\u2000' && c < '\u2100')) {
            t = "000" + Integer.toHexString(c);
            sb.append("\\u" + t.substring(t.length() - 4));
          } else {
            sb.append(c);
          }
      }
    }
    return sb.toString();
  }

  /**
   * Work Horse of the library
   * @param o
   * @param alreadyVisited
   * @throws org.json.JSONException
   * @throws java.lang.IllegalAccessException
   */
  protected static String toJSON(Object o, HashSet alreadyVisited) throws JSONException, IllegalAccessException {

    if (o == null) {
      return "null";
    }

    Class c = o.getClass();
    if (!PRIMITIVES.contains(c)) {
      alreadyVisited.add(o);
    }

    JSONStringer s = new NoEscapesStringer();

    if ((Jsonable.class).isAssignableFrom(c)) {
      s.object();
      Field[] fields = c.getFields();
      for (int i = 0; i < fields.length; i++) {
        Object fieldValue = fields[i].get(o);
        if (!alreadyVisited.contains(fieldValue)) {
          s.key(fields[i].getName());
          s.value(JSON.toJSON(fieldValue, alreadyVisited));
        }
      }
      Method[] methods = c.getMethods();
      // Note I am assuming your methods are named something like "getObject".
      // With that in my mind I set the name of the json object to the substring
      // from 3 to the end, downcasing it, resulting in get being dropped
      // and the second word capitalization being lowered. So you get
      // object.
      for (int i = 0; i < methods.length; i++) {
        if (methods[i].getParameterTypes().length == 0 && methods[i].isAnnotationPresent(TOJSON.class)) {
          Object returnValue;
          try {
            returnValue = methods[i].invoke(null);
          } catch (Exception e) {
            continue;
          }
          if (!alreadyVisited.contains(returnValue)) {
            s.key(methods[i].getName().substring(3).toLowerCase());
            s.value(JSON.toJSON(returnValue, alreadyVisited));
          }
        }
      }
      s.endObject();
    } else if ((Object[].class).isAssignableFrom(c)) {
      s.array();
      Object[] array = (Object[]) o;
      Object _o;
      Class _c;
      for (int j = 0; j < array.length; j++) {
        s.value(JSON.toJSON(array[j], alreadyVisited));
      }
      s.endArray();
    } else {
      // Check this part
      if (PRIMITIVEARRAYS.contains(c)) {
        if ((byte[].class).isAssignableFrom(c)) {
          try {
            return "\"" + new String((byte[]) o, "UTF-8") + "\"";
          } catch (UnsupportedEncodingException ex) {
            return "\"" + new String((byte[])o) + "\"";
          }
        } else {
          s.array();
          Object[] array = ((Object[]) o);
          for (int i = 0; i < array.length; i++) {
            s.value(String.valueOf(array[i]));
          }
          s.endArray();
        }
      } else if (c.isPrimitive() || PRIMITIVES.contains(c) || c == JSONObject.class || c == JSONArray.class) {
        return o.toString();
      } else if (c == String.class) {
        return '"' + escape(o.toString()) + '"';
      } else {
        s.object();
        Method[] methods = c.getMethods();
        // Note I am assuming your methods are named something like "getObject".
        // With that in my mind I set the name of the json object to the substring
        // from 3 to the end, downcasing it, resulting in get being dropped
        // and the second word capitalization being lowered. So you get
        // object.
        for (int i = 0; i < methods.length; i++) {
          if (methods[i].getParameterTypes().length == 0 && methods[i].isAnnotationPresent(TOJSON.class)) {
            Object returnValue;
            try {
              returnValue = methods[i].invoke(o, ((Object[]) null));
            } catch (Exception e) {
              continue;
            }
            if (!alreadyVisited.contains(returnValue)) {
              s.key(methods[i].getName().substring(3).toLowerCase());
              s.value(JSON.toJSON(returnValue, alreadyVisited));
            }
          }
        }
        s.endObject();
      }
    }
    return s.toString();
  }
  }
