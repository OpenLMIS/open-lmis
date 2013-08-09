package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.openlmis.core.service.RequisitionGroupService;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.openlmis.web.response.OpenLmisResponse.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Henok
 * Date: 7/6/13
 * Time: 5:28 PM
 */

@Controller
@NoArgsConstructor
public class RequisitionGroupController extends BaseController {

    @Autowired
    RequisitionGroupService requisitionGroupService;

    @RequestMapping(value="/requisitionGroup/getList",method= GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REQUISITION_GROUP')")
    public ResponseEntity<OpenLmisResponse> getRequisitionGroupList(HttpServletRequest request){
        return OpenLmisResponse.response("requisitionGroups",requisitionGroupService.getCompleteList());
    }

    @RequestMapping(value="/requisitionGroup/insert.json",method=POST,headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REQUISITION_GROUP')")
    public ResponseEntity<OpenLmisResponse> insert(@RequestBody RequisitionGroup requisitionGroup, HttpServletRequest request){
        ResponseEntity<OpenLmisResponse> successResponse;
        requisitionGroup.setModifiedBy(loggedInUserId(request));
        try {
            requisitionGroupService.saveChanges(requisitionGroup);
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
        successResponse = success(String.format("Requisition group '%s' has been successfully saved", requisitionGroup.getName()));
        successResponse.getBody().addData("requisitionGroup", requisitionGroup);
        return successResponse;
    }

    @RequestMapping(value="/requisitionGroup/getDetails/{id}",method = GET,headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REQUISITION_GROUP')")
    public ResponseEntity<OpenLmisResponse> getDetailsForRequisitionGroup(@PathVariable(value="id") Long id){
        return OpenLmisResponse.response("requisitionGroup",requisitionGroupService.loadRequisitionGroupById(id));
    }

    @RequestMapping(value="/requisitionGroup/remove/{id}",method = GET,headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REQUISITION_GROUP')")
    public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value="id") Long requisitionGroupId, HttpServletRequest request){
        ResponseEntity<OpenLmisResponse> successResponse;
        try {
            requisitionGroupService.removeRequisitionGroup(requisitionGroupId);
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
        successResponse = success(String.format("Requisition group has been successfully removed"));
        return successResponse;
    }

}