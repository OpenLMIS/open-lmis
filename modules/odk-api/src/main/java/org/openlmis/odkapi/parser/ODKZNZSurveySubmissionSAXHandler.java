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

import org.openlmis.odkapi.domain.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;


public class ODKZNZSurveySubmissionSAXHandler extends DefaultHandler {

    private ODKAccount odkAccount;
    private ODKSubmission odkSubmission;
    private ODKLMISSurveySubmission odkLmisSurveySubmission;
    private ODKRandRSubmission odkRandRSubmission;
    private ODKStorageSurveySubmission odkStorageSurveySubmission;
    private String tempString;

    private ArrayList<String> pictures = new ArrayList<String>();

    boolean bdata;
    boolean bmeta;
    boolean binstanceID;
    boolean bcountry;
    boolean bzone;
    boolean bregion;
    boolean bdistrict;
    boolean bfacility;

    // storage survey
    boolean bastorage;
    boolean badequateStorageSpace;
    boolean badequateShelves;
    boolean bstoreRoomClean;
    boolean bproductsArrangedAppropriately;
    boolean bproductsStoredIssued;
    boolean bmedicinesStoredSeparately;
    boolean bcoldChainFollowed;
    boolean bproductsFreeFromDusts;
    boolean bproductsFreeFromMoisture;
    boolean bproductsFreeFromSunlight;
    boolean bstoreRoomPreventedFromInfestation;
    boolean badequateSecurity;
    boolean bfireExtinguisherAvailable;
    boolean bstoreRoomConditionConductive;
    boolean bcontrolForUnauthorizedPersonnel;

    // lmis tools survey
    boolean blmisToolsRecordKeeping;
    boolean bstoreLedgerAvailable;
    boolean bstoreLedgersInStoreRoom;
    boolean bbinCardsAvailable;
    boolean bbinCardsKeptWithProducts;
    boolean bendingBalancesEqualToStocks;
    boolean blossesAdjustmentsCorrectlyFilled;
    boolean bledgersBinCardsFilledCorrectly;
    boolean bphysicalStockCountsExercisesConducted;
    boolean bddrAvailable;
    boolean binvoicesKeptInFile;
    boolean blastSupervisionVisitInFile;

    // r and r survey
    boolean brandr;
    boolean brandrAvailable;
    boolean bopeningEndingBalancesEqual;
    boolean bendingBalanceCorrespondsToLedger;
    boolean bconsumptionEstimationCorrectlyFilled;
    boolean bstockOutAdjustmentCorrect;
    boolean bquantityRequiredCorrectlyFilled;
    boolean bcolumnOfCostsFilledCorrectly;
    boolean brandrFormsFilled;

    // GPS and pictures

    boolean bfacilityGPSLocation;
    boolean blatitude;
    boolean blongitude;
    boolean baltitude;
    boolean baccuracy;
    boolean bfirstPicture;
    boolean bsecondPicture;
    boolean bthirdPicture;

    boolean bdeviceID;

    double storageTotalPercentage = 0;
    double lmisTotalPercentage = 0;
    double randrTotalPercentage = 0;


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

        if (qName.equals("country"))
        {
            bcountry = true;
        }

        if (qName.equals("zone")) {
            bzone = true;
        }

        if (qName.equals("region"))
        {
            bregion = true;
        }

        if (qName.equals("district"))
        {
            bdistrict = true;
        }

        if (qName.equals("facility"))
        {
            bfacility = true;

        }

        // storage

        if (qName.equals("a_storage")) {
            bastorage = true;
        }

        if (qName.equals("adequate_storage_space"))
        {
            badequateStorageSpace = true;
        }

        if (qName.equals("adequate_shelves"))
        {
            badequateShelves = true;
        }

        if (qName.equals("store_room_clean"))
        {
            bstoreRoomClean = true;
        }

        if (qName.equals("products_arranged_appropriately"))
        {
            bproductsArrangedAppropriately = true;
        }

        if (qName.equals("products_stored_issued"))
        {
            bproductsStoredIssued = true;
        }

        if (qName.equals("medicines_stored_separately"))
        {
            bmedicinesStoredSeparately = true;
        }

        if (qName.equals("cold_chain_followed"))
        {
            bcoldChainFollowed = true;
        }

