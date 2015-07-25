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

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.core.service.StockManagementService;
import org.openlmis.web.model.GeoZoneInfo;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.openlmis.core.domain.Lot;

/**
 * This controller provides GET, POST, and DELETE endpoints related to stock management.
 */

@Controller
@Api(value = "Stock Management", description = "Track the stock on hand at various facilities.")
public class StockManagementController extends BaseController
{
  @Autowired
  private StockManagementService service;

  //TODO: Determine what the permissions associated with @PreAuthorize should be. (MANAGE_PROGRAM_PRODUCT, below, is just a placeholder).

  @RequestMapping(value = "/api/2/lots/{lotId}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  @ApiOperation(value = "Get information about the specified product lot (batch).",
          notes = "(This endpoint is not yet ready for use.) <p /> Note that the products property will always be an object with an ID value. Optionally, it may be expanded to include all of the product's other properties (and associated values) as well. To specify that such an expansion should occur, add \"?expand=product\" to the query parameter. For example: <p> /api/2/lots/{lotId}?expand=product",
          response = Lot.class)
  public ResponseEntity getLot(@PathVariable Long lotId, @RequestParam(value = "expand", required = false) String expand)
  {
    boolean expandProduct = (expand != null && expand.contains("product"));
    Lot lot = service.getTestLot(lotId, expandProduct);

    if(lot != null)
      return OpenLmisResponse.response(lot);
    else
      return OpenLmisResponse.error("The specified lot does not exist." , HttpStatus.NOT_FOUND);
  }

}
