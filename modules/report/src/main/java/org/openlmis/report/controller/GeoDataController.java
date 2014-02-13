/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.report.mapper.lookup.GeographicZoneReportMapper;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.response.OpenLmisResponse;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.openlmis.report.util.InteractiveReportPeriodFilterParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/gis/")
public class GeoDataController extends BaseController {

  public static final String USER_ID = "USER_ID";

  public static final String OPEN_LMIS_OPERATION_YEARS = "years";
  public static final String OPEN_LMIS_OPERATION_MONTHS = "months";

  @Autowired
  private GeographicZoneReportMapper reportLookupService;
  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  @RequestMapping(value="/reporting-rate", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getPrograms(){
      return OpenLmisResponse.response( "map", this.reportLookupService.getGeoReportingRate(1L,1L) );
  }

}
