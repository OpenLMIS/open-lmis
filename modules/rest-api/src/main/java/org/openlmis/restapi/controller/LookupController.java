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
import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.*;
import org.openlmis.report.model.dto.Facility;
import org.openlmis.report.model.dto.FacilityType;
import org.openlmis.report.model.dto.Program;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static org.openlmis.restapi.response.RestResponse.error;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Controller
@NoArgsConstructor
@Api(value = "Lookups", description = "Returns shared Lookup data", position = 1)
public class LookupController {

  public static final String ACCEPT_JSON = "Accept=application/json";
  public static final String UNEXPECTED_EXCEPTION = "unexpected.exception";
  public static final String FORBIDDEN_EXCEPTION = "forbidden.exception";

  @Autowired
  private ReportLookupService lookupService;

  @ApiOperation(value = "Product Categories", notes = "Returns a list of product categories", response = ProductCategory.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = ProductCategory.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/product-categories", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getProductCategories(Principal principal) {
    return RestResponse.response("product-categories", lookupService.getAllProductCategories());
  }

  @ApiOperation(value = "Products", notes = "Returns a list of products.", response = Product.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = Product.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/products", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getProducts(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                    @RequestParam(value = "paging", defaultValue = "true") Boolean paging,
                                    Principal principal) {
    RowBounds rowBounds = paging ? new RowBounds(page, pageSize) : new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    return RestResponse.response("products", lookupService.getFullProductList(rowBounds));
  }


  @ApiOperation(value = "Product Detail by Code", notes = "Returns details of a product", response = Product.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = Product.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/product/{code}", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getProductByCode(Principal principal, @PathVariable("code") String code) {
    return RestResponse.response("product", lookupService.getProductByCode(code));
  }

  @ApiOperation(value = "Dosage Units", notes = "Returns a list of Dosage Units.", response = DosageUnit.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = DosageUnit.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/dosage-units", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getDosageUnits(Principal principal) {
    return RestResponse.response("dosage-units", lookupService.getDosageUnits());
  }

  @ApiOperation(value = "Facility Types", notes = "List of Facility Types.", response = FacilityType.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = FacilityType.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/facility-types", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getFacilityTypes(Principal principal) {
    return RestResponse.response("facility-types", lookupService.getAllFacilityTypes());
  }

  @ApiOperation(value = "Facilities", notes = "Returns a list of facilities.", response = Facility.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = Facility.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/facilities", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getFacilities(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, @RequestParam(value = "paging", defaultValue = "true") Boolean paging, Principal principal) {
    RowBounds rowBounds = paging ? new RowBounds(page, pageSize) : new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    return RestResponse.response("facilities", lookupService.getAllFacilities(rowBounds));
  }

  @ApiOperation(value = "Facility Detail by Code", notes = "Returns Facility Detail by Code", response = Facility.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = Facility.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/facility/{code}", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getFacilityByCode(Principal principal, @PathVariable("code") String code) {
    return RestResponse.response("facility", lookupService.getFacilityByCode(code));
  }


  @ApiOperation(value = "Programs", notes = "Returns a list of Programs.", response = Program.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = Program.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/programs", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getPrograms(Principal principal) {
    return RestResponse.response("programs", lookupService.getAllPrograms());
  }

  @ApiOperation(value = "Program Products", notes = "Returns a complete list of Products supported by Program.", response = ProgramProduct.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = ProgramProduct.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/program-products", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getProgramProducts(Principal principal) {
    return RestResponse.response("program-products", lookupService.getAllProgramProducts());
  }

  @ApiOperation(value = "Facility Type Approved Products", notes = "Returns a complete list of Facility type supported by Program.", response = FacilityTypeApprovedProduct.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = FacilityTypeApprovedProduct.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/facility-approved-products", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getFacilityApprovedProducts(Principal principal) {
    return RestResponse.response("facility-approved-products", lookupService.getAllFacilityTypeApprovedProducts());
  }

  @ApiOperation(value = "Program detail By Code", notes = "Returns program detail by code", response = Program.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = Program.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/program/{code}", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getProgramByCode(Principal principal, @PathVariable("code") String code) {
    return RestResponse.response("program", lookupService.getProgramByCode(code));
  }

