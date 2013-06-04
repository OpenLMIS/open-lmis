package org.openlmis.functional;

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


public class XmlUtils {



    public static Element getRootElement(String response)
            throws ParserConfigurationException, SAXException, IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(response.getBytes()));

            Element rootElement = document.getDocumentElement();
            return rootElement;
        } catch (Exception e) {
            return null;
        }
    }
    public static List<String> getNodeValues(String response, String tagName) throws ParserConfigurationException, SAXException, IOException{
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(response.getBytes()));

            Element rootElement = document.getDocumentElement();

            List<String> arrayList = new ArrayList<String>();
            NodeList list = rootElement.getElementsByTagName(tagName);
            NodeList subList = null;
                if (list != null && list.getLength() > 0)
                {
                    for (int k = 0; k < list.getLength(); k++)
                    {
                        subList = list.item(k).getChildNodes();
                        if (subList != null && subList.getLength() > 0)
                        {
                            arrayList.add(subList.item(0).getNodeValue());
                        }
                    }
                return arrayList;
                }
        }
        catch (Exception e) {
            return null;
        }
        return null;
    }



}

