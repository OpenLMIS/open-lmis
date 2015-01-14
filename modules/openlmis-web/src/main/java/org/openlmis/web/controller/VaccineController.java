/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.vaccine.domain.*;
import org.openlmis.vaccine.service.*;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller handles endpoints to Vaccine related features
 */
@Controller
@NoArgsConstructor
@RequestMapping(value="/vaccine")
public class VaccineController extends BaseController {

   @Autowired
   private VaccineTargetService vaccineTargetService;

    @Autowired
    private ManufacturerService manufacturerService;

    @Autowired
    private VaccineDistributionBatchService distributionBatchService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private VaccineDashboardReportService vaccineReportService;

    @RequestMapping(value = "/target/create", method = POST, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE_TARGETS')")
    public ResponseEntity insert(@RequestBody VaccineTarget vaccineTarget, HttpServletRequest request) {

        vaccineTarget.setCreatedBy(loggedInUserId(request));
        vaccineTarget.setModifiedBy(loggedInUserId(request));

        ResponseEntity<OpenLmisResponse> response;

        try {
            vaccineTargetService.updateVaccineTarget(vaccineTarget);
        } catch(DuplicateKeyException exp){
        return OpenLmisResponse.error("There is a record with the same geographic zone and target year.", HttpStatus.BAD_REQUEST);
      }

        response = success(messageService.message("message.vaccine.target.created.success"));
        response.getBody().addData("vaccineTarget", vaccineTarget);
        return response;
    }

    @RequestMapping(value = "/target/delete/{id}", method = DELETE, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE_TARGETS')")
    public ResponseEntity deleteVaccineTarget(@PathVariable(value="id") Long id){

        vaccineTargetService.deleteVaccineTarget(id);

        ResponseEntity<OpenLmisResponse> response;
        response = success(messageService.message("message.vaccine.target.created.success"));
        return response;
    }

    @RequestMapping(value = "/target/list", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineTargets() {

        return OpenLmisResponse.response("vaccineTargets", vaccineTargetService.getVaccineTargets());
    }

    @RequestMapping(value = "/target/get/{id}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineTarget(@PathVariable(value="id") Long id){
        return OpenLmisResponse.response("vaccineTarget", vaccineTargetService.getVaccineTarget(id));
    }

    @RequestMapping(value = "/usable-batches/product/{productId}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getUsableBatches(@PathVariable("productId") Long productId){
        return OpenLmisResponse.response("usableBatches", distributionBatchService.getUsableBatches(productId));
    }

    @RequestMapping(value = "/receive-vaccine", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> searchReceivedVaccine(@RequestParam(required = true) Long facilityId) {
        return OpenLmisResponse.response("receivedVaccines", distributionBatchService.getReceivedVaccinesForFacility(facilityId));
    }

    @RequestMapping(value = "/receive-vaccine/{id}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getReceivedVaccineById(@PathVariable("id") Long id) {
        return OpenLmisResponse.response("receivedVaccine", distributionBatchService.getReceivedVaccinesById(id));
    }

    @RequestMapping(value = "/receive-vaccine", method = POST, headers = ACCEPT_JSON)
    //@PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_VACCINE_DISTRIBUTION_BATCH')")
    public ResponseEntity receiveVaccine(@RequestBody InventoryTransaction inventoryTransaction, HttpServletRequest request){

        inventoryTransaction.setCreatedBy(loggedInUserId(request));
        inventoryTransaction.setModifiedBy(loggedInUserId(request));

        //ResponseEntity<OpenLmisResponse> response;
        try {
            distributionBatchService.receiveVaccine(inventoryTransaction);

        }catch (DataException exception) {
            OpenLmisResponse openLmisResponse = new OpenLmisResponse("receiveVaccine", inventoryTransaction);
            return openLmisResponse.errorEntity(exception, BAD_REQUEST);
        }
       /* response = success(messageService.message("Vaccine received successfully"));

        response.getBody().addData("receiveVaccine", inventoryTransaction);
        return response;*/

        return success("Vaccine received successfully");

        /*String successMessage = messageService.message("Vaccine received successfully");
        OpenLmisResponse openLmisResponse = new OpenLmisResponse("receiveVaccine", inventoryTransaction);
        return openLmisResponse.successEntity(successMessage);*/
    }

    @RequestMapping(value = "/receive-vaccine/{id}", method = PUT, headers = ACCEPT_JSON)
    //@PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE_DISTRIBUTION_BATCH')")
    public ResponseEntity<OpenLmisResponse> update(@PathVariable("id") Long id, @RequestBody InventoryTransaction inventoryTransaction, HttpServletRequest request) {
        inventoryTransaction.setCreatedBy(loggedInUserId(request));
        inventoryTransaction.setModifiedBy(loggedInUserId(request));

        try {
            distributionBatchService.receiveVaccine(inventoryTransaction);

        }catch (DataException exception) {
            OpenLmisResponse openLmisResponse = new OpenLmisResponse("receiveVaccine", inventoryTransaction);
            return openLmisResponse.errorEntity(exception, BAD_REQUEST);
        }

        return success("Inventory transaction updated successfully");
       /* String successMessage = messageService.message("Inventory transaction updated successfully");
        OpenLmisResponse openLmisResponse = new OpenLmisResponse("receiveVaccine", inventoryTransaction);
        return openLmisResponse.successEntity(successMessage);*/
    }

    @RequestMapping(value = "/distribute-vaccine", method = POST, headers = ACCEPT_JSON)
    //@PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_VACCINE_DISTRIBUTION_BATCH')")
    public ResponseEntity distributeVaccine(@RequestBody InventoryTransaction inventoryTransaction, HttpServletRequest request){

        inventoryTransaction.setCreatedBy(loggedInUserId(request));
        inventoryTransaction.setModifiedBy(loggedInUserId(request));

        ResponseEntity<OpenLmisResponse> response;

        try {

            distributionBatchService.distributeVaccine(inventoryTransaction);

        }catch (DataException exception) {
            OpenLmisResponse openLmisResponse = new OpenLmisResponse("distributeVaccine", inventoryTransaction);
            return openLmisResponse.errorEntity(exception, BAD_REQUEST);
        }
        response = success(messageService.message("Vaccine distributed successfully"));
        response.getBody().addData("distributeVaccine", inventoryTransaction);
        return response;
    }


    @RequestMapping(value = "/geographic-zone-facility/tree", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getGeoZoneFacilityTree(HttpServletRequest request){
        return OpenLmisResponse.response("geoZoneFacilities",vaccineReportService.getGeoZoneTree(loggedInUserId(request)));
    }
    @RequestMapping(value = "/manufacturers", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getManufacturers(){
        return OpenLmisResponse.response("manufacturers", manufacturerService.getAll());
    }

    @RequestMapping(value = "/status", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getVaccineDistributionStatus(){
        return OpenLmisResponse.response("status", statusService.getAll());
    }


}
