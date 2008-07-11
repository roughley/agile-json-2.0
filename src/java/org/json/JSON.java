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
  private static Class[] _primitives = {Object.class, String.class, Short.class, Byte.class, Character.class, Boolean.class, Integer.class, Float.class, Double.class, Long.class};
  protected static HashSet PRIMITIVES = new HashSet(Arrays.asList(_primitives));
  private static Class[] _primitivearrays = {
    Short[].class, Byte[].class, Character[].class, Boolean[].class, Integer[].class, Float[].class, Double[].class, Long[].class,
    short[].class, byte[].class, char[].class, boolean[].class, int[].class, float[].class, double[].class, long[].class
  };
  protected static HashSet PRIMITIVEARRAYS = new HashSet(Arrays.asList(_primitivearrays));

  /**
   * Public interface to protected toJSON method.
   * @param o
   * @return valid Json
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
   * Refactor of jsonifyFields
   * @param o
   * @param c
   * @param s
   * @param alreadyVisited
   * @throws java.lang.IllegalAccessException
   * @throws org.json.JSONException 
   */
  private static void jsonifyFields(Object o, Class c, JSONStringer s, HashSet alreadyVisited) throws IllegalAccessException, JSONException {
    Field[] fields = c.getFields();
    for (int i = 0; i < fields.length; i++) {
      Object fieldValue = fields[i].get(o);
      if (!alreadyVisited.contains(fieldValue)) {
        s.key(fields[i].getName());
        s.value(JSON.toJSON(fieldValue, alreadyVisited));
      }
    }
  }

  public static void jsonifyArray(Object o, JSONStringer s, HashSet alreadyVisited) throws JSONException, IllegalAccessException {
    s.array();
    Object[] array = (Object[]) o;
    Object _o;
    Class _c;
    for (int j = 0; j < array.length; j++) {
      s.value(JSON.toJSON(array[j], alreadyVisited));
    }
    s.endArray();
  }

  /**
   * Note I am assuming your methods are named something like "getObject".
   * With that in my mind I set the name of the json object to the substring
   * from 3 to the end, downcasing it, resulting in get being dropped
   * and the second word capitalization being lowered. So you get
   * object.
   * @param o
   * @param c
   * @param s
   * @param alreadyVisited
   * @return boolean on whether any items were written.
   * @throws java.lang.IllegalAccessException
   * @throws org.json.JSONException 
   */
  private static boolean jsonifyGetters(Object o, Class c, JSONStringer s, HashSet alreadyVisited, boolean initialValue) throws IllegalAccessException, JSONException {
    Method[] methods = c.getMethods();
    boolean initAnyOutputValue = initialValue;
    boolean anyOutput = initialValue;
    for (int i = 0; i < methods.length; i++) {
      TOJSON a;
      if (methods[i].getParameterTypes().length == 0 && (a = methods[i].getAnnotation(TOJSON.class)) != null) {
        Object returnValue;
        try {
          returnValue = methods[i].invoke(o, ((Object[]) null));
        } catch (Exception e) {
          continue;
        }
        if (!alreadyVisited.contains(returnValue)) {
          if (!anyOutput) {
            anyOutput = true;
            s.object();
          }
          if (a.contentEndIndex() == -1) {
            s.key(methods[i].getName().substring(a.prefixEndIndex()).toLowerCase());
          } else {
            s.key(methods[i].getName().substring(a.prefixEndIndex(), a.contentEndIndex()).toLowerCase());
          }
          s.value(JSON.toJSON(returnValue, alreadyVisited));
        } else {
          continue;
        }
      }
    }
    if (anyOutput && !initAnyOutputValue) {
      s.endObject();
    }
    return anyOutput;
  }

  /**
   * Work Horse of the library
   * @param o
   * @param alreadyVisited
   * @return Proper Json String
   * @throws org.json.JSONException
   * @throws java.lang.IllegalAccessException
   */
  protected static String toJSON(Object o, HashSet alreadyVisited) throws JSONException, IllegalAccessException {

    // If null return JSON's null value, null
    if (o == null) {
      return "null";
    }

    // Get class for reflection purposes
    Class c = o.getClass();

    // Make sure that given a primitive, it is not added to already visited
    // This is for two reasons:
    // 1. Classes are sealed so can not point to other objects.
    // 2. String, et. al., have overridden equals methods which is true
    //     given equality of value, not equality of reference.
    // This results in the loss of values in the json representation
    if (!PRIMITIVES.contains(c)) {
      alreadyVisited.add(o);
    }

    // Use no excape stringer b/c we want to escape strings only once,
    // to stop thigns like \\\\\\n, etc.
    JSONStringer s = new NoEscapesStringer();

    // If Jsonable, handle like a Jsonable
    if ((Jsonable.class).isAssignableFrom(c)) {
      s.object();
      // Jsonify Fields
      jsonifyGetters(o, c, s, alreadyVisited, true);
      jsonifyFields(o, c, s, alreadyVisited);
      s.endObject();
    } else if ((Object[].class).isAssignableFrom(c)) {
      if((Byte[].class).isAssignableFrom(c)) {
        Byte[] B = (Byte[])o;
        byte[] b = new byte[B.length];
        for(int i = 0;i < B.length;i++) {
          b[i] = B[i].byteValue();
        }
        return "\"" + escape(new String(b)) + "\"";
      } else if ((Character[].class).isAssignableFrom(c)) {
        Character[] C = (Character[])o;
        char[] primitiveC = new char[C.length];
        for(int i = 0;i < C.length;i++) {
          primitiveC[i] = C[i].charValue();
        }
        return "\"" + escape(new String(primitiveC)) + "\"";
      } else {
        jsonifyArray(o, s, alreadyVisited);
      }
    } else {
      // Check this part
      if (PRIMITIVEARRAYS.contains(c)) {
        // If byte array return as a string. Otherwise returns
        // as a space delimited Hex Representation of the bytes
        if ((byte[].class).isAssignableFrom(c)) {
          try {
            return "\"" + escape(new String((byte[]) o, "UTF-8")) + "\"";
          } catch (UnsupportedEncodingException ex) {
            return "\"" + escape(new String((byte[]) o)) + "\"";
          }
        } else if ((char[].class).isAssignableFrom(c)) {
          return "\"" + escape(new String((char[]) o)) + "\"";
        } else {
          s.array();
          if ((short[].class).isAssignableFrom(c)) {
            short[] array = (short[]) o;
            for (short b : array) {
              s.value(String.valueOf(b));
            }
          } else if ((int[].class).isAssignableFrom(c)) {
            int[] array = (int[]) o;
            for (int b : array) {
              s.value(String.valueOf(b));
            }
          } else if ((long[].class).isAssignableFrom(c)) {
            long[] array = (long[]) o;
            for (long b : array) {
              s.value(String.valueOf(b));
            }
          } else if ((float[].class).isAssignableFrom(c)) {
            float[] array = (float[]) o;
            for (float b : array) {
              s.value(String.valueOf(b));
            }
          } else if ((double[].class).isAssignableFrom(c)) {
            double[] array = (double[]) o;
            for (double b : array) {
              s.value(String.valueOf(b));
            }
          } else {
            boolean[] array = (boolean[]) o;
            for (boolean b : array) {
              s.value(String.valueOf(b));
            }
          }
          s.endArray();
        }
      } else if ((c != String.class && c != Character.TYPE && c != Character.class) && (PRIMITIVES.contains(c) || c == JSONObject.class || c == JSONArray.class)) {
        return o.toString();
      } else if (c == String.class || c == Character.TYPE || c == Character.class) {
        return '"' + escape(o.toString()) + '"';
      } else {
        // Note json object is created inside jsonify getters
        // this is because we dont know whether or not we have
        // methods which are annotated as jsonable until we get in there.
        // This allows us to just tostring the output if no methods
        // have been annotated.
        if (!jsonifyGetters(o, c, s, alreadyVisited, false)) {
          return "\"" + escape(o.toString()) + "\"";
        }
      }
    }
    return s.toString();
  }
  }
