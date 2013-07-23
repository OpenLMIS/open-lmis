package org.openlmis.restapi.controller;


import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.domain.CHW;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestCHWService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class RestCHWController extends BaseController {

  @Autowired
  private RestCHWService restCHWService;

  @RequestMapping(value = "/rest-api/chw", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<RestResponse> createCHW(@RequestBody CHW chw) {
    try {
      restCHWService.create(chw);
    } catch (DataException e) {
      return RestResponse.error(e.getOpenLmisMessage(), BAD_REQUEST);
    }
    return RestResponse.success("CHW created successfully");
  }
}
