package org.openlmis.restapi.controller;

import com.wordnik.swagger.annotations.Api;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.restapi.domain.ProgramDataFormDTO;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProgramDataService;
import org.openlmis.restapi.service.RestRequisitionService;
import org.openlmis.rnr.domain.Rnr;
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

  @Autowired
  private RestRequisitionService restRequisitionService;

  @RequestMapping(value = "/rest-api/programData", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity createProgramDataForm(@RequestBody ProgramDataFormDTO programDataForm, Principal principal) {

    Rnr requisition = restProgramDataService.createProgramDataForm(programDataForm, loggedInUserId(principal));
    if (requisition != null) {
      restRequisitionService.notifySubmittedEvent(requisition);
    }
    return RestResponse.success("api.program.data.save.success");
  }

  /* 为了兼容android端的老版本，"programCode":"RAPID_TEST", 在新版本中android端将使用TEST_KIT替代 */
  @RequestMapping(value = "/rest-api/programData/facilities/{facilityId}", method = GET)
  public ResponseEntity getProgramDataFormsByFacility(@PathVariable Long facilityId) {
    List<ProgramDataFormDTO> programDataFormList = restProgramDataService.getProgramDataFormsByFacility(facilityId);
    return RestResponse.response("programDataForms", programDataFormList);
  }
}