        if (qName.equals("bproductsFreeFromDusts"))
        {
            bproductsFreeFromDusts = true;
        }

        if (qName.equals("products_free_from_moisture"))
        {
            bproductsFreeFromMoisture = true;
        }

        if (qName.equals("products_free_from_sunlight"))
        {
            bproductsFreeFromSunlight = true;
        }

        if (qName.equals("store_room_prevented_from_infestation"))
        {
            bstoreRoomPreventedFromInfestation = true;
        }

        if (qName.equals("adequate_security"))
        {
            badequateSecurity = true;
        }

        if (qName.equals("fire_extinguisher_available"))
        {
            bfireExtinguisherAvailable = true;
        }

        if (qName.equals("store_room_condition_conductive"))
        {
            bstoreRoomConditionConductive = true;
        }

        if (qName.equals("control_for_unauthorized_personnel"))
        {
            bcontrolForUnauthorizedPersonnel = true;
        }

        // lmis tools

        if (qName.equals("store_ledger_available"))
        {
            bstoreLedgerAvailable= true;
        }

        if (qName.equals("store_ledgers_in_store_room"))
        {
            bstoreLedgersInStoreRoom= true;
        }

        if (qName.equals("bin_cards_available"))
        {
            bbinCardsAvailable= true;
        }

        if (qName.equals("bin_cards_kept_with_products"))
        {
            bbinCardsKeptWithProducts= true;
        }

        if (qName.equals("ending_balances_equal_to_stocks"))
        {
            bendingBalancesEqualToStocks= true;
        }

        if (qName.equals("losses_adjustments_correctly_filled"))
        {
            blossesAdjustmentsCorrectlyFilled= true;
        }

        if (qName.equals("ledgers_bin_cards_filled_correctly"))
        {
            bledgersBinCardsFilledCorrectly= true;
        }

        if (qName.equals("physical_stock_counts_exercises_conducted"))
        {
            bphysicalStockCountsExercisesConducted= true;
        }

        if (qName.equals("ddr_available"))
        {
            bddrAvailable= true;
        }

        if (qName.equals("invoices_kept_in_file"))
        {
            binvoicesKeptInFile= true;
        }

        if (qName.equals("last_supervision_visit_in_file"))
        {
            blastSupervisionVisitInFile= true;
        }

        // r and r

        if (qName.equals("randr_available"))
        {
            brandrAvailable= true;
        }

        if (qName.equals("opening_ending_balances_equal"))
        {
            bopeningEndingBalancesEqual= true;
        }

        if (qName.equals("ending_balance_corresponds_to_ledger"))
        {
            bendingBalancesEqualToStocks= true;
        }

        if (qName.equals("consumption_estimation_correctly_filled"))
        {
            bconsumptionEstimationCorrectlyFilled= true;
        }

        if (qName.equals("stock_out_adjustment_correct"))
        {
            bstockOutAdjustmentCorrect= true;
        }

        if (qName.equals("quantity_required_correctly_filled"))
        {
            bquantityRequiredCorrectlyFilled= true;
        }

        if (qName.equals("column_of_costs_filled_correctly"))
        {
            bcolumnOfCostsFilledCorrectly= true;
        }

        if (qName.equals("randr_forms_filled"))
        {
            brandrFormsFilled= true;
        }

        // GPS and pictures

        if (qName.equals("facility_gps_location"))
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

        if (qName.equals("first_picture"))
        {
            bfirstPicture = true;
        }

        if (qName.equals("second_picture"))
        {
            bsecondPicture = true;
        }

        if (qName.equals("third_picture"))
        {
            bthirdPicture = true;
        }

        if (qName.equals("deviceID"))
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

        if (qName.equals("meta"))
        {
            bmeta = false;
        }

        if (qName.equals("instanceID"))
        {
            binstanceID = false;
        }

        if (qName.equals("country"))
        {
            bcountry = false;
        }

        if (qName.equals("zone")) {
            bzone = false;
        }

        if (qName.equals("region"))
        {
            bregion = false;
        }

        if (qName.equals("district"))
        {
            bdistrict = false;
        }

        if (qName.equals("facility"))
        {
            bfacility = false;

        }

        // storage

        if (qName.equals("a_storage")) {
            bastorage = false;
        }

        if (qName.equals("adequate_storage_space"))
        {
            badequateStorageSpace = false;
        }

