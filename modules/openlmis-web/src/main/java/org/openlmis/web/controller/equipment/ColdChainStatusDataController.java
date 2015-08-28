package org.openlmis.web.controller.equipment;

import org.openlmis.equipment.service.ColdChainStatusDataService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value="/equipment/cold-chain/")
public class ColdChainStatusDataController extends BaseController {


  @Autowired
  ColdChainStatusDataService service;

  @RequestMapping(method = GET, value = "designations")
  public ResponseEntity<OpenLmisResponse> getDesignations( ){
    return OpenLmisResponse.response("designations",service.getAllDesignations());
  }

  @RequestMapping(method = GET, value = "pqsStatus")
  public ResponseEntity<OpenLmisResponse> getPqsStatus( ){
    return OpenLmisResponse.response("pqs_status",service.getAllPqsStatus());
  }

}
