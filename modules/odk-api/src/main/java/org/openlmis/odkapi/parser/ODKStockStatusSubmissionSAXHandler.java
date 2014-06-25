/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
/**
 * Created with IntelliJ IDEA.
 * User: Messay Yohannes <deliasmes@gmail.com>
 * To change this template use File | Settings | File Templates.
 */
package org.openlmis.odkapi.parser;
import org.openlmis.odkapi.domain.ODKAccount;
import org.openlmis.odkapi.domain.ODKStockStatusSubmission;
import org.openlmis.odkapi.domain.ODKSubmission;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class ODKStockStatusSubmissionSAXHandler extends  DefaultHandler
{
    private ODKAccount odkAccount = new ODKAccount();
    private List<ODKStockStatusSubmission> listODKStockStatusSubmissions = new ArrayList<>();
    private ODKSubmission odkSubmission;
    private ODKStockStatusSubmission tempODKStockStatusSubmission;
    private String tempString;

    boolean bdata;
    boolean bmeta;
    boolean binstanceID;
    boolean bstockStatusSurvey;
    boolean bmsdCode;
    boolean bcommodityName;
    boolean bmanaged;
    boolean bphysicalInventory;
    boolean bquantityExpiredToday;
    boolean bstockCardAvailable;
    boolean bstockDataThreeMonths;
    boolean bsoSevenDays;
    boolean btotalDaysStockedoutThreeMonths;
    boolean bissuedThreeMonths;
    boolean bdaysDataAvailable;
    boolean bdeviceID;



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

        if (qName.equals("stock_status_survey"))
        {
            tempODKStockStatusSubmission = new ODKStockStatusSubmission();
            bstockStatusSurvey = true;
        }

        if (qName.equals("MSD_Code"))
        {
            bmsdCode = true;
        }

        if (qName.equals("Commodity_Name"))
        {
           bcommodityName = true;
        }

        if (qName.equals("Managed"))
        {
            bmanaged = true;
        }

        if (qName.equals("Physical_inventory"))
        {
            bphysicalInventory = true;
        }

        if (qName.equals("QtyExpiredToday"))
        {
            bquantityExpiredToday = true;
        }

        if (qName.equals("StockCardAvailable"))
        {
           bstockCardAvailable = true;
        }

        if (qName.equals("StockData3Months"))
        {
            bstockDataThreeMonths = true;
        }

        if (qName.equals("SO_seven_days"))
        {
            bsoSevenDays = true;
        }

        if (qName.equals("TotalDaysStockedout3months"))
        {
            btotalDaysStockedoutThreeMonths = true;
        }

        if (qName.equals("Issued3Months"))
        {
            bissuedThreeMonths = true;
        }

        if (qName.equals("DaysDataAvailable"))
        {
            bdaysDataAvailable = true;
        }

        if (qName.equals("bdeviceID"))
        {
            bdeviceID = true;
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
       if (qName.equals("data"))
       {
           bdata = false;
       }

        if (qName.equals("stock_status_survey"))
        {
            listODKStockStatusSubmissions.add(tempODKStockStatusSubmission);
            bstockStatusSurvey = false;
        }

        if (qName.equals("MSD_Code"))
        {
            bmsdCode = false;
        }

        if (qName.equals("Commodity_Name"))
        {
            bcommodityName = false;
        }

        if (qName.equals("Managed"))
        {
            bmanaged = false;
        }

        if (qName.equals("Physical_inventory"))
        {
            bphysicalInventory = false;
        }

        if (qName.equals("QtyExpiredToday"))
        {
            bquantityExpiredToday = false;
        }

        if (qName.equals("StockCardAvailable"))
        {
            bstockCardAvailable = false;
        }

        if (qName.equals("StockData3Months"))
        {
            bstockDataThreeMonths = false;
        }

        if (qName.equals("SO_seven_days"))
        {
            bsoSevenDays = false;
        }

        if (qName.equals("TotalDaysStockedout3months"))
        {
            btotalDaysStockedoutThreeMonths = false;
        }

        if (qName.equals("Issued3Months"))
        {
            bissuedThreeMonths = false;
        }

        if (qName.equals("DaysDataAvailable"))
        {
            bdaysDataAvailable = false;
        }

        if (qName.equals("bdeviceID"))
        {
            bdeviceID = false;
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

        else if (bmsdCode)
        {
            tempODKStockStatusSubmission.setMSDCode(new String(ch, start, length));
            bmsdCode = false;
        }

        else if (bcommodityName)
        {
            tempODKStockStatusSubmission.setCommodityName(new String(ch, start, length));
            bcommodityName = false;
        }

        else if (bmanaged)
        {
            tempString = new String(ch, start, length);
            tempString.trim();
            if (tempString.equals("Yes"))
            {
                tempODKStockStatusSubmission.setManaged(true);
            }

            else
            {
                tempODKStockStatusSubmission.setManaged(false);
            }
            bmanaged = false;
        }

        else if (bphysicalInventory)
        {
            tempODKStockStatusSubmission.setPhysicalInventory(Double.parseDouble(new String(ch, start, length)));
            bphysicalInventory = false;
        }

        else if (bquantityExpiredToday)
        {
            tempODKStockStatusSubmission.setQuantityExpiredToday(Integer.parseInt(new String(ch, start, length)));
            bquantityExpiredToday = false;
        }

        else if (bstockCardAvailable)
        {
            tempString = new String(ch, start, length);
            tempString.trim();
            if (tempString.equals("Yes"))
            {
                tempODKStockStatusSubmission.setStockCardAvailable(true);
            }

            else
            {
                tempODKStockStatusSubmission.setStockCardAvailable(false);
            }
            bstockCardAvailable = false;
        }

        else if (bstockDataThreeMonths)
        {
            tempString = new String(ch, start, length);
            tempString.trim();
            if (tempString.equals("Yes"))
            {
                tempODKStockStatusSubmission.setStockDataThreeMonths(true);
            }

            else
            {
                tempODKStockStatusSubmission.setStockDataThreeMonths(false);
            }
            bstockDataThreeMonths = false;
        }

        else if (bsoSevenDays)
        {
            tempString = new String(ch, start, length);
            tempString.trim();
            if (tempString.equals("Yes"))
            {
                tempODKStockStatusSubmission.setSOSevenDays(true);
            }

            else
            {
                tempODKStockStatusSubmission.setSOSevenDays(false);
            }
            bsoSevenDays = false;
        }

        else if (btotalDaysStockedoutThreeMonths)
        {
            tempODKStockStatusSubmission.setTotalDaysStockedoutThreeMonths(Integer.parseInt(new String(ch, start, length)));
            btotalDaysStockedoutThreeMonths = false;
        }

        else if (bissuedThreeMonths)
        {
            tempODKStockStatusSubmission.setIssuedThreeMonths(Double.parseDouble(new String(ch, start, length)));
            bissuedThreeMonths = false;
        }

        else if (bdaysDataAvailable)
        {
            tempODKStockStatusSubmission.setDaysDataAvailable(Integer.parseInt(new String(ch, start, length)));
            bdaysDataAvailable = false;
        }

        else if (bdeviceID)
        {
            odkAccount.setDeviceId(new String(ch, start, length));
            bdeviceID = false;
        }

    }
    public List<ODKStockStatusSubmission> getListODKStockStatusSubmissions() {
        return listODKStockStatusSubmissions;
    }

    public void setListODKStockStatusSubmissions(List<ODKStockStatusSubmission> listODKStockStatusSubmissions) {
        this.listODKStockStatusSubmissions = listODKStockStatusSubmissions;
    }

    public ODKAccount getOdkAccount() {
        return odkAccount;
    }

    public void setOdkAccount(ODKAccount odkAccount) {
        this.odkAccount = odkAccount;
    }

    public ODKSubmission getOdkSubmission() {
        return odkSubmission;
    }

    public void setOdkSubmission(ODKSubmission odkSubmission) {
        this.odkSubmission = odkSubmission;
    }
}
