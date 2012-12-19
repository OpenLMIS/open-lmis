package org.openlmis.web.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.web.form.RnrColumnList;
import org.openlmis.web.form.RnrTemplateForm;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.service.RnrTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@NoArgsConstructor
public class RnrTemplateController {

    private RnrTemplateService rnrTemplateService;

    @Autowired
    public RnrTemplateController(RnrTemplateService rnrTemplateService) {
        this.rnrTemplateService = rnrTemplateService;
    }

    // TODO : url should have rnr-template and not rnr
    @RequestMapping(value = "/admin/rnr/{programCode}/columns", method = RequestMethod.GET, headers = "Accept=application/json")
    public RnrTemplateForm fetchAllProgramRnrColumnList(@PathVariable("programCode") String programCode) {
        List<RnRColumnSource> sources = new ArrayList<>();
        sources.add(RnRColumnSource.USER_INPUT);
        sources.add(RnRColumnSource.CALCULATED);
        return new RnrTemplateForm(rnrTemplateService.fetchAllRnRColumns(programCode), sources);
    }

    @RequestMapping(value = "/logistics/rnr/{programCode}/columns", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<RnrColumn> fetchVisibleProgramRnrColumnList(@PathVariable("programCode") String programCode) {
        return rnrTemplateService.fetchVisibleRnRColumns(programCode);
    }

    @RequestMapping(value = "/admin/rnr/{programCode}/columns", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity saveRnRTemplateForProgram(@PathVariable("programCode") String programCode,
                                                    @RequestBody RnrColumnList rnrColumnList) {

        ProgramRnrTemplate programRnrTemplate = new ProgramRnrTemplate(programCode, rnrColumnList);
        Map<String, String> validationErrors = rnrTemplateService.saveRnRTemplateForProgram(programRnrTemplate);
        ResponseEntity responseEntity;
        if (validationErrors != null && validationErrors.size() > 0) {
            ValidationError errorWrapper = new ValidationError();
            errorWrapper.setErrorMap(validationErrors);
            responseEntity = new ResponseEntity(errorWrapper, HttpStatus.BAD_REQUEST);
        } else {
            responseEntity = new ResponseEntity(HttpStatus.OK);
        }
        return responseEntity;
    }

    @Data
    @NoArgsConstructor
    private class ValidationError {
        private Map<String, String> errorMap;

    }
}
