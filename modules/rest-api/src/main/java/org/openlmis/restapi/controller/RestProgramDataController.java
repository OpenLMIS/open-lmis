package org.openlmis.restapi.controller;

import com.wordnik.swagger.annotations.Api;
import lombok.NoArgsConstructor;
import org.openlmis.restapi.domain.ProgramDataFormDTO;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProgramDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
@Api(value = "Program data", description = "Program data API")
public class RestProgramDataController extends BaseController {

  @Autowired
  private RestProgramDataService restProgramDataService;

  @RequestMapping(value = "/rest-api/programData", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity createProgramDataForm(@RequestBody ProgramDataFormDTO programDataForm, Principal principal) {

    restProgramDataService.createProgramDataForm(programDataForm, loggedInUserId(principal));
    return RestResponse.success("api.program.data.save.success");
  }
}