        if (qName.equals("adequate_shelves"))
        {
            badequateShelves = false;
        }

        if (qName.equals("store_room_clean"))
        {
            bstoreRoomClean = false;
        }

        if (qName.equals("products_arranged_appropriately"))
        {
            bproductsArrangedAppropriately = false;
        }

        if (qName.equals("products_stored_issued"))
        {
            bproductsStoredIssued = false;
        }

        if (qName.equals("medicines_stored_separately"))
        {
            bmedicinesStoredSeparately = false;
        }

        if (qName.equals("cold_chain_followed"))
        {
            bcoldChainFollowed = false;
        }

        if (qName.equals("bproductsFreeFromDusts"))
        {
            bproductsFreeFromDusts = false;
        }

        if (qName.equals("products_free_from_moisture"))
        {
            bproductsFreeFromMoisture = false;
        }

        if (qName.equals("products_free_from_sunlight"))
        {
            bproductsFreeFromSunlight = false;
        }

        if (qName.equals("store_room_prevented_from_infestation"))
        {
            bstoreRoomPreventedFromInfestation = false;
        }

        if (qName.equals("adequate_security"))
        {
            badequateSecurity = false;
        }

        if (qName.equals("fire_extinguisher_available"))
        {
            bfireExtinguisherAvailable = false;
        }

        if (qName.equals("store_room_condition_conductive"))
        {
            bstoreRoomConditionConductive = false;
        }

        if (qName.equals("control_for_unauthorized_personnel"))
        {
            bcontrolForUnauthorizedPersonnel = false;
        }


        // lmis tools
        if (qName.equals("store_ledger_available"))
        {
            bstoreLedgerAvailable= false;
        }

        if (qName.equals("store_ledgers_in_store_room"))
        {
            bstoreLedgersInStoreRoom= false;
        }

        if (qName.equals("bin_cards_available"))
        {
            bbinCardsAvailable= false;
        }

        if (qName.equals("bin_cards_kept_with_products"))
        {
            bbinCardsKeptWithProducts= false;
        }

        if (qName.equals("ending_balances_equal_to_stocks"))
        {
            bendingBalancesEqualToStocks= false;
        }

        if (qName.equals("losses_adjustments_correctly_filled"))
        {
            blossesAdjustmentsCorrectlyFilled= false;
        }

        if (qName.equals("ledgers_bin_cards_filled_correctly"))
        {
            bledgersBinCardsFilledCorrectly= false;
        }

        if (qName.equals("physical_stock_counts_exercises_conducted"))
        {
            bphysicalStockCountsExercisesConducted= false;
        }

        if (qName.equals("ddr_available"))
        {
            bddrAvailable= false;
        }

        if (qName.equals("invoices_kept_in_file"))
        {
            binvoicesKeptInFile= false;
        }

        if (qName.equals("last_supervision_visit_in_file"))
        {
            blastSupervisionVisitInFile= false;
        }

        // r and r

        if (qName.equals("randr_available"))
        {
            brandrAvailable= false;
        }

        if (qName.equals("opening_ending_balances_equal"))
        {
            bopeningEndingBalancesEqual= false;
        }

        if (qName.equals("ending_balance_corresponds_to_ledger"))
        {
            bendingBalancesEqualToStocks= false;
        }

        if (qName.equals("consumption_estimation_correctly_filled"))
        {
            bconsumptionEstimationCorrectlyFilled= false;
        }

        if (qName.equals("stock_out_adjustment_correct"))
        {
            bstockOutAdjustmentCorrect= false;
        }

        if (qName.equals("quantity_required_correctly_filled"))
        {
            bquantityRequiredCorrectlyFilled= false;
        }

        if (qName.equals("column_of_costs_filled_correctly"))
        {
            bcolumnOfCostsFilledCorrectly= false;
        }

        if (qName.equals("randr_forms_filled"))
        {
            brandrFormsFilled= false;
        }


        // GPS and pictures
        if (qName.equals("facility_gps_location"))
        {
            bfacilityGPSLocation = false;
        }

        if (qName.equals("Latitude"))
        {
            blatitude = false;
        }

        if (qName.equals("Longitude"))
        {
            blongitude = false;
        }

        if (qName.equals("Altitude"))
        {
            baltitude = false;
        }

        if (qName.equals("Accuracy"))
        {
            baccuracy = false;

        }

