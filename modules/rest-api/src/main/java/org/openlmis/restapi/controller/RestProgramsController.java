package org.openlmis.restapi.controller;


import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.domain.ProgramWithRegimens;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProgramsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class RestProgramsController extends BaseController {

  public static final String PROGRAMS = "programs";

  @Autowired
  private RestProgramsService restProgramsService;

  @RequestMapping(value = "/rest-api/associate-programs", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity associatePrograms(@RequestParam(required = true) Long parentProgramId, @RequestBody(required = true) List<String> programCodes) {
    restProgramsService.associate(parentProgramId, programCodes);
    return RestResponse.success("msg.rnr.programs.success");
  }

  @RequestMapping(value = "/rest-api/programs/{facilityId}", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity getProgramsByFacilityId(@PathVariable(value = "facilityId") Long facilityId) {
    List<ProgramWithRegimens> programWithRegimens;
    try {
      programWithRegimens = restProgramsService.getAllProgramWithRegimenByFacilityId(facilityId);
    } catch (DataException e) {
      return RestResponse.error(e.getOpenLmisMessage(), BAD_REQUEST);
    }
    return RestResponse.response(PROGRAMS, programWithRegimens);
  }
}
