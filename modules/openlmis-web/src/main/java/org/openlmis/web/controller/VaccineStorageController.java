package org.openlmis.web.controller;

import org.apache.poi.hssf.record.formula.functions.Count;
import org.openlmis.core.exception.DataException;
import org.openlmis.vaccine.domain.Countries;
import org.openlmis.vaccine.domain.StorageType;
import org.openlmis.vaccine.domain.Temperature;
import org.openlmis.vaccine.domain.VaccineStorage;
import org.openlmis.vaccine.service.CountriesService;
import org.openlmis.vaccine.service.StorageTypeService;
import org.openlmis.vaccine.service.TempratureService;
import org.openlmis.vaccine.service.VaccineStorageService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
@Controller
public class VaccineStorageController extends BaseController  {
    @Autowired
    private VaccineStorageService storageService;
    @Autowired
    private StorageTypeService storageTypeService;
    @Autowired
    private TempratureService tempratureService;
    @Autowired
    private CountriesService countriesService;

    public static final String VACCINESTORAGE = "vaccineStorage";
    public static final String VACCINESTORAGEDATAIL = "vaccineStorage";
    public static final String VACCINESTORAGELIST = "vaccineStorageList";
    public static final String TEMPERATURELIST = "temperatureList";
    public static final String STORAGETYPELIST = "storageTypeList";
    public static final String FACILLYLIST = "facilityList";
    public static final String STORAGETYPE="storageType";
    public static final String TEMPRATURE="temprature";
    public static final String COUNTRIESLIST = "countriesList";
    public static final String COUNTRIES="countries";
    @RequestMapping(value = "/createVaccineStorage", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody VaccineStorage vaccineStorage, HttpServletRequest request) {
        //System.out.println(" here saving help Content");
        vaccineStorage.setCreatedBy(loggedInUserId(request));
        vaccineStorage.setModifiedBy(loggedInUserId(request));
        vaccineStorage.setModifiedDate(new Date());
        vaccineStorage.setCreatedDate(new Date());
        //System.out.println(" help content id is " + helpContent.getName());


        return saveVaccineStorage(vaccineStorage, true);
    }