        if (qName.equals("first_picture"))
        {
            bfirstPicture = false;
        }

        if (qName.equals("second_picture"))
        {
            bsecondPicture = false;
        }

        if (qName.equals("third_picture"))
        {
            bthirdPicture = false;
        }






        if (qName.equals("deviceID"))
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


        else if (bfacility) {
           tempString = new String(ch, start, length);
           odkStorageSurveySubmission = new ODKStorageSurveySubmission();
           odkStorageSurveySubmission.setFacilityId(Long.parseLong(tempString));
            bfacility = false;
                    }

        // storage

        else if (bastorage) {
            bastorage = false;
        }

        else if (badequateStorageSpace)
        {
            tempString = new String(ch, start, length);
            if (Integer.parseInt(tempString) == 1) {
                storageTotalPercentage ++;
            }
            odkStorageSurveySubmission.setAdequateStorageSpace(Integer.parseInt(tempString));
            badequateStorageSpace = false;
        }

        else if (badequateShelves)
        {
            tempString =  new String(ch, start, length);
            if (Integer.parseInt(tempString) == 1) {
                storageTotalPercentage ++;
            }
            odkStorageSurveySubmission.setAdequateShelves(Integer.parseInt(tempString));
            badequateShelves = false;
        }

        else if (bstoreRoomClean)
        {
            tempString =  new String(ch, start, length);
            if (Integer.parseInt(tempString) == 1) {
                storageTotalPercentage ++;
            }
            odkStorageSurveySubmission.setStoreRoomClean(Integer.parseInt(tempString));
            bstoreRoomClean = false;
        }

        else if (bproductsArrangedAppropriately)
        {
            tempString =  new String(ch, start, length);
            if (Integer.parseInt(tempString) == 1) {
                storageTotalPercentage ++;
            }
            odkStorageSurveySubmission.setProductsArrangedAppropriately(Integer.parseInt(tempString));
            bproductsArrangedAppropriately = false;
        }

        else if (bproductsStoredIssued)
        {
            tempString =  new String(ch, start, length);
            if (Integer.parseInt(tempString) == 1) {
                storageTotalPercentage ++;
            }
            odkStorageSurveySubmission.setProductsStoredIssued(Integer.parseInt(tempString));
            bproductsStoredIssued = false;
        }

        else if (bmedicinesStoredSeparately)
        {
            tempString =  new String(ch, start, length);
            odkStorageSurveySubmission.setMedicinesStoredSeparately(Integer.parseInt(tempString));
            bmedicinesStoredSeparately = false;
        }

        else if (bcoldChainFollowed)
        {
            tempString =  new String(ch, start, length);
            odkStorageSurveySubmission.setColdChainFollowed(Integer.parseInt(tempString));
            bcoldChainFollowed = false;
        }

        else if (bproductsFreeFromDusts)
        {
            tempString =  new String(ch, start, length);
            odkStorageSurveySubmission.setProductsFreeFromDusts(Integer.parseInt(tempString));
            bproductsFreeFromDusts = false;
        }

        else if (bproductsFreeFromMoisture)
        {
            tempString =  new String(ch, start, length);
            odkStorageSurveySubmission.setProductsFreeFromMoisture(Integer.parseInt(tempString));
            bproductsFreeFromMoisture = false;
        }

        else if (bproductsFreeFromSunlight)
        {
            tempString =  new String(ch, start, length);
            odkStorageSurveySubmission.setProductsFreeFromSunlight(Integer.parseInt(tempString));
            bproductsFreeFromSunlight = false;
        }

        else if (bstoreRoomPreventedFromInfestation)
        {
            tempString =  new String(ch, start, length);
            odkStorageSurveySubmission.setStoreRoomPreventedFromInfestation(Integer.parseInt(tempString));
            bstoreRoomPreventedFromInfestation = false;
        }

        else if (badequateSecurity)
        {
            tempString =  new String(ch, start, length);
            odkStorageSurveySubmission.setAdequateSecurity(Integer.parseInt(tempString));
            badequateSecurity = false;
        }

        else if (bfireExtinguisherAvailable)
        {
            tempString =  new String(ch, start, length);
            odkStorageSurveySubmission.setFireExtinguisherAvailable(Integer.parseInt(tempString));
            bfireExtinguisherAvailable = false;
        }

