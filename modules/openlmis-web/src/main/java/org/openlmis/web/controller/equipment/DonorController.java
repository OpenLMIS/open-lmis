package org.openlmis.web.controller.equipment;

import org.openlmis.core.exception.DataException;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.service.DonorService;
import org.openlmis.web.controller.BaseController;
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

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.openlmis.web.response.OpenLmisResponse.*;

@Controller
@RequestMapping(value="/donor/")
public class DonorController extends BaseController {

  @Autowired
  private DonorService donorService;

  @RequestMapping(value="list",method= GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DONOR')")
  public ResponseEntity<OpenLmisResponse> getAll(){
    return OpenLmisResponse.response("donors",donorService.getAllWithDetails());
  }

  @RequestMapping(value="insert.json",method=POST,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DONOR')")
  public ResponseEntity<OpenLmisResponse> insert(@RequestBody Donor donor, HttpServletRequest request){
    ResponseEntity<OpenLmisResponse> successResponse;
    donor.setModifiedBy(loggedInUserId(request));
    try {
      donorService.save(donor);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    successResponse = success(String.format("Donor '%s' has been successfully saved", donor.getShortName()));
    successResponse.getBody().addData("donor", donor);
    return successResponse;
  }

  @RequestMapping(value="getDetails/{id}",method = GET,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DONOR')")
  public ResponseEntity<OpenLmisResponse> getDetailsForDonor(@PathVariable(value="id") Long id){
    return OpenLmisResponse.response("donor",donorService.getById(id));
  }

  @RequestMapping(value="remove/{id}",method = GET,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DONOR')")
  public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value="id") Long donorId, HttpServletRequest request){
    ResponseEntity<OpenLmisResponse> successResponse;
    try {
      donorService.removeDonor(donorId);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    successResponse = success(String.format("Donor has been successfully removed"));
    return successResponse;
  }

}