    private ResponseEntity<OpenLmisResponse> saveVaccineStorage(VaccineStorage vaccineStorage, boolean createOperation) {
        try {
            if (createOperation) {
                System.out.println("creating "+vaccineStorage.getLocation());
                this.storageService.addVaccineStorage(vaccineStorage);
            } else {
                this.storageService.updateVaccineStorage(vaccineStorage);
            }
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + vaccineStorage.getId()) + "' " + (createOperation ? "created" : "updated") + " successfully");
            response.getBody().addData(VACCINESTORAGE, this.storageService.loadVaccineStorageDetail(vaccineStorage.getId()));
            response.getBody().addData(VACCINESTORAGELIST, this.storageService.loadVaccineStorageList());
            return response;
        } catch (DuplicateKeyException exp) {
            System.out.println(exp.getStackTrace());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        } catch (DataException e) {
            System.out.println(e.getStackTrace());
            return error(e, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        }
    }
        /*

         */

    @RequestMapping(value = "/vaccineStorageDetail/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getVaccineStorageDetail(@PathVariable("id") Long id) {
        //System.out.println(" here calling");
        VaccineStorage vaccineStorage = this.storageService.loadVaccineStorageDetail(id);
        return OpenLmisResponse.response(VACCINESTORAGEDATAIL, vaccineStorage);
    }

    @RequestMapping(value = "/vaccine-storage/facility/{facilityId}", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getVaccineStorageByFacilityId(@PathVariable("facilityId") Long facilityId) {

        return OpenLmisResponse.response("vaccineStorageList", this.storageService.getByFacilityId(facilityId));
    }


    @RequestMapping(value = "/vaccineStorageList", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getVaccineStorageList() {
        //System.out.println(" here calling");
        return OpenLmisResponse.response(VACCINESTORAGELIST, this.storageService.loadVaccineStorageList());
    }
    @RequestMapping(value = "/updateVaccineStorage", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> update(@RequestBody VaccineStorage vaccineStorage, HttpServletRequest request) {
        //System.out.println(" updating ");
        vaccineStorage.setModifiedBy(loggedInUserId(request));
        vaccineStorage.setModifiedDate(new Date());

        //System.out.println(" help topic id is" + helpTopic.getName());
        return saveVaccineStorage(vaccineStorage, false);
    }
    @RequestMapping(value = "/deleteVaccineStorage", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> delete(@RequestBody VaccineStorage vaccineStorage, HttpServletRequest request) {

        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + vaccineStorage.getId()) + "Deleted successfully");
        response.getBody().addData(VACCINESTORAGE, vaccineStorage);
        response.getBody().addData(VACCINESTORAGELIST, this.storageService.loadVaccineStorageList());
        this.storageService.deleteVccineStorage(vaccineStorage);
        return response;
    }


    @RequestMapping(value = "/storageTypeList", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getStorageTypeList() {
        //System.out.println(" here calling");
        return OpenLmisResponse.response(STORAGETYPELIST, this.storageTypeService.loadStorageTypeList());
    }

    @RequestMapping(value = "/tempratureList", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getTemperatureList() {

        return OpenLmisResponse.response(TEMPERATURELIST, this.tempratureService.loadTempratureList());
    }
    /* below are methods .. for temperature and storagetypes lookup

     */
    private ResponseEntity<OpenLmisResponse> saveStorageType(StorageType storageType, boolean createOperation) {
        try {
            if (createOperation) {
                System.out.println("creating "+storageType.getStorageTypeName());
                this.storageTypeService.addStorageType(storageType);
            } else {
                this.storageTypeService.updateStorageType(storageType);
            }
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + storageType.getId()) + "' " + (createOperation ? "created" : "updated") + " successfully");
            response.getBody().addData(STORAGETYPE, this.storageTypeService.loadStorageTypeDetail(storageType.getId()));
            response.getBody().addData(STORAGETYPELIST, this.storageTypeService.loadStorageTypeList());
            return response;
        } catch (DuplicateKeyException exp) {
            System.out.println(exp.getStackTrace());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        } catch (DataException e) {
            System.out.println(e.getStackTrace());
            return error(e, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        }
    }
        /*

         */

    @RequestMapping(value = "/storageTypeDetail/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getStorageTypeDetail(@PathVariable("id") Long id) {
        //System.out.println(" here calling");
        StorageType storageType = this.storageTypeService.loadStorageTypeDetail(id);
        return OpenLmisResponse.response(   STORAGETYPE, storageType);
    }



    @RequestMapping(value = "/updateStorageType", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> updateStorageType(@RequestBody StorageType storageType, HttpServletRequest request) {
        //System.out.println(" updating ");
        storageType.setModifiedBy(loggedInUserId(request));
        storageType.setModifiedDate(new Date());

        //System.out.println(" help topic id is" + helpTopic.getName());
        return saveStorageType(storageType, false);
    }
    //////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/createStorageType", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> saveStorageType(@RequestBody StorageType storageType, HttpServletRequest request) {
        //System.out.println(" here saving help Content");
        storageType.setCreatedBy(loggedInUserId(request));
        storageType.setModifiedBy(loggedInUserId(request));
        storageType.setModifiedDate(new Date());
        storageType.setCreatedDate(new Date());
        //System.out.println(" help content id is " + helpContent.getName());


        return saveStorageType(storageType, true);
    }


    @RequestMapping(value = "/deleteStorageType", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> deleteStorageType(@RequestBody StorageType storageType, HttpServletRequest request) {

        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + storageType.getId()) + "Deleted successfully");
        response.getBody().addData(STORAGETYPE, storageType);
        response.getBody().addData(STORAGETYPELIST, this.storageTypeService.loadStorageTypeList());
        this.storageTypeService.removeStorageType(storageType);
        return response;
    }
    /*
    temprature lookup related
     */
    private ResponseEntity<OpenLmisResponse> saveTemprature(Temperature temperature, boolean createOperation) {
        try {
            if (createOperation) {
                System.out.println("creating "+ temperature.getTempratureName());
                this.tempratureService.addTemprature(temperature);
            } else {
                this.tempratureService.updateTemprature(temperature);
            }
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + temperature.getId()) + "' " + (createOperation ? "created" : "updated") + " successfully");
            response.getBody().addData(TEMPRATURE, this.tempratureService.loadTempratureDetail(temperature.getId()));
            response.getBody().addData(TEMPERATURELIST, this.tempratureService.loadTempratureList());
            return response;
        } catch (DuplicateKeyException exp) {
            System.out.println(exp.getStackTrace());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        } catch (DataException e) {
            System.out.println(e.getStackTrace());
            return error(e, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        }
    }
        /*

         */

    @RequestMapping(value = "/tempratureDetail/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getTempratureDetail(@PathVariable("id") Long id) {
        //System.out.println(" here calling");
        Temperature temperature = this.tempratureService.loadTempratureDetail(id);
        return OpenLmisResponse.response(   TEMPRATURE, temperature);
    }



    @RequestMapping(value = "/updateTemprature", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> updateTemprature(@RequestBody Temperature temperature, HttpServletRequest request) {
        //System.out.println(" updating ");
        temperature.setModifiedBy(loggedInUserId(request));
        temperature.setModifiedDate(new Date());

        //System.out.println(" help topic id is" + helpTopic.getName());
        return saveTemprature(temperature, false);
    }
    //////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/createTemprature", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> saveTemprature(@RequestBody Temperature temperature, HttpServletRequest request) {
        //System.out.println(" here saving help Content");
        temperature.setCreatedBy(loggedInUserId(request));
        temperature.setModifiedBy(loggedInUserId(request));
        temperature.setModifiedDate(new Date());
        temperature.setCreatedDate(new Date());
        //System.out.println(" help content id is " + helpContent.getName());


        return saveTemprature(temperature, true);
    }


    @RequestMapping(value = "/deleteTemprature", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> deleteTemprature(@RequestBody Temperature temperature, HttpServletRequest request) {

        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + temperature.getId()) + "Deleted successfully");
        response.getBody().addData(TEMPRATURE, temperature);
        response.getBody().addData(TEMPERATURELIST, this.tempratureService.loadTempratureList());
        this.tempratureService.removeTemprature(temperature);
        return response;
    }
    @RequestMapping(value = "/facilityList", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> loadFacilityList() {
        //System.out.println(" here calling");
        return OpenLmisResponse.response(FACILLYLIST, this.storageService.loadFacillityList());
    }
//    crurd for storage type


        /*

         */

    @RequestMapping(value = "/storageTypes/{id}", method = RequestMethod.GET)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getStorageTypeDetail1(@PathVariable("id") Long id) {
        //System.out.println(" here calling");
        StorageType storageType = this.storageTypeService.loadStorageTypeDetail(id);
        return OpenLmisResponse.response(   STORAGETYPE, storageType);
    }



    @RequestMapping(value = "/storageTypes/{id}", method = PUT, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> updateStorageType1(@RequestBody StorageType storageType, HttpServletRequest request) {
        //System.out.println(" updating ");
        storageType.setModifiedBy(loggedInUserId(request));
        storageType.setModifiedDate(new Date());

        //System.out.println(" help topic id is" + helpTopic.getName());
        return saveStorageType(storageType, false);
    }
    //////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/storageTypes", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> createStorageType(@RequestBody StorageType storageType, HttpServletRequest request) {
        //System.out.println(" here saving help Content");
        storageType.setCreatedBy(loggedInUserId(request));
        storageType.setModifiedBy(loggedInUserId(request));
        storageType.setModifiedDate(new Date());
        storageType.setCreatedDate(new Date());
        //System.out.println(" help content id is " + helpContent.getName());


        return saveStorageType(storageType, true);
    }


    @RequestMapping(value = "/storageTypes/{id}", method = DELETE, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> removeStorageType(@RequestBody StorageType storageType, HttpServletRequest request) {

        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + storageType.getId()) + "Deleted successfully");
        response.getBody().addData(STORAGETYPE, storageType);
        response.getBody().addData(STORAGETYPELIST, this.storageTypeService.loadStorageTypeList());
        this.storageTypeService.removeStorageType(storageType);
        return response;
    }