        else if (bstoreRoomConditionConductive)
        {
            tempString =  new String(ch, start, length);
            odkStorageSurveySubmission.setStoreRoomConditionConductive(Integer.parseInt(tempString));
            bstoreRoomConditionConductive = false;
        }

        else if (bcontrolForUnauthorizedPersonnel)
        {
            tempString =  new String(ch, start, length);
            odkStorageSurveySubmission.setControlForUnauthorizedPersonnel(Integer.parseInt(tempString));
            bcontrolForUnauthorizedPersonnel = false;
        }

        // lmis tools

        else if (bstoreLedgerAvailable)
        {
            odkLmisSurveySubmission = new ODKLMISSurveySubmission();
            tempString =  new String(ch, start, length);
            odkLmisSurveySubmission.setStoreLedgerAvailable(Integer.parseInt(tempString));
            bstoreLedgerAvailable= false;
        }

        else if (bstoreLedgersInStoreRoom)
        {
            tempString =  new String(ch, start, length);
            odkLmisSurveySubmission.setStoreLedgersInStoreRoom(Integer.parseInt(tempString));
            bstoreLedgersInStoreRoom= false;
        }

        else if (bbinCardsAvailable)
        {
            tempString =  new String(ch, start, length);
            odkLmisSurveySubmission.setBinCardsAvailable(Integer.parseInt(tempString));
            bbinCardsAvailable= false;
        }

        else if (bbinCardsKeptWithProducts)
        {
            tempString =  new String(ch, start, length);
            odkLmisSurveySubmission.setBinCardsKeptWithProducts(Integer.parseInt(tempString));
            bbinCardsKeptWithProducts= false;
        }

        else if (bendingBalancesEqualToStocks)
        {
            tempString =  new String(ch, start, length);
            odkLmisSurveySubmission.setEndingBalancesEqualToStocks(Integer.parseInt(tempString));
            bendingBalancesEqualToStocks= false;
        }

        else if (blossesAdjustmentsCorrectlyFilled)
        {
            tempString =  new String(ch, start, length);
            odkLmisSurveySubmission.setLossesAdjustmentsCorrectlyFilled(Integer.parseInt(tempString));
            blossesAdjustmentsCorrectlyFilled= false;
        }

        else if (bledgersBinCardsFilledCorrectly)
        {
            tempString =  new String(ch, start, length);
            odkLmisSurveySubmission.setLedgersBinCardsFilledCorrectly(Integer.parseInt(tempString));
            bledgersBinCardsFilledCorrectly= false;
        }

        else if (bphysicalStockCountsExercisesConducted)
        {
            tempString =  new String(ch, start, length);
            odkLmisSurveySubmission.setPhysicalStockCountsExercisesConducted(Integer.parseInt(tempString));
            bphysicalStockCountsExercisesConducted= false;
        }

        else if (bddrAvailable)
        {
            tempString =  new String(ch, start, length);
            odkLmisSurveySubmission.setDdrAvailable(Integer.parseInt(tempString));
            bddrAvailable= false;
        }

        else if (binvoicesKeptInFile)
        {
            tempString =  new String(ch, start, length);
            odkLmisSurveySubmission.setInvoicesKeptInFile(Integer.parseInt(tempString));
            binvoicesKeptInFile= false;
        }

        else if (blastSupervisionVisitInFile)
        {
            tempString =  new String(ch, start, length);
            odkLmisSurveySubmission.setLastSupervisionVisitInFile(Integer.parseInt(tempString));
            blastSupervisionVisitInFile= false;
        }

        // r and r

        else if (brandrAvailable)
        {
            odkRandRSubmission = new ODKRandRSubmission();
            tempString =  new String(ch, start, length);
            odkRandRSubmission.setRandrAvailable(Integer.parseInt(tempString));
            brandrAvailable= false;
        }

        else if (bopeningEndingBalancesEqual)
        {
            tempString =  new String(ch, start, length);
            odkRandRSubmission.setOpeningEndingBalancesEqual(Integer.parseInt(tempString));
            bopeningEndingBalancesEqual= false;
        }

        else if (bendingBalancesEqualToStocks)
        {
            tempString =  new String(ch, start, length);
            odkRandRSubmission.setEndingBalanceCorrespondsToLedger(Integer.parseInt(tempString));
            bendingBalancesEqualToStocks= false;
        }