  @ApiOperation(value = "Loss and Adjustment Types", notes = "Returns loss and adjustment types", response = LossesAndAdjustmentsType.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = LossesAndAdjustmentsType.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/losses-adjustments-types", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getLossesAdjustmentsTypes(Principal principal) {
    return RestResponse.response("losses-adjustments-types", lookupService.getAllAdjustmentTypes());
  }

  @ApiOperation(value = "Processing Periods", notes = "Returns all processing periods", response = org.openlmis.report.model.dto.ProcessingPeriod.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = org.openlmis.report.model.dto.ProcessingPeriod.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/processing-periods", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getProcessingPeriods(Principal principal) {
    return RestResponse.response("processing-periods", lookupService.getAllProcessingPeriods());
  }


  @ApiOperation(value = "Processing Schedules", notes = "Returns list of processing schedule groups", response = ProcessingSchedule.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = ProcessingSchedule.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/processing-schedules", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getProcessingSchedules(Principal principal) {
    return RestResponse.response("processing-schedules", lookupService.getAllProcessingSchedules());
  }

  @ApiOperation(value = "Geographic Zones", notes = "Returns list of geographic zones", response = GeographicZone.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = GeographicZone.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/geographic-zones", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getGeographicZones(Principal principal) {
    return RestResponse.response("geographic-zones", lookupService.getAllZones());
  }



    @ApiOperation(value = "Geographic Levels", notes = "Returns list of geographic levels", response = GeographicLevel.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful request", response = GeographicLevel.class),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = "/rest-api/lookup/geographic-levels", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity getGeographicLevels( Principal principal) {
        return RestResponse.response("geographic-levels", lookupService.getAllGeographicLevels());
    }



    @ApiOperation(value = "Regimens", notes = "Returns list of regimens", response = Regimen.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful request", response = Regimen.class),
    @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/regimens", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getRegimens(Principal principal) {
    return RestResponse.response("regimens", lookupService.getAllRegimens());
  }


    @ApiOperation(value = "Regimen Categories", notes = "Returns list of regimen categories", response = RegimenCategory.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful request", response = RegimenCategory.class),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = "/rest-api/lookup/regimen-categories", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity getRegimenCategories(Principal principal) {
        return RestResponse.response("regimen-categories", lookupService.getAllRegimenCategories());
    }


    @ApiOperation(value = "Dosage Frequencies", notes = "Returns list of dosage frequencies", response = DosageFrequency.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful request", response = DosageFrequency.class),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = "/rest-api/lookup/dosage-frequencies", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity getDosageFrequencies(Principal principal) {
        return RestResponse.response("dosage-frequencies", lookupService.getAllDosageFrequencies());
    }



    @ApiOperation(value = "Regimen Product Combinations", notes = "Returns list of regimen product combinations", response = RegimenProductCombination.class)
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Successful request", response = RegimenProductCombination.class),
          @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/regimen-product-combinations", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getRegimenProductCombinations(Principal principal) {
       return RestResponse.response("regimen-product-combinations", lookupService.getAllRegimenProductCombinations());
  }


 @ApiOperation(value = "Regimen Combination Constituents", notes = "Returns list of regimen combination constituents", response = RegimenCombinationConstituent.class)
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Successful request", response = RegimenCombinationConstituent.class),
          @ApiResponse(code = 500, message = "Internal server error")}
  )
  @RequestMapping(value = "/rest-api/lookup/regimen-combination-constituents", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getRegimenCombinationConstituents(Principal principal) {
      return RestResponse.response("regimen-combination-constituents", lookupService.getAllRegimenCombinationConstituents());
  }


  @ApiOperation(value = "Regimen Constituents' Dosages", notes = "Returns list of dosages for regimen constituents", response = RegimenConstituentDosage.class)
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Successful request", response = RegimenConstituentDosage.class),
          @ApiResponse(code = 500, message = "Internal server error")}
   )
  @RequestMapping(value = "/rest-api/lookup/regimen-constituent-dosages", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity getRegimenConstituentDosages(Principal principal) {
      return RestResponse.response("regimen-constituent-dosages", lookupService.getAllRegimenConstituentDosages());
  }



  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestResponse> handleException(Exception ex) {
    if (ex instanceof AccessDeniedException) {
      return error(FORBIDDEN_EXCEPTION, FORBIDDEN);
    }
    return error(UNEXPECTED_EXCEPTION, INTERNAL_SERVER_ERROR);
  }

}
