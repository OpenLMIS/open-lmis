package org.openlmis.admin.controller;

import org.openlmis.admin.form.ProgramRnRTemplateForm;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.service.RnRTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class RnRTemplateController {

    private RnRTemplateService rnrTemplateService;

    @Autowired
    public RnRTemplateController(RnRTemplateService rnrTemplateService) {
        this.rnrTemplateService = rnrTemplateService;
    }

    @RequestMapping(value = "/rnr/master/columns", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<RnrColumn> fetchMasterColumnList() {
        return rnrTemplateService.fetchAllMasterColumns();
    }

    @RequestMapping(value = "/rnr/{programId}/columns", method = RequestMethod.POST, headers = "Accept=application/json")
    public void createRnRTemplateForProgram(@PathVariable String programId, @RequestBody ProgramRnRTemplateForm programRnRTemplateForm) {
        rnrTemplateService.createRnRTemplateForProgram(Integer.parseInt(programId), programRnRTemplateForm);
    }

}
