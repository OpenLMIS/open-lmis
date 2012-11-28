package org.openlmis.admin.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@NoArgsConstructor
public class ProgramController {

    private ProgramService programService;

    @Autowired
    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @RequestMapping(value = "/admin/programs", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Program> getAllActivePrograms() {
        return programService.getAllActive();
    }

    @RequestMapping(value = "/logistics/facility/{facilityCode}/programs.json", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Program> getProgramsForFacility(@PathVariable(value = "facilityCode") String facilityCode) {
        return programService.getByFacilityCode(facilityCode);
    }

}
