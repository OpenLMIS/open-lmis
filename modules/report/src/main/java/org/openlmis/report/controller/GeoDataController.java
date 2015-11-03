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
import org.openlmis.report.mapper.lookup.GeographicZoneReportMapper;
import org.openlmis.report.model.geo.GeoFacilityIndicator;
import org.openlmis.report.service.GeographicReportProvider;
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
@RequestMapping(value = "/gis/")
public class GeoDataController extends BaseController {

    public static final String USER_ID = "USER_ID";
    public static final String MAP = "map";
    public static final String FACILITIES = "facilities";
    public static final String PRODUCTS = "products";
    public static final String EQUIPMENTS_STATUS = "equipmentsStatus";
    public static final String GEO_ZONE = "geoZone";
    public static final String CONSUMPTION = "consumption";
    public static final String EQUIPMENTS_STATUS_SUMMARY = "equipmentsStatusSummary";

    @Autowired
    private GeographicReportProvider geographicReportProvider;
    @Autowired
    private GeographicZoneReportMapper geographicZoneReportMapper;

    public static String getCommaSeparatedIds(List<Long> idList) {
        return idList == null ? "{}" : idList.toString().replace("[", "").replace("]", "");
    }


    @RequestMapping(value = "/reporting-rate", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getReportingRateReport(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                   @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                   @RequestParam(value = "schedule", required = true, defaultValue = "0") Long schedule,
                                                                   HttpServletRequest request) {
        Long userId = loggedInUserId(request);

        return OpenLmisResponse.response(MAP, this.geographicZoneReportMapper.getGeoReportingRate(userId, program,schedule, period));
    }


    @RequestMapping(value = "/reporting-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getReportingFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                   @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                   @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId,
             HttpServletRequest request
    ) {
        Long userId = loggedInUserId(request);
        List<GeoFacilityIndicator> facilities= this.geographicZoneReportMapper.getReportingFacilities(program, geoZoneId, period, userId);
        return OpenLmisResponse.response(FACILITIES, facilities);
    }


    @RequestMapping(value = "/non-reporting-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getNonReportingFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                      @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                      @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId,
                                                                      @RequestParam(value = "schedule", required = true, defaultValue = "0") Long schedule,
                                                                      HttpServletRequest request
    ) {
        Long userId = loggedInUserId(request);

        return OpenLmisResponse.response(FACILITIES, this.geographicZoneReportMapper.getNonReportingFacilities(program, geoZoneId, period,schedule, userId));
    }


    @RequestMapping(value = "/stock-status-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockStatusSummaryFacilityReport(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                                @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                                @RequestParam(value = "product", required = true, defaultValue = "0") Long product) {
        return OpenLmisResponse.response(MAP, this.geographicZoneReportMapper.getGeoStockStatusFacilitySummary(program, period, product));
    }


    @RequestMapping(value = "/stocked-out-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockedOutFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                    @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                    @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                    @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ) {
        return OpenLmisResponse.response(FACILITIES, this.geographicZoneReportMapper.getStockedOutFacilities(program, geoZoneId, period, product));
    }

    @RequestMapping(value = "/under-stocked-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getUnderStockedFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                      @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                      @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                      @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ) {
        return OpenLmisResponse.response(FACILITIES, this.geographicZoneReportMapper.getUnderStockedFacilities(program, geoZoneId, period, product));
    }

    @RequestMapping(value = "/over-stocked-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getOverStockedFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                     @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                     @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                     @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ) {
        return OpenLmisResponse.response(FACILITIES, this.geographicZoneReportMapper.getOverStockedFacilities(program, geoZoneId, period, product));
    }

    @RequestMapping(value = "/adequately-stocked-facilities", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAdequatelyStockedFacilities(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                           @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                           @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                           @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ) {
        return OpenLmisResponse.response(FACILITIES, this.geographicZoneReportMapper.getAdequatelyStockedFacilities(program, geoZoneId, period, product));
    }

    @RequestMapping(value = "/stock-status-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockStatusProductReport(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                        @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                        @RequestParam(value = "zone", required = true, defaultValue = "0") Long geoZoneId) {
        return OpenLmisResponse.response(PRODUCTS, this.geographicZoneReportMapper.getStockStatusProductSummary(program, geoZoneId, period));
    }


    @RequestMapping(value = "/stocked-out-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockedOutProducts(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                  @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                  @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                  @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ) {
        return OpenLmisResponse.response(PRODUCTS, this.geographicZoneReportMapper.getStockedOutProducts(program, geoZoneId, period, product));
    }

    @RequestMapping(value = "/under-stocked-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getUnderStockedProducts(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                    @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                    @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                    @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ) {
        return OpenLmisResponse.response(PRODUCTS, this.geographicZoneReportMapper.getUnderStockedProducts(program, geoZoneId, period, product));
    }


    @RequestMapping(value = "/over-stocked-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getOverStockedProducts(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                   @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                   @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                   @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ) {
        return OpenLmisResponse.response(PRODUCTS, this.geographicZoneReportMapper.getOverStockedProducts(program, geoZoneId, period, product));
    }

    @RequestMapping(value = "/adequately-stocked-products", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAdequatelyStockedProducts(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                         @RequestParam(value = "period", required = true, defaultValue = "0") Long period,
                                                                         @RequestParam(value = "product", required = true, defaultValue = "0") Long product,
                                                                         @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ) {
        return OpenLmisResponse.response(PRODUCTS, this.geographicZoneReportMapper.getAdequatelyStockedProducts(program, geoZoneId, period, product));
    }


    //Lab equipment status by location
    @RequestMapping(value = "/facilitiesEquipments", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitiesLabEquipmentStatus(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                            @RequestParam(value = "zone", required = true, defaultValue = "0") Long zone,
                                                                            @RequestParam(value = "facilityType", required = true, defaultValue = "0") Long facilityType,
                                                                            @RequestParam(value = "facility", required = true, defaultValue = "0") Long facility,
                                                                            @RequestParam(value = "equipmentType", required = true, defaultValue = "0") Long equipmentType,
                                                                            @RequestParam(value = "equipment", required = true, defaultValue = "0") Long equipment,
                                                                            HttpServletRequest request
    ) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response(EQUIPMENTS_STATUS,
                this.geographicZoneReportMapper.getFacilitiesEquipments(program, zone, facilityType, facility, equipmentType, userId, equipment));
    }

    @RequestMapping(value = "/facilitiesEquipmentsStatusGeo", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitiesLabEquipmentStatus2(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                             @RequestParam(value = "zone", required = true, defaultValue = "0") Long zone,
                                                                             @RequestParam(value = "facilityType", required = true, defaultValue = "0") Long facilityType,
                                                                             @RequestParam(value = "facility", required = true, defaultValue = "0") Long facility,
                                                                             @RequestParam(value = "equipmentType", required = true, defaultValue = "0") Long equipmentType,
                                                                             @RequestParam(value = "equipment", required = true, defaultValue = "0") Long equipment,
                                                                             HttpServletRequest request

    ) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response(EQUIPMENTS_STATUS, this.geographicZoneReportMapper
                .getFacilityEquipmentStatusGeo(program, zone, facilityType, facility, equipmentType, userId, equipment));
    }

    @RequestMapping(value = "/facilitiesByEquipmentOperationalStatus", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitiesByEquipmentOperationalStatus(@RequestParam(value = "status", required = true, defaultValue = "") String status,
                                                                                      @RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                                      @RequestParam(value = "zone", required = true, defaultValue = "0") Long zone,
                                                                                      @RequestParam(value = "facilityType", required = true, defaultValue = "0") Long facilityType,
                                                                                      @RequestParam(value = "facility", required = true, defaultValue = "0") Long facility,
                                                                                      @RequestParam(value = "equipmentType", required = true, defaultValue = "0") Long equipmentType,
                                                                                      @RequestParam(value = "equipment", required = true, defaultValue = "0") Long equipment,
                                                                                      HttpServletRequest request
    ) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response(EQUIPMENTS_STATUS, this.geographicZoneReportMapper
                .getFacilitiesByEquipmentOperationalStatus(program, zone, facilityType, facility, equipmentType, userId, status, equipment));
    }

    @RequestMapping(value = "/facilitiesEquipmentStatusSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitiesEquipmentStatusSummary(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                                @RequestParam(value = "zone", required = true, defaultValue = "0") Long zone,
                                                                                @RequestParam(value = "facilityType", required = true, defaultValue = "0") Long facilityType,
                                                                                @RequestParam(value = "facility", required = true, defaultValue = "0") Long facility,
                                                                                @RequestParam(value = "equipmentType", required = true, defaultValue = "0") Long equipmentType,
                                                                                @RequestParam(value = "equipment", required = true, defaultValue = "0") Long equipment,
                                                                                HttpServletRequest request
    ) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response(EQUIPMENTS_STATUS_SUMMARY, this.geographicZoneReportMapper
                .getFacilitiesEquipmentStatusSummary(program, zone, facilityType, facility, equipmentType, userId, equipment));
    }

    @RequestMapping(value = "/stock-status-product-consumption", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockStatusProductConsumption(@RequestParam(value = "program", required = true, defaultValue = "0") Long program,
                                                                             @RequestParam(value = "product", required = true, defaultValue = "0") List<Long> productListId,
                                                                             @RequestParam(value = "period", required = true, defaultValue = "0") Long periodId,
                                                                             @RequestParam(value = "geo_zone", required = true, defaultValue = "0") Long geoZoneId
    ) {
        return OpenLmisResponse.response(CONSUMPTION, this.geographicZoneReportMapper.getStockStatusProductConsumption(program, periodId, geoZoneId, getCommaSeparatedIds(productListId)));
    }

    @RequestMapping(value = "/geo-zone-geometry", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getGeoZoneGeometryJson() {

        return OpenLmisResponse.response(GEO_ZONE, this.geographicZoneReportMapper.getGeoZoneGeometryJson());
    }
}
