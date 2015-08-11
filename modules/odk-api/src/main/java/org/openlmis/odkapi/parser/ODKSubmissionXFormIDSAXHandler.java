/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.odkapi.parser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ODKSubmissionXFormIDSAXHandler extends DefaultHandler
{
    boolean bdata;
    String formBuildId;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException
    {
        if (qName.equals("data"))
        {
            bdata = true;
            setFormBuildId(attributes.getValue(0));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException
    {
        if(bdata)
        {
            bdata = false;
        }
    }

    public String getFormBuildId() {
        return formBuildId;
    }

    public void setFormBuildId(String formBuildId) {
        this.formBuildId = formBuildId;
    }
}
