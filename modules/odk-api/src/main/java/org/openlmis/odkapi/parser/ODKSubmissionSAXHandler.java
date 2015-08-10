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
import org.openlmis.odkapi.domain.ODKSubmissionData;
import org.openlmis.odkapi.domain.ODKSubmission;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class ODKSubmissionSAXHandler extends DefaultHandler
{
    private ODKAccount odkAccount;
    private List<ODKSubmissionData> listOfODKSubmissionData;
    private ODKSubmissionData odkSubmissionData;
    private ODKSubmission odkSubmission;
    private HashMap<Long,ArrayList<String>> facilityPictures = new HashMap<Long,ArrayList<String>>();
    Long tempFacilityId;
    ArrayList<String> tempPictures = new ArrayList<String>();

    boolean bdata;
    boolean bmeta;
    boolean binstanceID;
    boolean bfacility;
    boolean bfacilityGPSLocation;
    boolean blatitude;
    boolean blongitude;
    boolean baltitude;
    boolean baccuracy;
    boolean bfirstPicture;
    boolean bsecondPicture;
    boolean bthirdPicture;
    boolean bfourthPicture;
    boolean bfifthPicture;
    boolean bdeviceID;
    boolean bsubscriberid;
    boolean bsimserial;
    boolean bphonenumber;
    boolean busername;
    boolean bemail;



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

        if (qName.equals("meta"))
        {
            bmeta = true;
        }

        if (qName.equals("instanceID"))
        {
            binstanceID = true;
        }

        if (qName.equals("facility"))
        {
            bfacility = true;
            odkSubmissionData = new ODKSubmissionData();
            if (listOfODKSubmissionData == null)
            {
                listOfODKSubmissionData = new ArrayList<>();
            }

        }
        if (qName.equals("facilityGPSLocation"))
        {
            bfacilityGPSLocation = true;
        }
        if (qName.equals("Latitude"))
        {
            blatitude = true;
        }

        if (qName.equals("Longitude"))
        {
            blongitude = true;
        }

        if (qName.equals("Altitude"))
        {
            baltitude = true;
        }

        if (qName.equals("Accuracy"))
        {
            baccuracy = true;
        }

        if(qName.equals("firstPicture"))
        {
            bfirstPicture = true;
        }

        if(qName.equals("secondPicture"))
        {
            bsecondPicture = true;
        }

        if(qName.equals("thirdPicture"))
        {
            bthirdPicture = true;
        }

        if(qName.equals("fourthPicture"))
        {
            bfourthPicture = true;
        }

        if(qName.equals("fifthPicture"))
        {
            bfifthPicture = true;
        }

        if(qName.equals("deviceID"))
        {
            bdeviceID = true;
        }

        if(qName.equals("subscriberid"))
        {
            bsubscriberid = true;
        }

        if(qName.equals("simserial"))
        {
            bsimserial = true;
        }

        if(qName.equals("phonenumber"))
        {
            bphonenumber = true;
        }

        if(qName.equals("username"))
        {
            busername = true;
        }

        if(qName.equals("email"))
        {
            bemail = true;
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if (qName.equals("facilitiesGroup"))
        {
            odkSubmissionData.setFacilityPictures(new ArrayList<byte[]>());
            this.listOfODKSubmissionData.add(odkSubmissionData);
            facilityPictures.put(tempFacilityId, tempPictures);
            tempFacilityId = null;
            tempPictures = null;
        }

        if(qName.equals("firstPicture"))
        {
            bfirstPicture = false;
        }

        if(qName.equals("secondPicture"))
        {
            bsecondPicture = false;
        }

        if(qName.equals("thirdPicture"))
        {
            bthirdPicture = false;
        }

        if(qName.equals("fourthPicture"))
        {
            bfourthPicture = false;
        }

        if(qName.equals("fifthPicture"))
        {
            bfifthPicture = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException
    {
         if(bdata)
         {
             bdata = false;
         }
         else if (bmeta)
         {
             bmeta = false;
         }
         else if (binstanceID)
         {
             this.odkSubmission.setInstanceId(new String(ch, start, length));
             binstanceID = false;
         }
        else if(bfacility)
        {
            this.odkSubmissionData.setFacilityId(Long.parseLong(new String(ch, start, length)));
            tempFacilityId = this.odkSubmissionData.getFacilityId();
            bfacility = false;
        }

         else if (bfacilityGPSLocation)
         {
             // format : 8.9602334 38.7691866 0.0 2828.0
             String temp = new String(ch, start, length);
             String[] vals = temp.split(" ");
             this.odkSubmissionData.setGPSLatitude(Double.parseDouble(vals[0]));
             this.odkSubmissionData.setGPSLongitude(Double.parseDouble(vals[1]));
             this.odkSubmissionData.setGPSAltitude(Double.parseDouble(vals[2]));
             this.odkSubmissionData.setGPSAccuracy(Double.parseDouble(vals[3]));
             bfacilityGPSLocation = false;
         }

        else if (blatitude)
        {
           // this.odkSubmissionData.setGPSLatitude(Double.parseDouble(new String(ch, start, length)));
            blatitude = false;
        }
        else if (blongitude)
        {
          //  this.odkSubmissionData.setGPSLongitude(Double.parseDouble(new String(ch, start, length)));
            blongitude = false;
        }
        else if (baltitude)
        {
          //  this.odkSubmissionData.setGPSAltitude(Double.parseDouble(new String(ch, start, length)));
            baltitude = false;
        }

        else if (baccuracy)
        {
           // this.odkSubmissionData.setGPSAccuracy(Double.parseDouble(new String(ch, start, length)));
            baccuracy = false;
        }
        else if (bfirstPicture)
         {
             tempPictures.add(new String(ch, start, length));
             bfirstPicture = false;
         }

         else if (bsecondPicture)
         {
             tempPictures.add(new String(ch, start, length));
             bsecondPicture = false;
         }

        else if (bthirdPicture)
         {
             tempPictures.add(new String(ch, start, length));
             bthirdPicture = false;
         }

         else if (bfourthPicture)
         {
             tempPictures.add(new String(ch, start, length));
             bfourthPicture = false;
         }
         else if (bfifthPicture)
         {
             tempPictures.add(new String(ch, start, length));
             bfifthPicture = false;
         }
        else if (bdeviceID)
         {
             this.odkAccount = new ODKAccount();
             this.odkAccount.setDeviceId(new String(ch, start, length));
             bdeviceID = false;
         }

         else if (bsubscriberid)
         {
             this.odkAccount.setSubscriberId(new String(ch, start, length));
             bsubscriberid = false;
         }

         else if (bsimserial)
         {
             this.odkAccount.setSIMSerial(new String(ch, start, length));
             bsimserial = false;
         }

         else if(bphonenumber)
         {
             this.odkAccount.setPhoneNumber(new String(ch, start, length));
             bphonenumber = false;
         }

         else if(busername)
         {
             this.odkAccount.setODKUserName(new String(ch, start, length));
             busername = false;
         }

         else if (bemail)
         {
             this.odkAccount.setODKEmail(new String(ch, start, length));
             bemail = false;
         }

        else
        {
            // Do Nothing
        }

    }
    public List<ODKSubmissionData> getListOfODKSubmissionData()
    {
        return listOfODKSubmissionData;
    }

    public ODKSubmission getOdkSubmission()
    {
        return this.odkSubmission;
    }

    public HashMap<Long,ArrayList<String>> getFacilityPictures()
    {
        return this.facilityPictures;
    }

    public ODKAccount getOdkAccount()
    {
        return this.odkAccount;
    }

}
