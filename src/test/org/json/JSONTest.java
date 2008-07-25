/*
 * JSONTest.java
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
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
      name = "Michael Gottesman";
      age = 18;
      years_of_college = 1;
      weight = 160.0;
      height = 60.1;
      relatives = new GetAccsrJsonableTestObject[3];
      relatives[0] = new GetAccsrJsonableTestObject("Jacob Saxon");
      relatives[1] = new GetAccsrJsonableTestObject("Shoshi Gottesman");
      relatives[2] = new GetAccsrJsonableTestObject("Leopold Gottesman");
      friends = new String[2];
      friends[0] = new String("Gregory Hillman");
      friends[1] = new String("Alex Fleming");
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
    System.out.println("Expected:\n" + expResult);
    System.out.println("Actual:\n" + result);
    JSONObject jsonExpected = new JSONObject(expResult);
    JSONObject jsonResult = new JSONObject(result);
    assertEquals(0, jsonExpected.compareTo(jsonResult));
  }
  }
