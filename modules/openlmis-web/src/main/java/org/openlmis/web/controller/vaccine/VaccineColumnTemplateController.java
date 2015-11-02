/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.web.controller.vaccine;

import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.dto.ProgramColumnTemplateDTO;
import org.openlmis.vaccine.service.VaccineColumnTemplateService;
import org.openlmis.vaccine.service.VaccineOrderRequisitionServices.VaccineOrderRequisitionsColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/vaccine/columns/")
public class VaccineColumnTemplateController extends BaseController {

  public static final String COLUMNS = "columns";
  @Autowired
  VaccineColumnTemplateService service;
  @Autowired
  VaccineOrderRequisitionsColumnService columnService;

  @RequestMapping(value = "get/{programId}")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable Long programId) {
    return OpenLmisResponse.response(COLUMNS, service.getTemplate(programId));
  }

  @RequestMapping(value="save")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody ProgramColumnTemplateDTO programColumnDTO) {
    service.saveChanges(programColumnDTO.getColumns());
    return OpenLmisResponse.response(COLUMNS, service.getTemplate(programColumnDTO.getProgramId()));
  }

  @RequestMapping(value = "get/columns")
  public ResponseEntity<OpenLmisResponse>getAll(){

    return OpenLmisResponse.response(COLUMNS,columnService.getAllColumns());
  }

}
