/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ReportTemplate;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ReportService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class ReportTemplateController extends BaseController {
  public static final String JASPER_UPLOAD_SUCCESS = "jasper.upload.success";
  public static final String ERROR_JASPER_UPLOAD = "error.jasper.upload";

  ReportService reportService;

  @Autowired
  public ReportTemplateController(ReportService reportService) {
    this.reportService = reportService;
  }

  @RequestMapping(value = "/report-templates", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'UPLOAD_REPORT')")
  public ResponseEntity<OpenLmisResponse> uploadJasperTemplate(HttpServletRequest request, MultipartFile file, String name) {

    try {
      ReportTemplate reportTemplate = new ReportTemplate(file, loggedInUserId(request));
      reportService.insert(reportTemplate);
      return success(JASPER_UPLOAD_SUCCESS);
    } catch (IOException e) {
      return error(ERROR_JASPER_UPLOAD, BAD_REQUEST);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
  }

}
