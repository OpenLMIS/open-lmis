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

import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.RegimenService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.rnr.domain.RegimenTemplate;
import org.openlmis.rnr.service.RegimenColumnService;
import org.openlmis.web.form.RegimenFormDTO;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.openlmis.core.web.OpenLmisResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller handles endpoint related to create, list regimens
 */

@Controller
public class RegimenController extends BaseController {

  public static final String REGIMENS_SAVED_SUCCESSFULLY = "regimens.saved.successfully";

  @Autowired
  private RegimenService regimenService;

  @Autowired
  private RegimenColumnService regimenColumnService;

  public static final String REGIMENS = "regimens";
  public static final String REGIMEN_CATEGORIES = "regimen_categories";

  @Transactional
  @RequestMapping(value = "/programId/{programId}/regimens", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REGIMEN_TEMPLATE')")
  public ResponseEntity<OpenLmisResponse> save(@PathVariable Long programId, @RequestBody RegimenFormDTO regimenFormDTO, HttpServletRequest request) {
    regimenService.save(regimenFormDTO.getRegimens(), loggedInUserId(request));
    RegimenTemplate regimenTemplate = new RegimenTemplate(programId, regimenFormDTO.getRegimenColumnList());
    regimenColumnService.save(regimenTemplate, loggedInUserId(request));
    return success(REGIMENS_SAVED_SUCCESSFULLY);
  }

  @RequestMapping(value = "/programId/{programId}/regimens", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REGIMEN_TEMPLATE')")
  public ResponseEntity<OpenLmisResponse> getByProgram(@PathVariable("programId") Long programId) {
    try {
      ResponseEntity<OpenLmisResponse> response;
      List<Regimen> regimens = regimenService.getByProgram(programId);
      response = response(REGIMENS, regimens);
      return response;
    } catch (DataException dataException) {
      return error(UNEXPECTED_EXCEPTION, HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/regimenCategories", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REGIMEN_TEMPLATE')")
  public ResponseEntity<OpenLmisResponse> getAllRegimenCategories() {
    try {
      ResponseEntity<OpenLmisResponse> response;
      List<RegimenCategory> regimenCategories = regimenService.getAllRegimenCategories();
      response = response(REGIMEN_CATEGORIES, regimenCategories);
      return response;
    } catch (DataException dataException) {
      return error(UNEXPECTED_EXCEPTION, HttpStatus.BAD_REQUEST);
    }
  }

}
