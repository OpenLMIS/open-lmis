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
import org.openlmis.odkapi.domain.ODKAccount;
import org.openlmis.odkapi.domain.ODKProofOfDeliverySubmissionData;
import org.openlmis.odkapi.domain.ODKSubmission;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class ODKProofOfDeliverySubmissionSAXHandler extends DefaultHandler
{
  private ODKAccount odkAccount;
  private List<ODKProofOfDeliverySubmissionData> odkProofOfDeliverySubmissionDataList;
  private ODKProofOfDeliverySubmissionData odkProofOfDeliverySubmissionData;
  private ODKSubmission odkSubmission;
  private ArrayList<String> deliveryPictures = new ArrayList<>();
  Long tempProductCode;


  boolean bdata;
  boolean bmeta;
  boolean binstanceId;
  boolean bitemName;
  boolean bquantityToDeliver;
  boolean ballQuantityDelivered;
  boolean bactualQuantityDelivered;
  boolean bdiscrepancyAmount;
  boolean bcommentForShortfallItem;
  boolean bfirstPictureOfDeliveredCartoons;
  boolean bsecondPictureOfDeliveredCartoons;
  boolean bthirdPictureOfDeliveredCartoons;
  boolean boverAllCommentoverAllComment;
  boolean breveivedBy;
  boolean bdeviceId;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException
    {
        if (qName.equals("data"))
        {
            bdata = true;
            odkSubmission = new ODKSubmission();
            odkSubmission.setFormBuildId(attributes.getValue(0));
        }

        if (qName.equals("bmeta"))
        {
            bmeta = true;
        }

        if (qName.equals("instanceID"))
        {
            binstanceId = true;
        }

        if (qName.equals("itemName"))
        {
            bitemName  = true;
            odkProofOfDeliverySubmissionData = new ODKProofOfDeliverySubmissionData();

        }

        if (qName.equals("quantityToDeliver"))
        {
            bquantityToDeliver= true;
        }

        if (qName.equals("allQuantityDelivered"))
        {
            ballQuantityDelivered  = true;
        }

        if (qName.equals("actualQuantityDelivered"))
        {
           bactualQuantityDelivered = true;
        }

        if (qName.equals("discrepancyAmount"))
        {
           bdiscrepancyAmount = true;
        }

        if (qName.equals("commentForShortfallItem"))
        {
         bcommentForShortfallItem = true;
        }

        if (qName.equals("firstPictureOfDeliveredCartoons"))
        {
          bfirstPictureOfDeliveredCartoons  = true;
        }

        if (qName.equals("secondPictureOfDeliveredCartoons"))
        {
          bsecondPictureOfDeliveredCartoons = true;
        }

        if (qName.equals("thirdPictureOfDeliveredCartoons"))
        {
          bthirdPictureOfDeliveredCartoons = true;
        }

        if (qName.equals("overAllCommentoverAllComment"))
        {
          boverAllCommentoverAllComment  = true;
        }

        if (qName.equals("reveivedBy"))
        {
            breveivedBy = true;
        }

        if (qName.equals("deviceID"))
        {
            odkAccount = new ODKAccount();
            bdeviceId = true;
        }

    }


    @Override
    public void characters(char ch[], int start, int length) throws SAXException
    {
        if (bdata)
        {
            bdata = false;
        }

        if (bmeta)
        {
            bmeta = false;
        }

        if (binstanceId)
        {
            this.odkSubmission.setInstanceId(new String(ch, start, length));
            binstanceId = false;
        }

        if (bitemName)
        {
            this.odkProofOfDeliverySubmissionData.setProductCode(new String(ch, start, length));
            bitemName  = false;
        }

        if (bquantityToDeliver)
        {
            bquantityToDeliver= false;
        }

        if (ballQuantityDelivered)
        {
            String temp = new String(ch, start, length);
            temp.trim();
            if (temp.equals("Yes"))
            {
               this.odkProofOfDeliverySubmissionData.setAllQuantityDelivered(true);
            }

            else
            {
                this.odkProofOfDeliverySubmissionData.setAllQuantityDelivered(false);
            }
            ballQuantityDelivered  = false;
        }

        if (bactualQuantityDelivered)
        {
            this.odkProofOfDeliverySubmissionData.setActualQuantityDelivered(Integer.parseInt(new String(ch, start, length)));
            bactualQuantityDelivered = false;
        }

        if (bdiscrepancyAmount)
        {
            this.odkProofOfDeliverySubmissionData.setDiscrepancyAmount(Integer.parseInt(new String(ch, start, length)));
            bdiscrepancyAmount = false;
        }

        if (bcommentForShortfallItem)
        {
            this.odkProofOfDeliverySubmissionData.setCommentForShortfallItem(new String(ch, start, length));
            bcommentForShortfallItem = false;
        }

        if (bfirstPictureOfDeliveredCartoons)
        {
            deliveryPictures.add(new String(ch, start, length));
            bfirstPictureOfDeliveredCartoons  = false;
        }

        if (bsecondPictureOfDeliveredCartoons)
        {
            deliveryPictures.add(new String(ch, start, length));
            bsecondPictureOfDeliveredCartoons = false;
        }

        if (bthirdPictureOfDeliveredCartoons)
        {
            deliveryPictures.add(new String(ch, start, length));
            bthirdPictureOfDeliveredCartoons = false;
        }

        if (boverAllCommentoverAllComment)
        {
            boverAllCommentoverAllComment  = false;
        }

        if (breveivedBy)
        {
            breveivedBy = false;
        }

        if (bdeviceId)
        {
            odkAccount.setDeviceId(new String(ch, start, length));
            bdeviceId = false;
        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if (qName.equals("data"))
        {
            bdata = false;
        }

        if (qName.equals("bmeta"))
        {
            bmeta = false;
        }

        if (qName.equals("instanceID"))
        {
            binstanceId = false;
        }

        if (qName.equals("itemName"))
        {
            bitemName  = false;
        }

        if (qName.equals("quantityToDeliver"))
        {
            bquantityToDeliver= false;
        }

        if (qName.equals("allQuantityDelivered"))
        {
            ballQuantityDelivered  = false;
        }

        if (qName.equals("actualQuantityDelivered"))
        {
            bactualQuantityDelivered = false;
        }

        if (qName.equals("discrepancyAmount"))
        {
            bdiscrepancyAmount = false;
        }

        if (qName.equals("commentForShortfallItem"))
        {
            bcommentForShortfallItem = false;
        }

        if (qName.equals("firstPictureOfDeliveredCartoons"))
        {

            bfirstPictureOfDeliveredCartoons  = false;
        }

        if (qName.equals("secondPictureOfDeliveredCartoons"))
        {
            bsecondPictureOfDeliveredCartoons = false;
        }

        if (qName.equals("thirdPictureOfDeliveredCartoons"))
        {
            bthirdPictureOfDeliveredCartoons = false;
        }

        if (qName.equals("overAllCommentoverAllComment"))
        {
            boverAllCommentoverAllComment  = false;
        }

        if (qName.equals("reveivedBy"))
        {
            breveivedBy = false;
        }

        if (qName.equals("deviceID"))
        {

            bdeviceId = false;
        }
    }

    public ODKAccount getOdkAccount() {
        return odkAccount;
    }

    public void setOdkAccount(ODKAccount odkAccount) {
        this.odkAccount = odkAccount;
    }

    public List<ODKProofOfDeliverySubmissionData> getOdkProofOfDeliverySubmissionDataList() {
        return odkProofOfDeliverySubmissionDataList;
    }

    public void setOdkProofOfDeliverySubmissionDataList(List<ODKProofOfDeliverySubmissionData> odkProofOfDeliverySubmissionDataList) {
        this.odkProofOfDeliverySubmissionDataList = odkProofOfDeliverySubmissionDataList;
    }

    public ODKProofOfDeliverySubmissionData getOdkProofOfDeliverySubmissionData() {
        return odkProofOfDeliverySubmissionData;
    }

    public void setOdkProofOfDeliverySubmissionData(ODKProofOfDeliverySubmissionData odkProofOfDeliverySubmissionData) {
        this.odkProofOfDeliverySubmissionData = odkProofOfDeliverySubmissionData;
    }

    public ODKSubmission getOdkSubmission() {
        return odkSubmission;
    }

    public void setOdkSubmission(ODKSubmission odkSubmission) {
        this.odkSubmission = odkSubmission;
    }

    public ArrayList<String> getDeliveryPictures() {
        return deliveryPictures;
    }

    public void setDeliveryPictures(ArrayList<String> deliveryPictures) {
        this.deliveryPictures = deliveryPictures;
    }

}

