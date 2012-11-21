package org.openlmis.admin.controller;

import org.openlmis.admin.form.ProgramRnRTemplateForm;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.service.RnrTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("")
public class RnrTemplateController {

    private RnrTemplateService rnrTemplateService;

    @Autowired
    public RnrTemplateController(RnrTemplateService rnrTemplateService) {
        this.rnrTemplateService = rnrTemplateService;
    }

    @RequestMapping(value = "/rnr/{programCode}/columns", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<RnrColumn> fetchMasterColumnList(@PathVariable("programCode") String programCode) {
        return rnrTemplateService.fetchAllRnRColumns(programCode);
    }

    @RequestMapping(value = "/admin/rnr/{programCode}/columns", method = RequestMethod.POST, headers = "Accept=application/json")
    public void saveRnRTemplateForProgram(@PathVariable("programCode") String programCode, @RequestBody ProgramRnRTemplateForm programRnRTemplateForm) {
        rnrTemplateService.saveRnRTemplateForProgram(programCode, programRnRTemplateForm);
    }

}
