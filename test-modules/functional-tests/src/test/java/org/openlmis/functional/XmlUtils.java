package org.openlmis.functional;


import com.sun.jna.platform.win32.NTSecApi;
import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.fail;


public class XmlUtils {

  /*
    * This function will return root element of xml file
    */

  public static Element getRootElement(String responseFile)
    throws ParserConfigurationException, SAXException, IOException {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory
        .newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(new File(responseFile));

      Element rootElement = document.getDocumentElement();
      return rootElement;
    } catch (Exception e) {
      return null;
    }
  }


  /*
  * Reusable function to get the attribute value of a node in xml file
  * <StatusCode class="0"/>
  */
  public static List<String> getAttributeValuesOfNode(String tagName, String attributeName, Element element) {
    String arr = null;
    List<String> list = new ArrayList<String>();

    NodeList nl = element.getElementsByTagName(tagName);
    if (nl != null && nl.getLength() > 0) {
      for (int i = 0; i < nl.getLength(); i++) {

        Element el = (Element) nl.item(i);
        arr = el.getAttribute(attributeName);
        list.add(arr);
      }
      return list;
    }
    return null;
  }

  /*
    * Reusable function to get a single attribute value based on index
    * <StatusCode class="0"/>
    */
  public static String getAttributeValueOfNode(String tagName, String attributeName, Element element, int pos) {
    String arr = null;

    NodeList nl = element.getElementsByTagName(tagName);
    if (nl != null && nl.getLength() > 0) {

      Element el = (Element) nl.item(pos);
      arr = el.getAttribute(attributeName);

      return arr;
    }
    return null;
  }


  /*
  * Reusable function to count the number of tags without attribute
  */
  public static int getTagCount(String tagName, Element element) {
    int count = 0;
    NodeList list = element.getElementsByTagName(tagName);
    NodeList subList = null;
    if (list != null && list.getLength() > 0) {
      for (int k = 0; k < list.getLength(); k++) {
        subList = list.item(k).getChildNodes();

        if (subList != null && subList.getLength() > 0) {
          count++;
        }
      }
      return count;
    }
    return count;
  }

  /*
    * Java function to read file content
    */
  @SuppressWarnings("finally")
  public static String readFile(String filePath) {
    String strLine, strLineFinal = "";
    try {
      FileInputStream fStream = new FileInputStream(filePath);
      DataInputStream in = new DataInputStream(fStream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));

      while ((strLine = br.readLine()) != null) {
        strLineFinal = strLineFinal + "\n" + strLine;
      }
      in.close();
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    } finally {
      return strLineFinal;
    }
  }

  /*
    * Function for deleting file Args : filename
    */
  public static void deleteFile(String fileName) {
    File f = new File(fileName);

    if (!f.exists())
      throw new IllegalArgumentException(
        "Delete: no such file or directory: " + fileName);

    if (!f.canWrite())
      throw new IllegalArgumentException("Delete: write protected: "
        + fileName);

    if (f.isDirectory()) {
      String[] files = f.list();
      if (files.length > 0)
        throw new IllegalArgumentException(
          "Delete: directory not empty: " + fileName);
    }

    boolean success = f.delete();

    if (!success)
      throw new IllegalArgumentException("Delete: deletion failed");
  }


  /*
    * Function to verify whether xml file exists or not
    */

  @SuppressWarnings("finally")
  public static boolean verifyXMLExistence(String xmlFile) {
    boolean flag = false;
    try {

      File requestXML = new File(xmlFile);

      if (requestXML.exists()) {
        flag = true;
      } else {
        fail("Failed due to : " + xmlFile + " file not found.");
        flag = false;
      }
    } catch (Exception e) {
      fail("Failed due to exception :" + e);
      e.printStackTrace();
    } finally {
      return flag;
    }
  }

  /*
    * Function to write some content into a file
    */

  public static void writeFile(String stringToRight, String responseFile) {
    BufferedWriter fw;
    try {
      fw = new BufferedWriter(new FileWriter(responseFile));
      fw.write(stringToRight);
      fw.close();
    } catch (IOException e) {
      fail("Failed in writing into xml file");
    }
  }


  /*
    * Reusable function to get the node(without an attribute) value
     * For example : <node>3</node>
    */

  public static List<String> getNodeValues(String tagName, Element element) {
    List<String> arrayList = new ArrayList<String>();
    NodeList list = element.getElementsByTagName(tagName);
    NodeList subList = null;
    if (list != null && list.getLength() > 0) {
      for (int k = 0; k < list.getLength(); k++) {
        subList = list.item(k).getChildNodes();
        if (subList != null && subList.getLength() > 0) {
          arrayList.add(subList.item(0).getNodeValue());
        }
      }
      return arrayList;
    }
    return null;
  }

  /*
  Dummy Test code
   */

//  WebDriver webDriver;
//
//  @BeforeMethod
//  public void setUp() {
//    webDriver = new FirefoxDriver();
//    webDriver.get("http://localhost:9091");
//  }
//
//  @Test
//  private void testXmlUtils() throws IOException, SAXException, ParserConfigurationException, InterruptedException {
//    String filePath = this.getClass().getClassLoader().getResource("DummyXml.xml").getFile();
//    Element rootElement = getRootElement(filePath);
//    List<String> attributeValues=getNodeValues("to", rootElement);
//    Thread.sleep(1000);
//
//    String[] array=new String[2];
//    array[0]="Dummy";
//    array[1]="Data";
//    int i=0;
//    for(String attributeValue : attributeValues) {
//      assertEquals(attributeValue, array[i]);
//      i++;
//    }
//
//    List<String> attributeValuesNew=getAttributeValuesOfNode("from","class",rootElement);
//    for(String attributeValueNew : attributeValuesNew) {
//      assertEquals(attributeValueNew, "0");
//    }
//
//    assertEquals(getTagCount("to", rootElement), "2");
//  }
//
//
//  @AfterMethod
//  public void tearDown() {
//    webDriver.quit();
//  }



}

