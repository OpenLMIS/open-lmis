package org.openlmis.restapi.controller;

import com.wordnik.swagger.annotations.Api;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.restapi.domain.ProgramDataFormDTO;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProgramDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
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

  @RequestMapping(value = "/rest-api/programData/facilities/{facilityId}", method = GET)
  public ResponseEntity getProgramDataFormsByFacility(@PathVariable Long facilityId) {
    List<ProgramDataFormDTO> programDataFormList = restProgramDataService.getProgramDataFormsByFacility(facilityId);
    return RestResponse.response("programData", programDataFormList);
  }
}
