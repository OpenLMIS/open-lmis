package org.openlmis.web.controller;

import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.core.service.BudgetFileTemplateService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static java.util.Arrays.asList;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class BudgetFileTemplateController extends BaseController {
  @Autowired
  BudgetFileTemplateService service;

  @RequestMapping(value = "/budget-file-template", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_EDI')")
  public ResponseEntity<OpenLmisResponse> get() {
    return response("budget_template", service.get());
  }

  @RequestMapping(value = "/budget-file-template", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_EDI')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody EDIFileTemplate ediFileTemplate,
                                                 HttpServletRequest request) {
    ediFileTemplate.validateAndSetModifiedBy(loggedInUserId(request), asList("facilityCode", "programCode", "periodStartDate", "allocatedBudget"));
    service.update(ediFileTemplate);

    return success("budget.file.configuration.success");
  }
}
