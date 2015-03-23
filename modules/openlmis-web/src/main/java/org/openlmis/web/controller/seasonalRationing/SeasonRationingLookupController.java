
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.web.controller.seasonalRationing;

import org.openlmis.core.domain.OrderQuantityAdjustmentFactor;
import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.OrderQuantityAdjustmentFactorService;
import org.openlmis.core.service.OrderQuantityAdjustmentTypeService;
import org.openlmis.vaccine.domain.Countries;
import org.openlmis.web.controller.BaseController;
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

@Controller
@RequestMapping(value="/season-rationing")
public class SeasonRationingLookupController extends BaseController {
    public static final String SEASONALRATIONINGTYPELIST = "seasonalityRationingsList";
    public static final String SEASONALRATIONINGTYPE="seasonalityRationingType";
    public static final String ADJUSTMENTFACTOR = "adjustmentFactor";
    public static final String ADJUSTMENTFACTORLIST="adjustmentFactorList";

    @Autowired
    private OrderQuantityAdjustmentTypeService quantityAdjustmentTypeService;
    @Autowired
    private OrderQuantityAdjustmentFactorService adjustmentFactorService;
    private ResponseEntity<OpenLmisResponse> saveSeasonalityRationingType(OrderQuantityAdjustmentType quantityAdjustmentType, boolean createOperation) {
        try {
            if (createOperation) {
                System.out.println("creating "+quantityAdjustmentType.getName());
                this.quantityAdjustmentTypeService.addOrderQuantityAdjustmentType(quantityAdjustmentType);
            } else {
                this.quantityAdjustmentTypeService.updateOrderQuantityAdjustmentType(quantityAdjustmentType);
            }
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + quantityAdjustmentType.getId()) + "' " + (createOperation ? "created" : "updated") + " successfully");
            response.getBody().addData(SEASONALRATIONINGTYPE, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentType(quantityAdjustmentType.getId()));
            response.getBody().addData(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());
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

    @RequestMapping(value = "/seasonalityRationingTypes/{id}", method = RequestMethod.GET)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getCountriesDetail1(@PathVariable("id") Long id) {
        //System.out.println(" here calling");
        OrderQuantityAdjustmentType adjustmentType = this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentType(id);
        return OpenLmisResponse.response(   SEASONALRATIONINGTYPE, adjustmentType);
    }



    @RequestMapping(value = "/seasonalityRationingTypes/{id}", method = PUT, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> updateCountries(@RequestBody OrderQuantityAdjustmentType quantityAdjustmentType, HttpServletRequest request) {
        //System.out.println(" updating ");
        quantityAdjustmentType.setModifiedBy(loggedInUserId(request));
        quantityAdjustmentType.setModifiedDate(new Date());

        //System.out.println(" help topic id is" + helpTopic.getName());
        return saveSeasonalityRationingType(quantityAdjustmentType, false);
    }
    //////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/seasonalityRationingTypes", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> createSeasonalityRationingType(@RequestBody OrderQuantityAdjustmentType quantityAdjustmentType, HttpServletRequest request) {
        //System.out.println(" here saving help Content");
        quantityAdjustmentType.setCreatedBy(loggedInUserId(request));
        quantityAdjustmentType.setModifiedBy(loggedInUserId(request));
        quantityAdjustmentType.setModifiedDate(new Date());
        quantityAdjustmentType.setCreatedDate(new Date());
        //System.out.println(" help content id is " + helpContent.getName());


        return saveSeasonalityRationingType(quantityAdjustmentType, true);
    }


    @RequestMapping(value = "/seasonalityRationingTypes/{id}", method = DELETE, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> removeCountries(@RequestBody OrderQuantityAdjustmentType quantityAdjustmentType, HttpServletRequest request) {
        System.out.println(" here deleting "+ quantityAdjustmentType.getName());
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + quantityAdjustmentType.getId()) + "Deleted successfully");
        response.getBody().addData(SEASONALRATIONINGTYPE, quantityAdjustmentType);

        System.out.println(" here deleting "+ quantityAdjustmentType.getName());
        this.quantityAdjustmentTypeService.deleteOrderQuantityAdjustmentType(quantityAdjustmentType);
        response.getBody().addData(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());
        return response;
    }
    //    @RequestMapping(value = "/storageTypes", method = RequestMethod.GET, headers = "Accept=application/json")
