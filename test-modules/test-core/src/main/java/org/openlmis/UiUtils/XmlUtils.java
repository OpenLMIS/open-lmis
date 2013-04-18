package org.openlmis.UiUtils;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XmlUtils {

  private static InputStream responseStream = null;
  private static String responseString = "";
  private static String strURLProperty = null;


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
  public static List<String> getAttributeValuesOfNode(String tagName,
                                                      String attributeName, Element element) {
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
  public static String getAttributeValueOfNode(String tagName,
                                               String attributeName, Element element, int pos) {
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
      FileInputStream fstream = new FileInputStream(filePath);
      DataInputStream in = new DataInputStream(fstream);
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
        SeleneseTestNgHelper.fail("Failed due to : " + xmlFile + " file not found.");
        flag = false;
      }
    } catch (Exception e) {
      SeleneseTestNgHelper.fail("Failed due to exception :" + e);
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
      SeleneseTestNgHelper.fail("Failed in writing into xml file");
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
  * Dummy code
  */

//  @SuppressWarnings("finally")
//  public static int verifyTagValue(String xmlFile,
//                                   Element rootElement, String Tag, String tagAttribute, String expectedTagValue) {
//    String flag = "";
//    int flagFinal = 0;
//    try {
//
//      rootElement = getRootElement(xmlFile);
//
//      List<String> attributeList = getAttributeValuesOfNode(
//        Tag, tagAttribute, rootElement);
//
//      for (String attribute : attributeList) {
//        flag = attribute;
//        if (flag.trim().equals(expectedTagValue))
//          break;
//      }
//      flagFinal = Integer.parseInt(flag.trim());
//    }  catch (ParserConfigurationException e) {
//      flagFinal = 1;
//      SeleneseTestNgHelper.fail("Failed in verifying tag due to exception :" + e);
//      e.printStackTrace();
//    } catch (SAXException e) {
//      flagFinal = 1;
//      SeleneseTestNgHelper.fail("Failed in verifying tag due to exception :" + e);
//      e.printStackTrace();
//    }  catch (Exception e) {
//      flagFinal = 1;
//      SeleneseTestNgHelper.fail("Failed in verifying tag due to exception :" + e);
//      e.printStackTrace();
//    } finally {
//      return flagFinal;
//    }
//  }

}

