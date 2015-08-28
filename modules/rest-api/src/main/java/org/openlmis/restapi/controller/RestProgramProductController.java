/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.Closure;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.dto.ProgramProductDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.restapi.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.forAllDo;
import static org.openlmis.restapi.response.RestResponse.error;
import static org.openlmis.restapi.response.RestResponse.response;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller is responsible for handling API endpoint to get product list with details
 * for a program to support the replenishment process.
 */

@Controller
@NoArgsConstructor
@Api(value="Program Products", description = "List of products with details", position = 4)
public class RestProgramProductController extends BaseController {

  public static final String PROGRAM_PRODUCT_LIST = "programProductList";

  @Autowired
  private ProgramProductService service;

  @RequestMapping(value = "/rest-api/program-products", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<RestResponse> getProgramProductsBy(@RequestParam String programCode,
                                                           @RequestParam(required = false) String facilityTypeCode) {
    try {
      final List<ProgramProductDTO> programProductsDTO = new ArrayList<>();
      List<ProgramProduct> programProducts = service.getProgramProductsBy(programCode, facilityTypeCode);
      forAllDo(programProducts, new Closure() {
        @Override
        public void execute(Object o) {
          programProductsDTO.add(new ProgramProductDTO((ProgramProduct) o));
        }
      });
      return response(PROGRAM_PRODUCT_LIST, programProductsDTO);
    } catch (DataException de) {
      return error(de.getOpenLmisMessage(), BAD_REQUEST);
    }
  }
}
