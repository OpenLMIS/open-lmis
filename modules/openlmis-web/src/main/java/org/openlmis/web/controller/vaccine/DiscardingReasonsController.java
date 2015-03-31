package org.openlmis.web.controller.vaccine;

import org.openlmis.vaccine.service.DiscardingReasonsService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/vaccine/discarding/reasons/")
public class DiscardingReasonsController extends BaseController {

  @Autowired
  DiscardingReasonsService service;

  @RequestMapping("all")
  public ResponseEntity<OpenLmisResponse> getAll(){
    return OpenLmisResponse.response("reasons", service.getAllReasons());
  }

}
