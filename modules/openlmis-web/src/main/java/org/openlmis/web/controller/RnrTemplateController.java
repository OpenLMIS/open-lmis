/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.service.RnrTemplateService;
import org.openlmis.web.form.RnrColumnList;
import org.openlmis.web.form.RnrTemplateForm;
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

import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class RnrTemplateController extends BaseController {

  public static final String RNR_TEMPLATE_SAVE_SUCCESS = "template.save.success";
  private RnrTemplateService rnrTemplateService;

  @Autowired
  public RnrTemplateController(RnrTemplateService rnrTemplateService) {
    this.rnrTemplateService = rnrTemplateService;
  }

  @RequestMapping(value = "/program/{programId}/rnr-template", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_RNR')")
  public RnrTemplateForm fetchAllProgramRnrColumnList(@PathVariable("programId") Long programId) {
    List<RnRColumnSource> sources = new ArrayList<>();
    sources.add(RnRColumnSource.USER_INPUT);
    sources.add(RnRColumnSource.CALCULATED);
    return new RnrTemplateForm(rnrTemplateService.fetchAllRnRColumns(programId), sources);
  }

  @RequestMapping(value = "/rnr/{programId}/columns", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION')")
  private List<RnrColumn> fetchColumnsForRequisition(@PathVariable("programId") Long programId) {
    return rnrTemplateService.fetchColumnsForRequisition(programId);
  }

  @RequestMapping(value = "/program/{programId}/rnr-template", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_RNR')")
  public ResponseEntity saveRnRTemplateForProgram(@PathVariable("programId") Long programId,
                                                  @RequestBody RnrColumnList rnrColumnList,
                                                  HttpServletRequest request) {
    ProgramRnrTemplate programRnrTemplate = new ProgramRnrTemplate(programId, rnrColumnList);
    programRnrTemplate.setModifiedBy(loggedInUserId(request));
    Map<String, OpenLmisMessage> validationErrors = rnrTemplateService.saveRnRTemplateForProgram(programRnrTemplate);
    ResponseEntity responseEntity;
    if (!validationErrors.isEmpty()) {
      responseEntity = response(getMessages(validationErrors), HttpStatus.BAD_REQUEST);
    } else {
      responseEntity = success(messageService.message(RNR_TEMPLATE_SAVE_SUCCESS));
    }
    return responseEntity;
  }

  private Map<String, String> getMessages(Map<String, OpenLmisMessage> validationErrors) {
    Map<String, String> validationErrorMessages = new HashMap<>();
    for (Map.Entry<String, OpenLmisMessage> entry : validationErrors.entrySet()) {
      String fieldName = entry.getKey();
      validationErrorMessages.put(fieldName, messageService.message(entry.getValue()));
    }
    return validationErrorMessages;
  }

}
