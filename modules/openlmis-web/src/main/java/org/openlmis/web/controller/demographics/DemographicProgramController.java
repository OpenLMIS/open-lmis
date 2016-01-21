package org.openlmis.web.controller.demographics;

import org.openlmis.core.domain.RightName;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DemographicProgramController extends BaseController {

  public static final String PROGRAMS = "programs";

  @Autowired
  ProgramService programService;

  @RequestMapping(value = "/demographics/programs.json", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DEMOGRAPHIC_ESTIMATES')")
  public ResponseEntity<OpenLmisResponse> getProgramsForDemographicEstimates(HttpServletRequest request) {
    return OpenLmisResponse.response(PROGRAMS, programService.getProgramsForUserByRights(loggedInUserId(request), RightName.MANAGE_DEMOGRAPHIC_ESTIMATES));
  }

}
