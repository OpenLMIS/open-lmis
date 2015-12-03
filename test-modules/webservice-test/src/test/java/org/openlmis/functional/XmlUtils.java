/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.functional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;


public class XmlUtils {

  public static List<String> getNodeValues(String response, String tagName) throws ParserConfigurationException, SAXException {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory
        .newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(new ByteArrayInputStream(response.getBytes()));

      Element rootElement = document.getDocumentElement();

      List<String> arrayList = new ArrayList<>();
      NodeList list = rootElement.getElementsByTagName(tagName);
      NodeList subList;
      if (list != null && list.getLength() > 0) {
        for (int k = 0; k < list.getLength(); k++) {
          subList = list.item(k).getChildNodes();
          if (subList != null && subList.getLength() > 0) {
            arrayList.add(subList.item(0).getNodeValue());
          }
        }
        return arrayList;
      }
    } catch (Exception e) {
      return null;
    }
    return null;
  }


}

