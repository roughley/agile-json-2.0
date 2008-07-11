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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gottesmm
 */
public class JSONTest {

  protected static class PublicFieldJsonableTestObject implements Jsonable {

    public String name;
    public int age;
    public Integer years_of_college;
    public double weight;
    public Double height;
    public PublicFieldJsonableTestObject[] relatives;
    public String[] friends;
    public byte[] test;

    public PublicFieldJsonableTestObject() {
      name = "Michael Gottesman";
      age = 18;
      years_of_college = 1;
      weight = 160.0;
      height = 60.1;
      relatives = new PublicFieldJsonableTestObject[3];
      relatives[0] = new PublicFieldJsonableTestObject("Jacob Saxon");
      relatives[1] = new PublicFieldJsonableTestObject("Shoshi Gottesman");
      relatives[2] = new PublicFieldJsonableTestObject("Leopold Gottesman");
      friends = new String[2];
      friends[0] = new String("Gregory Hillman");
      friends[1] = new String("Alex Fleming");
      test = new String("test123412341234").getBytes();
    }

    public PublicFieldJsonableTestObject(String name) {
      this.name = name;
    }
    }

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
    public Integer getYears_of_college() {
      return years_of_college;
    }
    }

  public static class BothJsonableTestObject extends PublicFieldJsonableTestObject {
    public BothJsonableTestObject[] relatives;
    
    protected String name1;
    protected int age1;
    protected Integer years_of_college1;
    protected double weight1;
    protected Double height1;
    protected BothJsonableTestObject[] relatives1;
    protected String[] friends1;
    protected byte[] test1;
    
    public BothJsonableTestObject() {
      name = "Michael Gottesman";
      age = 18;
      years_of_college = 1;
      weight = 160.0;
      height = 60.1;
      relatives = new BothJsonableTestObject[3];
      relatives[0] = new BothJsonableTestObject("Jacob Saxon");
      relatives[1] = new BothJsonableTestObject("Shoshi Gottesman");
      relatives[2] = new BothJsonableTestObject("Leopold Gottesman");
      friends = new String[2];
      friends[0] = new String("Gregory Hillman");
      friends[1] = new String("Alex Fleming");
      test = new String("test123412341234").getBytes();
      
      name1 = "Michael Gottesman";
      age1 = 18;
      years_of_college1 = 1;
      weight1 = 160.0;
      height1 = 60.1;
      relatives1 = new BothJsonableTestObject[3];
      relatives1[0] = new BothJsonableTestObject("Jacob Saxon");
      relatives1[1] = new BothJsonableTestObject("Shoshi Gottesman");
      relatives1[2] = new BothJsonableTestObject("Leopold Gottesman");
      friends1 = new String[2];
      friends1[0] = new String("Gregory Hillman");
      friends1[1] = new String("Alex Fleming");
      test1 = new String("test123412341234").getBytes();
    }
    
    public BothJsonableTestObject(String name) {
      this.name = name;
      this.name1 = name;
    }

    @TOJSON
    public String getName1() {
      return name1;
    }
    
    @TOJSON
    public int getAge1() {
      return age1;
    }

    @TOJSON
    public String[] getFriends1() {
      return friends1;
    }

    @TOJSON
    public Double getHeight1() {
      return height1;
    }

    @TOJSON
    public BothJsonableTestObject[] getRelatives1() {
      return relatives1;
    }

    @TOJSON
    public byte[] getTest1() {
      return test1;
    }

    @TOJSON
    public double getWeight1() {
      return weight1;
    }

    @TOJSON
    public Integer getYears_of_college1() {
      return years_of_college1;
    }
    }

  public JSONTest() {
  }

  
  @Test
  public void testJsonablePrimitiveActual() throws JSONException, IllegalAccessException {
    Jsonable j = new Jsonable() {
      public byte pByte = 126;
      public short pShort = 32766;
      public int pInt = 5;
      public long pLong = 9233456;
      public float pFloat = 12.345F;
      public double pDouble = 954.1245;
      public boolean pBoolean = true;
      public char pChar = 'b';
    };
    String s = JSON.toJSON(j);
    String exp = "{\"pByte\":126,\"pShort\":32766,\"pInt\":5,\"pLong\":9233456,\"pFloat\":12.345,\"pDouble\":954.1245,\"pBoolean\":true,\"pChar\":b}";
    System.out.println("Expected: " + exp);
    System.out.print("Actual: " +s);
    assertEquals(s,exp);
  }
  
  @Test
  public void testJsonablePrimitiveObject() {
    Jsonable j = new Jsonable() {
      public Integer i;
    };
  }
  
  /**
   * Test of toJSON method, of class JSON.
   * @throws java.lang.Exception 
   */
  @Test
  public void testJsonableObjectToJson() throws Exception {
    System.out.println("testJsonableObjectToJson");
    Object o = new PublicFieldJsonableTestObject();
    // TODO: Change file location so test data is independent of file system
    // In a rush and should be an easy change
    BufferedReader r = new BufferedReader(new FileReader("src/test/org/json/correct_output_TestJsonableObject.json"));
    String s = "";
    String expResult = "";
    while ((s = r.readLine()) != null) {
      expResult += s;
    }
    String result = JSON.toJSON(o);
    System.out.println("Expected: \n" + expResult);
    System.out.println("Actual: \n" + result);
    assertEquals(expResult, result);
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

//  @Test
//  public void testBoth() throws Exception {
//    System.out.println("testBoth");
//    Object o = new BothJsonableTestObject();
//    // TODO: Change file location so test data is independent of file system
//    // In a rush and should be an easy change
//    BufferedReader r = new BufferedReader(new FileReader("src/test/org/json/correct_output_TestJsonableObject2.json"));
//    String s = "";
//    String expResult = "";
//    while ((s = r.readLine()) != null) {
//      expResult += s;
//    }
//    String result = JSON.toJSON(o);
//    JSONObject jsonExpected = new JSONObject(expResult);
//    JSONObject jsonResult = new JSONObject(result);
//    assertEquals(0, jsonExpected.compareTo(jsonResult));
//  }
  }