////    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
//    public ResponseEntity<OpenLmisResponse> getAllStorageTypeList() {
//        //System.out.println(" here calling");
//        return OpenLmisResponse.response(STORAGETYPELIST, this.storageTypeService.loadStorageTypeList());
//    }
    @RequestMapping(value = "/seasonalityRationingTypes", method = RequestMethod.GET)
    //    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public  ResponseEntity<OpenLmisResponse>  searchCountries(@RequestParam(required = true) String param) {
        return OpenLmisResponse.response(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.searchForQuantityAdjustmentType(param));
    }
    @RequestMapping(value = "/seasonalityRationingTypes_remove", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> deleteSeasonalRationingType(@RequestBody OrderQuantityAdjustmentType quantityAdjustmentType, HttpServletRequest request) {
        System.out.println(" here deleting "+ quantityAdjustmentType.getName());
        this.quantityAdjustmentTypeService.deleteOrderQuantityAdjustmentType(quantityAdjustmentType);
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + quantityAdjustmentType.getId()) + "Deleted successfully");
        response.getBody().addData(SEASONALRATIONINGTYPE, quantityAdjustmentType);
        response.getBody().addData(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());

        return response;
    }
    @RequestMapping(value = "/seasonalityRationingTypeList", method = RequestMethod.GET)
    //    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public  ResponseEntity<OpenLmisResponse>  loadAllCountries() {
        return OpenLmisResponse.response(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());
    }
    /*
    orderQuanitityAdjustmentFactory
     */

    private ResponseEntity<OpenLmisResponse> saveAdjustmentFactor(OrderQuantityAdjustmentFactor adjustmentFactor, boolean createOperation) {
        try {
            if (createOperation) {
                System.out.println("creating "+adjustmentFactor.getName());
                this.adjustmentFactorService.addOrderQuantityAdjustmentFactor(adjustmentFactor);
            } else {
                this.adjustmentFactorService.updateOrderQuantityAdjustmentFactor(adjustmentFactor);
            }
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + adjustmentFactor.getId()) + "' " + (createOperation ? "created" : "updated") + " successfully");
            response.getBody().addData(ADJUSTMENTFACTOR, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentType(adjustmentFactor.getId()));
            response.getBody().addData(ADJUSTMENTFACTORLIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());
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

    @RequestMapping(value = "/adjustmentFactors/{id}", method = RequestMethod.GET)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getAdjustmentFactorDetail1(@PathVariable("id") Long id) {
        //System.out.println(" here calling");
        OrderQuantityAdjustmentFactor adjustmentFactor = this.adjustmentFactorService.loadOrderQuantityAdjustmentFactorDetail(id);
        return OpenLmisResponse.response(   ADJUSTMENTFACTOR, adjustmentFactor);
    }



    @RequestMapping(value = "/adjustmentFactors/{id}", method = PUT, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> updateAdjustmentFactor(@RequestBody OrderQuantityAdjustmentFactor adjustmentFactor, HttpServletRequest request) {
        //System.out.println(" updating ");
        adjustmentFactor.setModifiedBy(loggedInUserId(request));
        adjustmentFactor.setModifiedDate(new Date());

        //System.out.println(" help topic id is" + helpTopic.getName());
        return saveAdjustmentFactor(adjustmentFactor, false);
    }
    //////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/adjustmentFactors", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> createAdjustmentFactor(@RequestBody OrderQuantityAdjustmentFactor adjustmentFactor, HttpServletRequest request) {
        //System.out.println(" here saving help Content");
        adjustmentFactor.setCreatedBy(loggedInUserId(request));
        adjustmentFactor.setModifiedBy(loggedInUserId(request));
        adjustmentFactor.setModifiedDate(new Date());
        adjustmentFactor.setCreatedDate(new Date());
        //System.out.println(" help content id is " + helpContent.getName());


        return saveAdjustmentFactor(adjustmentFactor, true);
    }


    @RequestMapping(value = "/adjustmentFactors/{id}", method = DELETE, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> removeAdjustmentFactor(@RequestBody OrderQuantityAdjustmentFactor adjustmentFactor, HttpServletRequest request) {
        System.out.println(" here deleting "+ adjustmentFactor.getName());
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + adjustmentFactor.getId()) + "Deleted successfully");
        response.getBody().addData(ADJUSTMENTFACTOR, adjustmentFactor);

        System.out.println(" here deleting "+ adjustmentFactor.getName());
        this.adjustmentFactorService.deleteOrderQuantityAdjustmentFactor(adjustmentFactor);
        response.getBody().addData(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());
        return response;
    }
    //    @RequestMapping(value = "/storageTypes", method = RequestMethod.GET, headers = "Accept=application/json")
////    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
//    public ResponseEntity<OpenLmisResponse> getAllStorageTypeList() {
//        //System.out.println(" here calling");
//        return OpenLmisResponse.response(STORAGETYPELIST, this.storageTypeService.loadStorageTypeList());
//    }
    @RequestMapping(value = "/adjustmentFactors", method = RequestMethod.GET)
    //    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public  ResponseEntity<OpenLmisResponse>  searchAdjustmentFactorList(@RequestParam(required = true) String param) {
        return OpenLmisResponse.response(ADJUSTMENTFACTORLIST, this.adjustmentFactorService.searchAdjustmentFactor(param));
    }
    @RequestMapping(value = "/adjustmentFactors_remove", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> deleteAdjustmentFactor(@RequestBody OrderQuantityAdjustmentFactor adjustmentFactor, HttpServletRequest request) {
        System.out.println(" here deleting "+ adjustmentFactor.getName());
        this.adjustmentFactorService.deleteOrderQuantityAdjustmentFactor(adjustmentFactor);
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + adjustmentFactor.getId()) + "Deleted successfully");
        response.getBody().addData(ADJUSTMENTFACTOR, adjustmentFactor);
        response.getBody().addData(ADJUSTMENTFACTORLIST, this.adjustmentFactorService.loadOrderQuantityAdjustmentFactor());

        return response;
    }
    @RequestMapping(value = "/adjustmentFactorList", method = RequestMethod.GET)
    //    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public  ResponseEntity<OpenLmisResponse>  loadAllAdjustmentFactories() {
        return OpenLmisResponse.response(ADJUSTMENTFACTORLIST, this.adjustmentFactorService.loadOrderQuantityAdjustmentFactor());
    }
}
