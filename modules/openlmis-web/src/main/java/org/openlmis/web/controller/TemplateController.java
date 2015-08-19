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

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.service.TemplateService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static org.openlmis.core.web.OpenLmisResponse.error;
import static org.openlmis.core.web.OpenLmisResponse.success;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller handles endpoint related to GET, POST operations on Template.
 */

@Controller
@NoArgsConstructor
public class TemplateController extends BaseController {
  public static final String JASPER_CREATE_REPORT_SUCCESS = "create.report.success";
  public static final String JASPER_CREATE_REPORT_ERROR = "create.report.error";
  public static final String CONSISTENCY_REPORT = "Consistency Report";

  @Autowired
  private TemplateService templateService;

  @RequestMapping(value = "/report-templates", method = POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REPORT')")
  public ResponseEntity<OpenLmisResponse> createJasperReportTemplate(HttpServletRequest request, MultipartFile file, String name, String description) {
    try {
      Template template = new Template(name, null, null, CONSISTENCY_REPORT, description);
      template.setCreatedBy(loggedInUserId(request));
      templateService.validateFileAndInsertTemplate(template, file);

      return success(messageService.message(JASPER_CREATE_REPORT_SUCCESS), MediaType.TEXT_HTML_VALUE);
    } catch (IOException e) {
      return error(messageService.message(JASPER_CREATE_REPORT_ERROR), OK, MediaType.TEXT_HTML_VALUE);
    } catch (DataException e) {
      return OpenLmisResponse.error(e, OK, MediaType.TEXT_HTML_VALUE);
    }
  }

  @RequestMapping(value = "/report-templates", method = GET)
  @PreAuthorize("@permissionEvaluator.hasReportingPermission(principal)")
  public List<Template> getAllTemplatesForUser(HttpServletRequest request) {
    return templateService.getAllTemplatesForUser(loggedInUserId(request));
  }
}
