package org.openlmis.restapi.controller;


import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class RestProductController extends BaseController{

  @Autowired
  private RestProductService restProductService;

  @RequestMapping(value = "/rest-api/kits", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity createKit(@RequestBody(required = true) Product kit) {
    restProductService.buildAndSave(kit);
    return RestResponse.success("msg.kit.createsuccess");
  }

}
