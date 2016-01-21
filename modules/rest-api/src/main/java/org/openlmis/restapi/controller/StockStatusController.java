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

package org.openlmis.restapi.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.openlmis.report.model.dto.StockStatusDTO;
import org.openlmis.report.service.StockStatusService;
import org.openlmis.restapi.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Api(value="Stock Status", description = "Returns monthly and quarterly stock status", position = 0)
public class StockStatusController extends BaseController {

  @Autowired
  private StockStatusService service;



  @ApiOperation(value = "Returns stock status details", notes = "Returns a complete list stock status for selected program.", response = StockStatusDTO.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful retrieval of Stock Status detail", response = StockStatusDTO.class),
    @ApiResponse(code = 404, message = "Given program does not exist"),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/stock-status/quarter", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity getStockStatusQuarterly(
                      @RequestParam("quarter") Long quarter,
                      @RequestParam("year") Long year,
                      @RequestParam("program") String program,
                      Principal principal) {
    return RestResponse.response("report", service.getStockStatusByQuarter(program, year, quarter, loggedInUserId(principal)));
  }

  @ApiOperation(value = "Returns stock status details", notes = "Returns a complete list stock status for selected program.", response = StockStatusDTO.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful retrieval of Stock Status detail", response = StockStatusDTO.class),
    @ApiResponse(code = 404, message = "Given program does not exist"),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/stock-status/monthly", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity getStockStatusMonthly(
    @RequestParam("month") Long month,
    @RequestParam("year") Long year,
    @RequestParam("program") String program,
    Principal principal) {
    return RestResponse.response("report", service.getStockStatusByMonth(program, year, month,loggedInUserId(principal)));
  }

}
