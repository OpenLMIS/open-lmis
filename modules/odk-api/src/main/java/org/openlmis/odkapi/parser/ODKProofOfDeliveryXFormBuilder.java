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

import org.openlmis.rnr.domain.RnrLineItem;

import java.util.List;

public class ODKProofOfDeliveryXFormBuilder {

    private StringBuilder xformStringBuilder;


    public String buildXForm(String facilityName, String programAbbreviation, String periodString,List<RnrLineItem> listOfRequisitionItems)
    {

        int sizeOfList = listOfRequisitionItems.size();
        int itemCount = 0;

        xformStringBuilder = new StringBuilder();
        xformStringBuilder.append("<h:html xmlns=\"http://www.w3.org/2002/xforms\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:jr=\"http://openrosa.org/javarosa\">\n" +
                "\t<h:head>\n" +
                "\t\t<h:title>Proof of Delivery SurveyV2</h:title>\n" +
                "\t\t<model>\n" +
                "\t\t\t<instance>\n" +
                "\t\t\t\t<data id=\"");

        xformStringBuilder.append(facilityName + "-" + programAbbreviation + "-" + periodString +"\">");
        xformStringBuilder.append("<meta>\n" +
                "\t\t\t\t\t\t<instanceID/>\n" +
                "\t\t\t\t\t</meta>\n" +
                "\t\t\t\t\t<itemName/>");

        // add the data and instance section
        for(RnrLineItem lineItem :listOfRequisitionItems)
        {
            xformStringBuilder.append("<proof_of_delivery_survey_" + itemCount + ">");
            xformStringBuilder.append("<quantityToDeliver>" + lineItem.getQuantityRequested() + "</quantityToDeliver>");
            xformStringBuilder.append("<allQuantityDelivered/>");
            xformStringBuilder.append("<actualQuantityDelivered/>");
            xformStringBuilder.append("<discrepancyAmount/>");
            xformStringBuilder.append("<commentForShortfallItem/>");
            xformStringBuilder.append("<firstPictureOfDeliveredCartoons/>");
            xformStringBuilder.append("<secondPictureOfDeliveredCartoons/>");
            xformStringBuilder.append("<thirdPictureOfDeliveredCartoons/>");
            xformStringBuilder.append("<reveivedBy/>");
            xformStringBuilder.append("</proof_of_delivery_survey_" + itemCount + ">");
            itemCount ++;
        }

        // add the device id

        xformStringBuilder.append("<deviceID/>");

        // add end of data and instance

        xformStringBuilder.append("</data>\n" +
                "\t\t\t</instance>");
        // add itext section

        xformStringBuilder.append("<itext>\n" +
                "\t\t\t\t<translation lang=\"eng\">");



         // add the item name and option values
        itemCount = 0;

        xformStringBuilder.append("<text id=\"/data/itemName:label\">");
        xformStringBuilder.append("<value>Item Name </value> </text>");

        for(RnrLineItem lineItem :listOfRequisitionItems)
        {
          xformStringBuilder.append("<text id=\"/data/itemName:option"+itemCount+"\">");
          xformStringBuilder.append("<value>" + lineItem.getProduct()+ "</value> </text>");
          itemCount ++;
        }

        itemCount = 0;

        for(RnrLineItem lineItem :listOfRequisitionItems)
        {
            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+":label\">");
            xformStringBuilder.append("<value>" + lineItem.getProduct() + "</value> </text>");

            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+"/quantityToDeliver:label\">");
            xformStringBuilder.append("<value>Quantity To Deliver</value> </text>");

            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+"/allQuantityDelivered:label\">");
            xformStringBuilder.append("<value>All quantity Delivered ?</value> </text>");

            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+"/allQuantityDelivered:option0\">");
            xformStringBuilder.append("<value>Yes</value> </text>");

            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+"/allQuantityDelivered:option1\">");
            xformStringBuilder.append("<value>No</value> </text>");

            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+"/actualQuantityDelivered:label\">");
            xformStringBuilder.append("<value>Actual Delivered Quantity</value> </text>");

            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+"/discrepancyAmount:label\">");
            xformStringBuilder.append("<value>Discrepancy Amount</value> </text>");

            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+"/commentForShortfallItem:label\">");
            xformStringBuilder.append("<value>Comment For Shortfall</value> </text>");

            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+"/firstPictureOfDeliveredCartoons:label\">");
            xformStringBuilder.append("<value>First Picture of Delivered Cartoons</value> </text>");

            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+"/secondPictureOfDeliveredCartoons:label\">");
            xformStringBuilder.append("<value>Second Picture of Delivered Cartoons</value> </text>");

            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+"/thirdPictureOfDeliveredCartoons:label\">");
            xformStringBuilder.append("<value>Third Picture of Delivered Cartoons</value> </text>");

            xformStringBuilder.append("<text id=\"/data/proof_of_delivery_survey_"+itemCount+"/reveivedBy:label\">");
            xformStringBuilder.append("<value>Received By</value> </text>");

            itemCount++;
        }


        // add end of itext section
        xformStringBuilder.append("</translation>\n" +
                "\t\t\t</itext>");

        itemCount = 0;

        // add the bind section

        xformStringBuilder.append("<bind nodeset=\"/data/meta/instanceID\" type=\"string\" readonly=\"true()\" calculate=\"concat('uuid:', uuid())\"/>");
        xformStringBuilder.append("<bind nodeset=\"/data/itemName\" type=\"select1\" required=\"true()\"/>");

        for(RnrLineItem lineItem :listOfRequisitionItems)
        {
          xformStringBuilder.append("<bind nodeset=\"/data/proof_of_delivery_survey_"+itemCount+"\" relevant=\"(/data/itemName = '"+lineItem.getProductCode()+"')\"/>");
          itemCount ++;
        }

        itemCount = 0;

        for(RnrLineItem lineItem :listOfRequisitionItems)
        {
            xformStringBuilder.append("<bind nodeset=\"/data/proof_of_delivery_survey_"+itemCount+"/quantityToDeliver\" type=\"int\" readonly=\"true()\" />");
            xformStringBuilder.append("<bind nodeset=\"/data/proof_of_delivery_survey_"+itemCount+"/allQuantityDelivered\" type=\"select1\" required=\"true()\" />");
            xformStringBuilder.append("<bind nodeset=\"/data/proof_of_delivery_survey_"+itemCount+"/actualQuantityDelivered\" type=\"int\" required=\"true()\" relevant=\"(/data/proof_of_delivery_survey_"+itemCount+"/allQuantityDelivered = 'No')\"/>");
            xformStringBuilder.append("<bind nodeset=\"/data/proof_of_delivery_survey_"+itemCount+"/discrepancyAmount\" type=\"int\" required=\"true()\" relevant=\"(/data/proof_of_delivery_survey_"+itemCount+"/allQuantityDelivered = 'No')\"/>");
            xformStringBuilder.append("<bind nodeset=\"/data/proof_of_delivery_survey_"+itemCount+"/commentForShortfallItem\" type=\"string\" required=\"true()\" relevant=\"(/data/proof_of_delivery_survey_"+itemCount+"/allQuantityDelivered = 'No')\"/>");
            xformStringBuilder.append("<bind nodeset=\"/data/proof_of_delivery_survey_"+itemCount+"/firstPictureOfDeliveredCartoons\" type=\"binary\" />");
            xformStringBuilder.append("<bind nodeset=\"/data/proof_of_delivery_survey_"+itemCount+"/secondPictureOfDeliveredCartoons\" type=\"binary\" />");
            xformStringBuilder.append("<bind nodeset=\"/data/proof_of_delivery_survey_"+itemCount+"/thirdPictureOfDeliveredCartoons\" type=\"binary\" />");
            xformStringBuilder.append("<bind nodeset=\"/data/proof_of_delivery_survey_"+itemCount+"/reveivedBy\" type=\"string\" required=\"true()\"/>");
            itemCount ++;
        }
        // add the bind for the device id
        xformStringBuilder.append("<bind nodeset=\"/data/deviceID\" type=\"string\" jr:preload=\"property\" jr:preloadParams=\"deviceid\"/>");

        // close model   and head

        xformStringBuilder.append("</model>\n" +
                "\t</h:head>");


         // start the body part
        itemCount = 0;
        xformStringBuilder.append("<h:body>");

              // add the item name and option values
        xformStringBuilder.append("<select1 ref=\"/data/itemName\">\n" +
                "\t\t\t\t<label ref=\"jr:itext('/data/itemName:label')\"/>");

        for(RnrLineItem lineItem :listOfRequisitionItems)
        {
            xformStringBuilder.append("<item>\n" +
                    "\t\t\t\t\t<label ref=\"jr:itext('/data/itemName:option"+itemCount+"')\"/>");
            xformStringBuilder.append("<value>" +lineItem.getProductCode() + "</value> </item>");

            itemCount ++;
        }

        xformStringBuilder.append(" </select1>");

        // start adding the group

        itemCount = 0;

        for(RnrLineItem lineItem :listOfRequisitionItems)
        {

            xformStringBuilder.append("<group>");
            xformStringBuilder.append("<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+":label')\"/>");
            xformStringBuilder.append("<input ref=\"/data/proof_of_delivery_survey_"+itemCount+"/quantityToDeliver\">\n" +
                    "\t\t\t\t<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+"/quantityToDeliver:label')\"/>\n" +
                    "\t\t\t</input>");
            xformStringBuilder.append("<select1 ref=\"/data/proof_of_delivery_survey_"+itemCount+"/allQuantityDelivered\">\n" +
                    "\t\t\t\t<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+"/allQuantityDelivered:label')\"/>\n" +
                    "\t\t\t\t<item>\n" +
                    "\t\t\t\t\t<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+"/allQuantityDelivered:option0')\"/>\n" +
                    "\t\t\t\t\t<value>Yes</value>\n" +
                    "\t\t\t\t</item>\n" +
                    "\t\t\t\t<item>\n" +
                    "\t\t\t\t\t<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+"/allQuantityDelivered:option1')\"/>\n" +
                    "\t\t\t\t\t<value>No</value>\n" +
                    "\t\t\t\t</item>\n" +
                    "\t\t\t</select1>");
            xformStringBuilder.append("<input ref=\"/data/proof_of_delivery_survey_"+itemCount+"/actualQuantityDelivered\">\n" +
                    "\t\t\t\t<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+"/actualQuantityDelivered:label')\"/>\n" +
                    "\t\t\t</input>");
            xformStringBuilder.append("<input ref=\"/data/proof_of_delivery_survey_"+itemCount+"/discrepancyAmount\">\n" +
                    "\t\t\t\t<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+"/discrepancyAmount:label')\"/>\n" +
                    "\t\t\t</input>");
            xformStringBuilder.append("<input ref=\"/data/proof_of_delivery_survey_"+itemCount+"/commentForShortfallItem\">\n" +
                    "\t\t\t\t<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+"/commentForShortfallItem:label')\"/>\n" +
                    "\t\t\t</input>");
            xformStringBuilder.append("<upload ref=\"/data/proof_of_delivery_survey_"+itemCount+"/firstPictureOfDeliveredCartoons\" mediatype=\"image/*\">\n" +
                    "\t\t\t\t<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+"/firstPictureOfDeliveredCartoons:label')\"/>\n" +
                    "\t\t\t</upload>");
            xformStringBuilder.append("<upload ref=\"/data/proof_of_delivery_survey_"+itemCount+"/secondPictureOfDeliveredCartoons\" mediatype=\"image/*\">\n" +
                    "\t\t\t\t<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+"/secondPictureOfDeliveredCartoons:label')\"/>\n" +
                    "\t\t\t</upload>");
            xformStringBuilder.append("<upload ref=\"/data/proof_of_delivery_survey_"+itemCount+"/thirdPictureOfDeliveredCartoons\" mediatype=\"image/*\">\n" +
                    "\t\t\t\t<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+"/thirdPictureOfDeliveredCartoons:label')\"/>\n" +
                    "\t\t\t</upload>");

            xformStringBuilder.append("<input ref=\"/data/proof_of_delivery_survey_"+itemCount+"/reveivedBy\">");
            xformStringBuilder.append("<label ref=\"jr:itext('/data/proof_of_delivery_survey_"+itemCount+"/reveivedBy:label')\"/> </input>");

            xformStringBuilder.append("</group>");
            itemCount ++;
        }


        // end the body and document

        xformStringBuilder.append("</h:body> </h:html>");

        return xformStringBuilder.toString();
    }
}
