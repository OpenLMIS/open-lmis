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
import org.openlmis.report.mapper.lookup.GeographicZoneReportMapper;
import org.openlmis.report.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/gis/")
public class GeoDataController extends BaseController {

  public static final String USER_ID = "USER_ID";

  @Autowired
  private GeographicZoneReportMapper geographicZoneReportMapper;

  @RequestMapping(value="/reporting-rate", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getReportingRateReport(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                 @RequestParam(value = "period", required = true, defaultValue = "0") Long period){
      return OpenLmisResponse.response("map", this.geographicZoneReportMapper.getGeoReportingRate(program, period));
  }


  @RequestMapping(value="/reporting-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getReportingFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                 @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                 @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
                                                                 ){

      return OpenLmisResponse.response("facilities", this.geographicZoneReportMapper.getReportingFacilities(program, geoZoneId , period));
    }



  @RequestMapping(value="/non-reporting-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getNonReportingFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                 @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                 @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
  ){
     return OpenLmisResponse.response("facilities", this.geographicZoneReportMapper.getNonReportingFacilities(program, geoZoneId , period));
  }


    @RequestMapping(value="/stock-status-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockStatusSummaryFacilityReport(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                   @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                   @RequestParam(value = "product", required = true, defaultValue = "0") Long product) {
        return OpenLmisResponse.response("map", this.geographicZoneReportMapper.getGeoStockStatusFacilitySummary(program, period, product));
    }


    @RequestMapping(value="/stocked-out-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockedOutFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                      @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                      @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                      @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("facilities", this.geographicZoneReportMapper.getStockedOutFacilities(program, geoZoneId , period, product));
    }

    @RequestMapping(value="/under-stocked-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getUnderStockedFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                    @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                    @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                    @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("facilities", this.geographicZoneReportMapper.getUnderStockedFacilities(program, geoZoneId , period, product));
    }

    @RequestMapping(value="/over-stocked-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getOverStockedFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                      @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                      @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                      @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("facilities", this.geographicZoneReportMapper.getOverStockedFacilities(program, geoZoneId , period, product));
    }

    @RequestMapping(value="/adequately-stocked-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAdequatelyStockedFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                      @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                      @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                      @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("facilities", this.geographicZoneReportMapper.getAdequatelyStockedFacilities(program, geoZoneId , period, product));
    }

    @RequestMapping(value="/stock-status-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockStatusProductReport(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                        @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                        @RequestParam(value = "zone", required = true, defaultValue = "0") Long geoZoneId) {
        return OpenLmisResponse.response("products", this.geographicZoneReportMapper.getStockStatusProductSummary(program, geoZoneId, period));
    }


    @RequestMapping(value="/stocked-out-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockedOutProducts(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                         @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                         @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                         @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("products", this.geographicZoneReportMapper.getStockedOutProducts(program, geoZoneId , period, product));
    }

    @RequestMapping(value="/under-stocked-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getUnderStockedProducts(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                  @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                  @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                  @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("products", this.geographicZoneReportMapper.getUnderStockedProducts(program, geoZoneId , period, product));
    }


    @RequestMapping(value="/over-stocked-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getOverStockedProducts(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                    @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                    @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                    @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("products", this.geographicZoneReportMapper.getOverStockedProducts(program, geoZoneId , period, product));
    }

    @RequestMapping(value="/adequately-stocked-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAdequatelyStockedProducts(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                         @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                         @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                         @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("products", this.geographicZoneReportMapper.getAdequatelyStockedProducts(program, geoZoneId , period, product));
    }

    //Lab equipment status by location
    @RequestMapping(value="/facilitiesEquipments", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitiesLabEquipmentStatus(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                            @RequestParam(value = "zone", required = true, defaultValue = "0") Long zone,
                                                                            @RequestParam(value = "facilityType", required = true, defaultValue = "0") Long facilityType,
                                                                            @RequestParam(value = "facility", required = true, defaultValue = "0") Long facility,
                                                                            @RequestParam(value = "equipmentType", required = true, defaultValue = "0") Long equipmentType,
                                                                            @RequestParam(value = "equipment", required = true, defaultValue = "0") Long equipment,
                                                                            HttpServletRequest request
    ){
        Long userId = loggedInUserId(request);
        return  OpenLmisResponse.response("equipmentsStatus",
                this.geographicZoneReportMapper.getFacilitiesEquipments(program, zone,  facilityType, facility, equipmentType, userId, equipment));
    }

    @RequestMapping(value="/facilitiesEquipmentsStatusGeo2", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitiesLabEquipmentStatus2(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                             @RequestParam(value = "zone", required = true, defaultValue = "0") Long zone,
                                                                             @RequestParam(value = "facilityType", required = true, defaultValue = "0") Long facilityType,
                                                                             @RequestParam(value = "facility", required = true, defaultValue = "0") Long facility,
                                                                             @RequestParam(value = "equipmentType", required = true, defaultValue = "0") Long equipmentType,
                                                                             @RequestParam(value = "equipment", required = true, defaultValue = "0") Long equipment,
                                                                             HttpServletRequest request

    ){
        Long userId = loggedInUserId(request);
        return  OpenLmisResponse.response("equipmentsStatus", this.geographicZoneReportMapper
                .getFacilityEquipmentStatusGeo2(program, zone,  facilityType, facility, equipmentType, userId, equipment));
    }

    @RequestMapping(value="/facilitiesByEquipmentOperationalStatus", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitiesByEquipmentOperationalStatus(@RequestParam(value = "status", required = true, defaultValue = "") String status,
                                                                                      @RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                                      @RequestParam(value = "zone", required = true, defaultValue = "0") Long zone,
                                                                                      @RequestParam(value = "facilityType", required = true, defaultValue = "0") Long facilityType,
                                                                                      @RequestParam(value = "facility", required = true, defaultValue = "0") Long facility,
                                                                                      @RequestParam(value = "equipmentType", required = true, defaultValue = "0") Long equipmentType,
                                                                                      @RequestParam(value = "equipment", required = true, defaultValue = "0") Long equipment,
                                                                                      HttpServletRequest request
    ){
        Long userId = loggedInUserId(request);
        return  OpenLmisResponse.response("equipmentsStatus", this.geographicZoneReportMapper
                .getFacilitiesByEquipmentOperationalStatus(program, zone,  facilityType, facility, equipmentType, userId, status, equipment));
    }

    @RequestMapping(value="/facilitiesEquipmentStatusSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitiesEquipmentStatusSummary(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                                @RequestParam(value = "zone", required = true, defaultValue = "0") Long zone,
                                                                                @RequestParam(value = "facilityType", required = true, defaultValue = "0") Long facilityType,
                                                                                @RequestParam(value = "facility", required = true, defaultValue = "0") Long facility,
                                                                                @RequestParam(value = "equipmentType", required = true, defaultValue = "0") Long equipmentType,
                                                                                @RequestParam(value = "equipment", required = true, defaultValue = "0") Long equipment,
                                                                                HttpServletRequest request
    ){
        Long userId = loggedInUserId(request);
        return  OpenLmisResponse.response("equipmentsStatusSummary", this.geographicZoneReportMapper
                .getFacilitiesEquipmentStatusSummary(program, zone,  facilityType, facility, equipmentType, userId, equipment));
    }
}
