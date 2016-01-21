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

package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.report.mapper.lookup.RMNCHStatusReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return OpenLmisResponse.response("products", this.rmnchStatusReportMapper.getOverStockedProducts(geoZoneId, period, product));
    }

    @RequestMapping(value="/adequately-stocked-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAdequatelyStockedProducts(@RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                         @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                         @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("products", this.rmnchStatusReportMapper.getAdequatelyStockedProducts(geoZoneId, period, product));
    }

    @RequestMapping(value="/stock-status-product-consumption", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockStatusProductConsumption(@RequestParam(value = "product", required = true, defaultValue = "0")List<Long> productListId,
                                                                             @RequestParam(value = "period", required = true, defaultValue = "0") Long periodId,
                                                                             @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ){
        return OpenLmisResponse.response("consumption", this.rmnchStatusReportMapper.getStockStatusProductConsumption(periodId, geoZoneId, getCommaSeparatedIds(productListId)));
    }


    @RequestMapping(value="/geo-zone-geometry", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getGeoZoneGeometryJson(){

        return OpenLmisResponse.response("geoZone", this.rmnchStatusReportMapper.getGeoZoneGeometryJson());
    }
}