//    @RequestMapping(value = "/storageTypes", method = RequestMethod.GET, headers = "Accept=application/json")
////    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
//    public ResponseEntity<OpenLmisResponse> getAllStorageTypeList() {
//        //System.out.println(" here calling");
//        return OpenLmisResponse.response(STORAGETYPELIST, this.storageTypeService.loadStorageTypeList());
//    }
    @RequestMapping(value = "/storageTypes", method = RequestMethod.GET)
    //    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public  ResponseEntity<OpenLmisResponse>  searchStorageType(@RequestParam(required = true) String param) {
        return OpenLmisResponse.response(STORAGETYPELIST, this.storageTypeService.searchForStorageTypeList(param));
    }
    @RequestMapping(value = "/tempratures")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> searchTempratureList(@RequestParam(required = true) String param) {

        return OpenLmisResponse.response(TEMPERATURELIST, this.tempratureService.searchForTempratureList(param));
    }
//    end of storrage type crud

//    start of countries crud

     /*

         */
     private ResponseEntity<OpenLmisResponse> saveCountries(Countries countries, boolean createOperation) {
         try {
             if (createOperation) {
                 System.out.println("creating "+countries.getName());
                 this.countriesService.addCountries(countries);
             } else {
                 this.countriesService.updateCountries(countries);
             }
             ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + countries.getId()) + "' " + (createOperation ? "created" : "updated") + " successfully");
             response.getBody().addData(COUNTRIES, this.countriesService.loadCountriesDetail(countries.getId()));
             response.getBody().addData(COUNTRIESLIST, this.countriesService.loadCountriesList());
             return response;
         } catch (DuplicateKeyException exp) {
             System.out.println(exp.getStackTrace());
             return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
         } catch (DataException e) {
             System.out.println(e.getStackTrace());
             return error(e, HttpStatus.BAD_REQUEST);
         } catch (Exception e) {
             System.out.println(e.getMessage());
             return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
         }
     }

    @RequestMapping(value = "/countries/{id}", method = RequestMethod.GET)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getCountriesDetail1(@PathVariable("id") Long id) {
        //System.out.println(" here calling");
        Countries countries = this.countriesService.loadCountriesDetail(id);
        return OpenLmisResponse.response(   COUNTRIES, countries);
    }



    @RequestMapping(value = "/countries/{id}", method = PUT, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> updateCountries(@RequestBody Countries countries, HttpServletRequest request) {
        //System.out.println(" updating ");
        countries.setModifiedBy(loggedInUserId(request));
        countries.setModifiedDate(new Date());

        //System.out.println(" help topic id is" + helpTopic.getName());
        return saveCountries(countries, false);
    }
    //////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/countries", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> createCountries(@RequestBody Countries countries, HttpServletRequest request) {
        //System.out.println(" here saving help Content");
        countries.setCreatedBy(loggedInUserId(request));
        countries.setModifiedBy(loggedInUserId(request));
        countries.setModifiedDate(new Date());
        countries.setCreatedDate(new Date());
        //System.out.println(" help content id is " + helpContent.getName());


        return saveCountries(countries, true);
    }


    @RequestMapping(value = "/countries", method = DELETE, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> removeCountries(@RequestBody Countries countries, HttpServletRequest request) {

        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + countries.getId()) + "Deleted successfully");
        response.getBody().addData(COUNTRIES, countries);
        response.getBody().addData(COUNTRIESLIST, this.countriesService.loadCountriesList());
        System.out.println(" here deleting "+ countries.getName());
        this.countriesService.removeCountries(countries);
        return response;
    }
    //    @RequestMapping(value = "/storageTypes", method = RequestMethod.GET, headers = "Accept=application/json")
