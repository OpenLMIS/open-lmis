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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @RequestMapping(value = "/program/{programId}/rnr-template", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasPermission('','CONFIGURE_RNR')")
    public RnrTemplateForm fetchAllProgramRnrColumnList(@PathVariable("programId") Integer programId) {
        List<RnRColumnSource> sources = new ArrayList<>();
        sources.add(RnRColumnSource.USER_INPUT);
        sources.add(RnRColumnSource.CALCULATED);
        return new RnrTemplateForm(rnrTemplateService.fetchAllRnRColumns(programId), sources);
    }

    @RequestMapping(value = "/logistics/rnr/{programId}/columns", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
    public List<RnrColumn> fetchVisibleProgramRnrColumnList(@PathVariable("programId") Integer programId) {
        return rnrTemplateService.fetchVisibleRnRColumns(programId);
    }

    @RequestMapping(value = "/program/{programId}/rnr-template", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasPermission('','CONFIGURE_RNR')")
    public ResponseEntity saveRnRTemplateForProgram(@PathVariable("programId") Integer programId,
                                                    @RequestBody RnrColumnList rnrColumnList) {

        ProgramRnrTemplate programRnrTemplate = new ProgramRnrTemplate(programId, rnrColumnList);
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
