/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.service.RnrTemplateService;
import org.openlmis.web.form.RnrColumnList;
import org.openlmis.web.form.RnrTemplateForm;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.openlmis.core.web.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller handles endpoint to list, create rnr template.
 */

@Controller
public class RnrTemplateController extends BaseController {

  public static final String RNR_TEMPLATE_SAVE_SUCCESS = "template.save.success";

  @Autowired
  private RnrTemplateService rnrTemplateService;

  @RequestMapping(value = "/program/{programId}/rnr-template", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_RNR')")
  public RnrTemplateForm fetchAllProgramRnrColumnList(@PathVariable("programId") Long programId) {
    List<RnRColumnSource> sources = new ArrayList<>();
    sources.add(RnRColumnSource.USER_INPUT);
    sources.add(RnRColumnSource.CALCULATED);
    return new RnrTemplateForm(rnrTemplateService.fetchAllRnRColumns(programId), sources);
  }

  @RequestMapping(value = "/rnr/{programId}/columns", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION, VIEW_REQUISITION')")
  public List<RnrColumn> fetchColumnsForRequisition(@PathVariable("programId") Long programId) {
    return rnrTemplateService.fetchColumnsForRequisition(programId);
  }

  @RequestMapping(value = "/program/{programId}/rnr-template", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_RNR')")
  public ResponseEntity<OpenLmisResponse> saveRnRTemplateForProgram(@PathVariable("programId") Long programId,
                                                                    @RequestBody RnrColumnList rnrColumnList,
                                                                    HttpServletRequest request) {
    ProgramRnrTemplate programRnrTemplate = new ProgramRnrTemplate(programId, rnrColumnList);
    programRnrTemplate.setModifiedBy(loggedInUserId(request));
    Map<String, OpenLmisMessage> validationErrors = rnrTemplateService.saveRnRTemplateForProgram(programRnrTemplate);
    ResponseEntity<OpenLmisResponse> responseEntity;
    if (!validationErrors.isEmpty()) {
      responseEntity = response(getMessages(validationErrors), HttpStatus.BAD_REQUEST);
      responseEntity.getBody().addData("error", "form.error");
    } else {
      responseEntity = success(messageService.message(RNR_TEMPLATE_SAVE_SUCCESS));
    }
    return responseEntity;
  }

  private Map<String, String> getMessages(Map<String, OpenLmisMessage> validationErrors) {
    Map<String, String> validationErrorMessages = new HashMap<>();
    for (Map.Entry<String, OpenLmisMessage> entry : validationErrors.entrySet()) {
      String fieldName = entry.getKey();
      validationErrorMessages.put(fieldName, messageService.message(entry.getValue().getCode(), entry.getValue().getParams()));
    }
    return validationErrorMessages;
  }

}
