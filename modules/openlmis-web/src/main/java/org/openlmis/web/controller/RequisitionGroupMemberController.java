/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupMemberService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class RequisitionGroupMemberController extends BaseController {

    @Autowired
    RequisitionGroupMemberService requisitionGroupMemberService;

    @Autowired
    FacilityService facility;

    @Autowired
    RequisitionGroupService requisitionGroupService;

    @RequestMapping(value="/requisitionGroupMember/insert",method=POST,headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REQUISITION_GROUP')")
    public ResponseEntity<OpenLmisResponse> insert(@RequestBody RequisitionGroupMember requisitionGroupMember, HttpServletRequest request){
        ResponseEntity<OpenLmisResponse> successResponse;
        requisitionGroupMember.setModifiedBy(loggedInUserId(request));
        try {
            requisitionGroupMemberService.save(requisitionGroupMember);
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
        successResponse = success(String.format("Requisition group member has been successfully saved"));
        successResponse.getBody().addData("requisitionGroupMember", requisitionGroupMember);
        return successResponse;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequestMapping(value="/requisitionGroupMember/remove/{rgId}/{facId}",method = GET,headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REQUISITION_GROUP')")
    public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value="rgId") Long requisitionGroupId, @PathVariable(value="facId") Long facilityID, HttpServletRequest request){
        ResponseEntity<OpenLmisResponse> successResponse;
        try {
            requisitionGroupMemberService.removeRequisitionGroupMember(requisitionGroupService.loadRequisitionGroupById(requisitionGroupId),facility.getById(facilityID));
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
        successResponse = success(String.format("Requisition group member has been successfully removed"));
        //successResponse.getBody().addData("requisitionGroupMember", requisitionGroupMember);
        return successResponse;
    }
}