package org.openlmis.web.controller;

import org.openlmis.equipment.repository.DonorRepository;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/donor/")
public class DonorController extends BaseController {

  @Autowired
  private DonorRepository repository;

  @RequestMapping(value="list")
  public ResponseEntity<OpenLmisResponse> getAll(){
    return OpenLmisResponse.response("donors", repository.getAll());
  }
}
