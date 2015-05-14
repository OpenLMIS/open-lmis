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
import org.openlmis.report.mapper.lookup.RMNCHStatusReportMapper;
import org.openlmis.report.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/rmnch/")
public class RMNCHDataController extends BaseController {

  public static final String USER_ID = "USER_ID";

 public static String  getCommaSeparatedIds(List<Long> idList){

     return idList == null ? "{}" : idList.toString().replace("[", "").replace("]", "");
 }

  @Autowired
  private RMNCHStatusReportMapper rmnchStatusReportMapper;


    @RequestMapping(value="/stock-status-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockStatusSummaryFacilityReport(@RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                   @RequestParam(value = "product", required = true, defaultValue = "0") Long product) {
        return OpenLmisResponse.response("map", this.rmnchStatusReportMapper.getGeoStockStatusFacilitySummary(period, product));
    }


    @RequestMapping(value="/stocked-out-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockedOutFacilities(@RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                      @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                      @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("facilities", this.rmnchStatusReportMapper.getStockedOutFacilities(geoZoneId , period, product));
    }

    @RequestMapping(value="/under-stocked-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getUnderStockedFacilities(@RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                    @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                    @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("facilities", this.rmnchStatusReportMapper.getUnderStockedFacilities(geoZoneId , period, product));
    }

    @RequestMapping(value="/over-stocked-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getOverStockedFacilities(@RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                      @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                      @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("facilities", this.rmnchStatusReportMapper.getOverStockedFacilities(geoZoneId , period, product));
    }

    @RequestMapping(value="/adequately-stocked-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAdequatelyStockedFacilities(@RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                      @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                      @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("facilities", this.rmnchStatusReportMapper.getAdequatelyStockedFacilities(geoZoneId , period, product));
    }

    @RequestMapping(value="/stock-status-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockStatusProductReport(@RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                        @RequestParam(value = "zone", required = true, defaultValue = "0") Long geoZoneId) {
        return OpenLmisResponse.response("products", this.rmnchStatusReportMapper.getStockStatusProductSummary(geoZoneId, period));
    }


    @RequestMapping(value="/stocked-out-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockedOutProducts( @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                         @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                         @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("products", this.rmnchStatusReportMapper.getStockedOutProducts(geoZoneId , period, product));
    }

    @RequestMapping(value="/under-stocked-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getUnderStockedProducts(@RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                  @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                  @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("products", this.rmnchStatusReportMapper.getUnderStockedProducts(geoZoneId , period, product));
    }


    @RequestMapping(value="/over-stocked-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getOverStockedProducts(@RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                    @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                    @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("products", this.rmnchStatusReportMapper.getOverStockedProducts(geoZoneId , period, product));
    }

    @RequestMapping(value="/adequately-stocked-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAdequatelyStockedProducts(@RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                         @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                         @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("products", this.rmnchStatusReportMapper.getAdequatelyStockedProducts(geoZoneId , period, product));
    }

    @RequestMapping(value="/geo-zone-geometry", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getGeoZoneGeometryJson(){

        return OpenLmisResponse.response("geoZone", this.rmnchStatusReportMapper.getGeoZoneGeometryJson());
    }
}
