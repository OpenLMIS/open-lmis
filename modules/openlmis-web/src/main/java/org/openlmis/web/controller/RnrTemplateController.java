package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.service.RnrTemplateService;
import org.openlmis.web.form.RnrColumnList;
import org.openlmis.web.form.RnrTemplateForm;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@NoArgsConstructor
public class RnrTemplateController extends BaseController{

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
  @PreAuthorize("hasPermission('','CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public List<RnrColumn> fetchVisibleProgramRnrColumnList(@PathVariable("programId") Integer programId) {
    return rnrTemplateService.fetchVisibleRnRColumns(programId);
  }

  @RequestMapping(value = "/program/{programId}/rnr-template", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CONFIGURE_RNR')")
  public ResponseEntity saveRnRTemplateForProgram(@PathVariable("programId") Integer programId,
                                                  @RequestBody RnrColumnList rnrColumnList) {
      ProgramRnrTemplate programRnrTemplate = new ProgramRnrTemplate(programId, rnrColumnList);
      Map<String, OpenLmisMessage> validationErrors = rnrTemplateService.saveRnRTemplateForProgram(programRnrTemplate);
      ResponseEntity responseEntity;
      if (validationErrors != null && validationErrors.size() > 0) {
        responseEntity = OpenLmisResponse.error(validationErrors, HttpStatus.BAD_REQUEST);
      } else {
        responseEntity = OpenLmisResponse.success("Saved Successfully");
      }
      return responseEntity;
  }
}
