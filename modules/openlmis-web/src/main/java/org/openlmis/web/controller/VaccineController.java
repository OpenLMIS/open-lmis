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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.vaccine.domain.VaccineTarget;
import org.openlmis.vaccine.service.VaccineService;
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

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller handles endpoints to Vaccine related features
 */
@Controller
@NoArgsConstructor
@RequestMapping(value="/vaccine")
public class VaccineController extends BaseController {

   @Autowired
   private VaccineService vaccineService;

    @RequestMapping(value = "/target/create", method = POST, headers = ACCEPT_JSON)
    // TODO: Add appropriate permission
    //  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE')")
    public ResponseEntity insert(@RequestBody VaccineTarget vaccineTarget, HttpServletRequest request) {

        vaccineTarget.setCreatedBy(loggedInUserId(request));
        vaccineTarget.setModifiedBy(loggedInUserId(request));

        ResponseEntity<OpenLmisResponse> response;

        try {
            vaccineService.updateVaccineTarget(vaccineTarget);
        } catch(DuplicateKeyException exp){
        return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
    }

        response = success(messageService.message("message.vaccine.target.created.success"));
        response.getBody().addData("vaccineTarget", vaccineTarget);
        return response;
    }

    @RequestMapping(value = "/target/delete/{id}", method = DELETE, headers = ACCEPT_JSON)
    // TODO: Add appropriate permission
    //  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE')")
    public ResponseEntity deleteVaccineTarget(@PathVariable(value="id") Long id){

        vaccineService.deleteVaccineTarget(id);

        ResponseEntity<OpenLmisResponse> response;
        response = success(messageService.message("message.vaccine.target.created.success"));
        return response;
    }

    @RequestMapping(value = "/target/list", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineTargets() {

        return OpenLmisResponse.response("vaccineTargets", vaccineService.getVaccineTargets());
    }

    @RequestMapping(value = "/target/get/{id}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getVaccineTarget(@PathVariable(value="id") Long id){
        return OpenLmisResponse.response("vaccineTarget", vaccineService.getVaccineTarget(id));
    }

}