////    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
//    public ResponseEntity<OpenLmisResponse> getAllStorageTypeList() {
//        //System.out.println(" here calling");
//        return OpenLmisResponse.response(STORAGETYPELIST, this.storageTypeService.loadStorageTypeList());
//    }
    @RequestMapping(value = "/countries", method = RequestMethod.GET)
    //    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public  ResponseEntity<OpenLmisResponse>  searchCountries(@RequestParam(required = true) String param) {
        return OpenLmisResponse.response(COUNTRIESLIST, this.countriesService.searchForCountries(param));
    }
    @RequestMapping(value = "/countries_remove", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> deleteCountries(@RequestBody Countries countries, HttpServletRequest request) {
        System.out.println(" here deleting "+ countries.getName());
        this.countriesService.removeCountries(countries);
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + countries.getId()) + "Deleted successfully");
        response.getBody().addData(COUNTRIES, countries);
        response.getBody().addData(COUNTRIESLIST, this.countriesService.loadCountriesList());

        return response;
    }
    @RequestMapping(value = "/countriesList", method = RequestMethod.GET)
    //    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public  ResponseEntity<OpenLmisResponse>  loadAllCountries() {
        return OpenLmisResponse.response(COUNTRIESLIST, this.countriesService.loadCountriesList());
    }
    //end of countries crud
    }