        else if (bconsumptionEstimationCorrectlyFilled)
        {
            tempString =  new String(ch, start, length);
            odkRandRSubmission.setConsumptionEstimationCorrectlyFilled(Integer.parseInt(tempString));
            bconsumptionEstimationCorrectlyFilled= false;
        }

        else if (bstockOutAdjustmentCorrect)
        {
            tempString =  new String(ch, start, length);
            odkRandRSubmission.setStockOutAdjustmentCorrect(Integer.parseInt(tempString));
            bstockOutAdjustmentCorrect= false;
        }

        else if (bquantityRequiredCorrectlyFilled)
        {
            tempString =  new String(ch, start, length);
            odkRandRSubmission.setStockOutAdjustmentCorrect(Integer.parseInt(tempString));
            bquantityRequiredCorrectlyFilled= false;
        }

        else if (bcolumnOfCostsFilledCorrectly)
        {
            tempString =  new String(ch, start, length);
            odkRandRSubmission.setColumnOfCostsFilledCorrectly(Integer.parseInt(tempString));
            bcolumnOfCostsFilledCorrectly= false;
        }

        else if (brandrFormsFilled)
        {
            tempString =  new String(ch, start, length);
            odkRandRSubmission.setRandrFormsFilled(Integer.parseInt(tempString));
            brandrFormsFilled= false;
        }

        // GPS and pictures

        else if (bfacilityGPSLocation)
        {
            // format : 8.9602334 38.7691866 0.0 2828.0
            String temp = new String(ch, start, length);
            String[] vals = temp.split(" ");
            if (vals.length > 0) {
                odkStorageSurveySubmission.setGPSLatitude(Double.parseDouble(vals[0]));
                odkStorageSurveySubmission.setGPSLongitude(Double.parseDouble(vals[1]));
                odkStorageSurveySubmission.setGPSAltitude(Double.parseDouble(vals[2]));
                odkStorageSurveySubmission.setGPSAccuracy(Double.parseDouble(vals[3]));
            }
            bfacilityGPSLocation = false;
        }

        else if (blatitude)
        {
            // odkStorageSurveySubmission.setGPSLatitude(Double.parseDouble(new String(ch, start, length)));
            blatitude = false;
        }
        else if (blongitude)
        {
            // odkStorageSurveySubmission.setGPSLongitude(Double.parseDouble(new String(ch, start, length)));
            blongitude = false;
        }
        else if (baltitude)
        {
            // odkStorageSurveySubmission.setGPSAltitude(Double.parseDouble(new String(ch, start, length)));
            baltitude = false;
        }

        else if (baccuracy)
        {
            // odkStorageSurveySubmission.setGPSAccuracy(Double.parseDouble(new String(ch, start, length)));
            baccuracy = false;
        }

        else if (bfirstPicture)
        {
            pictures.add(new String(ch, start, length));
            bfirstPicture = false;
        }

        else if (bsecondPicture)
        {
            pictures.add(new String(ch, start, length));
            bsecondPicture = false;
        }

        else if (bthirdPicture)
        {
            pictures.add(new String(ch, start, length));
            bthirdPicture = false;
        }





        else if (bdeviceID)
        {
            odkAccount = new ODKAccount();
            odkAccount.setDeviceId(new String(ch, start, length));
            bdeviceID = false;
        }





        else
        {

        }
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

    public ODKLMISSurveySubmission getOdkLmisSurveySubmission() {
        return odkLmisSurveySubmission;
    }

    public void setOdkLmisSurveySubmission(ODKLMISSurveySubmission odkLmisSurveySubmission) {
        this.odkLmisSurveySubmission = odkLmisSurveySubmission;
    }

    public ODKRandRSubmission getOdkRandRSubmission() {
        return odkRandRSubmission;
    }

    public void setOdkRandRSubmission(ODKRandRSubmission odkRandRSubmission) {
        this.odkRandRSubmission = odkRandRSubmission;
    }

    public ODKStorageSurveySubmission getOdkStorageSurveySubmission() {
        return odkStorageSurveySubmission;
    }

    public void setOdkStorageSurveySubmission(ODKStorageSurveySubmission odkStorageSurveySubmission) {
        this.odkStorageSurveySubmission = odkStorageSurveySubmission;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }



}
