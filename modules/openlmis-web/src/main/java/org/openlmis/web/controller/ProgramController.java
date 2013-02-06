package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
public class ProgramController extends BaseController {

    private ProgramService programService;

    @Autowired
    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @RequestMapping(value = "/admin/programs", method = GET)
    @PreAuthorize("hasPermission('','CONFIGURE_RNR')")
    public List<Program> getAllActivePrograms() {
        return programService.getAllActive();
    }

    @RequestMapping(value = "/facilities/{facilityId}/programs.json", method = GET)
    @PreAuthorize("hasPermission('','CREATE_REQUISITION, AUTHORIZE_REQUISITION, MANAGE_USERS')")
    public List<Program> getProgramsForFacility(@PathVariable(value = "facilityId") Integer facilityId) {
        return programService.getByFacility(facilityId);
    }

    @RequestMapping(value = "/logistics/facility/{facilityId}/user/programs.json", method = GET)
    public List<Program> getUserSupportedProgramsToCreateOrAuthorizeRequisition(@PathVariable(value = "facilityId") Integer facilityId, HttpServletRequest request) {
        return programService.getProgramsSupportedByFacilityForUserWithRight(facilityId, loggedInUserId(request), CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    }

    @RequestMapping(value = "/create/requisition/supervised/programs.json", method = GET)
    public List<Program> getUserSupervisedActiveProgramsForCreateAndAuthorizeRequisition(HttpServletRequest request) {
        return programService.getUserSupervisedActiveProgramsWithRights(loggedInUserId(request), CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    }


}
