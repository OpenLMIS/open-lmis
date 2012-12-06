package org.openlmis.admin.controller;

import lombok.NoArgsConstructor;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@NoArgsConstructor
public class ProgramController {

    private ProgramService programService;

    private RoleRightsService roleRightsService;

    @Autowired
    public ProgramController(ProgramService programService, RoleRightsService roleRightsService) {
        this.programService = programService;
        this.roleRightsService = roleRightsService;
    }

    @RequestMapping(value = "/admin/programs", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Program> getAllActivePrograms() {
        return programService.getAllActive();
    }

    @RequestMapping(value = "/logistics/facility/{facilityCode}/programs.json", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Program> getProgramsForFacility(@PathVariable(value = "facilityCode") String facilityCode) {
        return programService.getByFacilityCode(facilityCode);
    }

    @RequestMapping(value = "/logistics/facility/{facilityCode}/user/programs.json", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Program> getUserSupportedProgramsToCreateRequisition(@PathVariable(value = "facilityCode") String facilityCode, HttpServletRequest request) {
        List<RoleAssignment> userSupportedProgramRoles = roleRightsService.getProgramWithGivenRightForAUser(Right.CREATE_REQUISITION, loggedInUser(request));
        return programService.filterActiveProgramsAndFacility(userSupportedProgramRoles, facilityCode);
    }

    private String loggedInUser(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(UserAuthenticationSuccessHandler.USER);
    }
}
