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
import org.openlmis.vaccine.domain.DistributionBatch;
import org.openlmis.vaccine.domain.DistributionType;
import org.openlmis.vaccine.domain.VaccineTarget;
import org.openlmis.vaccine.service.ManufacturerService;
import org.openlmis.vaccine.service.VaccineDistributionBatchService;
import org.openlmis.vaccine.service.VaccineTargetService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping(value = "/target/create", method = POST, headers = ACCEPT_JSON)
    // TODO: Add appropriate permission
    //  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE')")
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
    // TODO: Add appropriate permission
    //  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE')")
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

    @RequestMapping(value = "/distribution-batches/dispatch/{dispatchId}", method = GET, headers = ACCEPT_JSON)
    //@PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_VACCINE_DISTRIBUTION_BATCH')")
    public ResponseEntity<OpenLmisResponse> getDistributionBatchesByBatchNumber(@PathVariable("dispatchId") String dispatchId){
        return OpenLmisResponse.response("distributionBatches", distributionBatchService.getByDispatchId(dispatchId));
    }

    @RequestMapping(value = "/distribution-batches", method = GET, headers = ACCEPT_JSON)
    //@PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_VACCINE_DISTRIBUTION_BATCH')")
    public ResponseEntity<OpenLmisResponse> getDistributionBatches(){
        return OpenLmisResponse.response("distributionBatches", distributionBatchService.getAll());
    }

    @RequestMapping(value = "/distribution-batches/{id}", method = GET, headers = ACCEPT_JSON)
    //@PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_VACCINE_DISTRIBUTION_BATCH')")
    public ResponseEntity<OpenLmisResponse> getDistributionBatchById(@PathVariable("id") Long id){
        return OpenLmisResponse.response("distributionBatch", distributionBatchService.getById(id));
    }

    @RequestMapping(value = "/distribution-batches", method = POST, headers = ACCEPT_JSON)
    //@PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_VACCINE_DISTRIBUTION_BATCH')")
    public ResponseEntity insertDistributionBatches(@RequestBody DistributionBatch distributionBatch, HttpServletRequest request){

        distributionBatch.setCreatedBy(loggedInUserId(request));
        distributionBatch.setModifiedBy(loggedInUserId(request));

        ResponseEntity<OpenLmisResponse> response;

        try {
            distributionBatchService.update(distributionBatch);
        }catch (DataException exception) {
            OpenLmisResponse openLmisResponse = new OpenLmisResponse("distributionBatch", distributionBatch);
            return openLmisResponse.errorEntity(exception, BAD_REQUEST);
        }
        response = success(messageService.message("message.distribution.batch.created.success"));
        response.getBody().addData("distributionBatch", distributionBatch);
        return response;
    }

    @RequestMapping(value = "/distribution-batches/{id}", method = PUT, headers = ACCEPT_JSON)
    //@PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE_DISTRIBUTION_BATCH')")
    public ResponseEntity<OpenLmisResponse> update(@PathVariable("id") long id,
                                                   @RequestBody DistributionBatch distributionBatch,
                                                   HttpServletRequest request) {
        distributionBatch.setId(id);
        distributionBatch.setModifiedBy(loggedInUserId(request));

        try {
            distributionBatchService.update(distributionBatch);
        } catch (DataException exception) {
            OpenLmisResponse openLmisResponse = new OpenLmisResponse("distributionBatch", distributionBatch);
            return openLmisResponse.errorEntity(exception, BAD_REQUEST);
        }

        String successMessage = messageService.message("message.distribution.batch.updated.success");
        OpenLmisResponse openLmisResponse = new OpenLmisResponse("distributionBatch", distributionBatch);
        return openLmisResponse.successEntity(successMessage);
    }

    @RequestMapping(value = "/manufacturers", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getManufacturers(){
        return OpenLmisResponse.response("manufacturers", manufacturerService.getAll());
    }

    @RequestMapping(value = "/distributionTypes", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getDistributionTypes(){
        return OpenLmisResponse.response("distributionTypes", DistributionType.values());
    }


}
