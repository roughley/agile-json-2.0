/**
 * JSONTest.java
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gottesmm
 */
public class JSONTest {

  protected static class GetAccsrJsonableTestObject {

    protected String name;
    protected int age;
    protected Integer years_of_college;
    protected double weight;
    protected Double height;
    protected GetAccsrJsonableTestObject[] relatives;
    protected String[] friends;
    public byte[] test;

    public GetAccsrJsonableTestObject() {
      name = "John Smith";
      age = 22;
      years_of_college = 4;
      weight = 160.0;
      height = 60.1;
      relatives = new GetAccsrJsonableTestObject[3];
      relatives[0] = new GetAccsrJsonableTestObject("Joe Bob");
      relatives[1] = new GetAccsrJsonableTestObject("John Shmoe");
      relatives[2] = new GetAccsrJsonableTestObject("Sally Lee");
      friends = new String[2];
      friends[0] = new String("Banana Pear");
      friends[1] = new String("Orange Banana");
      test = new String("test123412341234").getBytes();
    }

    public GetAccsrJsonableTestObject(String name) {
      this.name = name;
    }

    @TOJSON
    public int getAge() {
      return age;
    }

    @TOJSON
    public String[] getFriends() {
      return friends;
    }

    @TOJSON
    public Double getHeight() {
      return height;
    }

    @TOJSON
    public String getName() {
      return name;
    }

    @TOJSON
    public GetAccsrJsonableTestObject[] getRelatives() {
      return relatives;
    }

    @TOJSON
    public byte[] getTest() {
      return test;
    }

    @TOJSON
    public double getWeight() {
      return weight;
    }

    @TOJSON
    public Integer getYearsOfCollege() {
      return years_of_college;
    }
    
    @TOJSON
    public Character[] getYearsOfBlah() {
      Character[] s = new Character[1];
      s[0] = null;
      return s;
    }
    
    @TOJSON
    public Collection getYearsOfBlah2() {
      HashSet<Byte> h = new HashSet<Byte>();
      h.add(null);
      return h;
    }
    }

  public JSONTest() {
  }

  @Test
  public void testNonJsonableAccsrToJson() throws Exception {
    System.out.println("testNonJsonableAccsrToJson");
    Object o = new GetAccsrJsonableTestObject();
    // TODO: Change file location so test data is independent of file system
    // In a rush and should be an easy change
    BufferedReader r = new BufferedReader(new FileReader("src/test/org/json/correct_output_TestJsonableObject.json"));
    String s = "";
    String expResult = "";
    while ((s = r.readLine()) != null) {
      expResult += s;
    }
    String result = JSON.toJSON(o);
    JSONObject jsonExpected = new JSONObject(expResult);
    JSONObject jsonResult = new JSONObject(result);
    assertEquals(0, jsonExpected.compareTo(jsonResult));
  }
  }
