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

import org.openlmis.vaccine.domain.VaccineQuantification;
import org.openlmis.vaccine.service.VaccineQuantificationService;
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

import java.util.Date;

import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value="/vaccineQuantification")
public class VaccineQuantificationController extends BaseController{


    @Autowired
    VaccineQuantificationService vaccineQuantificationService;

    @RequestMapping(value = "/create", method = POST, headers = ACCEPT_JSON)
    // TODO: Add appropriate permission
    //  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE_QUANTIFICATION')")
    public ResponseEntity insert(@RequestBody VaccineQuantification vaccineQuantification, HttpServletRequest request) {

        vaccineQuantification.setCreatedBy(loggedInUserId(request));

        ResponseEntity<OpenLmisResponse> response;

        try {
            vaccineQuantificationService.updateVaccineQuantification(vaccineQuantification);
        } catch(DuplicateKeyException exp){
            return OpenLmisResponse.error("There is record with the same vaccine quantification year already.", HttpStatus.BAD_REQUEST);
        }

        response = success(messageService.message("message.vaccine.Quantification.created.success"));
        //response.getBody().addData("vaccineQuantification", vaccineQuantification);
        return response;
    }

    @RequestMapping(value = "/delete/{id}", method = DELETE, headers = ACCEPT_JSON)
    // TODO: Add appropriate permission
    //  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE_QUANTIFICATION')")
    public ResponseEntity deleteVaccineQuantification(@PathVariable(value="id") Long id){

        vaccineQuantificationService.deleteVaccineQuantification(id);

        ResponseEntity<OpenLmisResponse> response;
        response = success(messageService.message("message.vaccine.Quantification.created.success"));
        return response;
    }

    @RequestMapping(value = "/list", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineQuantifications() {

        return OpenLmisResponse.response("vaccineQuantifications", vaccineQuantificationService.getVaccineQuantifications());
    }

    @RequestMapping(value = "/get/{id}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineQuantification(@PathVariable(value="id") Long id){
        return OpenLmisResponse.response("vaccineQuantification", vaccineQuantificationService.getVaccineQuantification(id));
    }

    @RequestMapping(value = "/dilutionsList", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineQuantificationDilutionsList(@PathVariable(value="id") Long id){
        return OpenLmisResponse.response("dilution", vaccineQuantificationService.getVaccineDilutions());
    }

    @RequestMapping(value = "/administrationMode", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineQuantificationAdministrationMode(){
        return OpenLmisResponse.response("administrationMode", vaccineQuantificationService.getVaccineAdministrationMode());
    }


    @RequestMapping(value = "/vaccination_type", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineQuantificationVaccinationType(){
        return OpenLmisResponse.response("vaccinationType", vaccineQuantificationService.getVaccinationTypes());
    }

    @RequestMapping(value = "/formLookups", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineQuantificationFormLookps(){

        ResponseEntity<OpenLmisResponse> response;
        response = success("success");
        response.getBody().addData("dilution",  vaccineQuantificationService.getVaccineDilutions());
        response.getBody().addData("administrationMode", vaccineQuantificationService.getVaccineAdministrationMode());
        response.getBody().addData("vaccinationType", vaccineQuantificationService.getVaccinationTypes());

        return response;
    }



}
