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
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.report.model.DashboardLookUpReportHeader;
import org.openlmis.report.model.dto.Notification;
import org.openlmis.report.service.DashboardBatchDataUpdateExecutorService;
import org.openlmis.report.service.lookup.DashboardLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/dashboard")
public class DashboardController extends BaseController {

    public static final String ITEM_FILL_RATE = "itemFillRate";
    public static final String ORDER_FILL_RATE = "orderFillRate";
    public static final String SHIPMENT_LEAD_TIME = "leadTime";
    public static final String STOCKING_EFFICIENCY_STATICS = "stocking";
    public static final String STOCKING_EFFICIENCY_DETAIL = "stocking";
    public static final String STOCKED_OUT_FACILITIES = "stockOut";
    public static final String ALERTS = "alerts";
    public static final String NOTIFICATIONS = "notifications";
    public static final String NOTIFICATIONS_DETAIL = "detail";
    public static final String RNR_STATUS_SUMMARY = "rnrStatusSummary";
    public static final String REPORTING_PERFORMANCE = "reportingPerformance";
    public static final String REPORTING_DETAILS = "reporting";
    public static final String RNR_STATUS_DETAILS = "rnrDetails";
    private static final String RNR_STATUS_BY_REQUISITION_GROUP = "rnrStatus";
    private static final String EMERGENCY_RNR_STATUS_SUMMARY = "emergencyRnrStatus";
    private static  final String EXTRA_ANALYTICS_DATA_FOR_RNR_SUMMARY = "analyticsData";
    @Autowired
    DashboardLookupService lookupService;

    @Autowired
    DashboardBatchDataUpdateExecutorService dashboardUpdateService;

    @Autowired
    MessageService messageService;

    @RequestMapping(value = "/itemFillRate", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getItemFillRate(@RequestParam("periodId") Long periodId,
                                                            @RequestParam("facilityId") Long facilityId,
                                                            @RequestParam("programId") Long programId,
                                                            @RequestParam("productListId") List<Long> productListId) {
        return OpenLmisResponse.response(ITEM_FILL_RATE, this.lookupService.getItemFillRate(periodId, facilityId, programId, productListId));
    }

    @RequestMapping(value = "/orderFillRate", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getOrderFillRate(@RequestParam("periodId") Long periodId,
                                                             @RequestParam("facilityId") Long facilityId,
                                                             @RequestParam("programId") Long programId) {
        return OpenLmisResponse.response(ORDER_FILL_RATE, this.lookupService.getOrderFillRate(periodId, facilityId, programId));
    }

    @RequestMapping(value = "/shipmentLeadTime", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getShipmentLeadTime(@RequestParam("periodId") Long periodId,
                                                                @RequestParam("programId") Long programId,
                                                                @RequestParam("zoneId") Long zoneId,
                                                                HttpServletRequest request) {
        return OpenLmisResponse.response(SHIPMENT_LEAD_TIME, this.lookupService.getShipmentLeadTime(loggedInUserId(request), periodId, programId, zoneId));
    }


    @RequestMapping(value = "/stockEfficiency", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockEfficiencyData(@RequestParam("periodId") Long periodId,
                                                                   @RequestParam("programId") Long programId,
                                                                   @RequestParam("zoneId") Long zoneId,
                                                                   @RequestParam("productListId") List<Long> productListId,
                                                                   HttpServletRequest request) {
        return OpenLmisResponse.response(STOCKING_EFFICIENCY_STATICS, this.lookupService.getStockEfficiencyData(loggedInUserId(request), periodId, programId, zoneId, productListId));
    }

    @RequestMapping(value = "/stockEfficiencyDetail", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockEfficiencyDetailData(@RequestParam("periodId") Long periodId,
                                                                         @RequestParam("programId") Long programId,
                                                                         @RequestParam("zoneId") Long zoneId,
                                                                         @RequestParam("productListId") List<Long> productListId,
                                                                         HttpServletRequest request) {
        return OpenLmisResponse.response(STOCKING_EFFICIENCY_DETAIL, this.lookupService.getStockEfficiencyDetailData(loggedInUserId(request), periodId, programId, zoneId, productListId));
    }

    @RequestMapping(value = "/stockedOutFacilities", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockedOutFacilities(@RequestParam("periodId") Long periodId,
                                                                    @RequestParam("programId") Long programId,
                                                                    @RequestParam("productId") Long productId,
                                                                    @RequestParam("zoneId") Long zoneId,
                                                                    HttpServletRequest request) {
        return OpenLmisResponse.response(STOCKED_OUT_FACILITIES, this.lookupService.getStockOutFacilities(loggedInUserId(request), periodId, programId, productId, zoneId));
    }

    @RequestMapping(value = "/geographic-zone/{zoneId}/program/{programId}/period/{periodId}/product/{productId}/stockedOutFacilities", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockedOutFacilitiesByGeographicZoneFilter(@PathVariable("periodId") Long periodId,
                                                                                          @PathVariable("programId") Long programId,
                                                                                          @PathVariable("productId") Long productId,
                                                                                          @PathVariable("zoneId") Long zoneId,
                                                                                          HttpServletRequest request) {
        return OpenLmisResponse.response(STOCKED_OUT_FACILITIES, this.lookupService.getStockOutFacilitiesByGeographicZoneFilter(loggedInUserId(request), periodId, programId, productId, zoneId));
    }

    @RequestMapping(value = "/alerts", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAlerts(@RequestParam("zoneId") Long zoneId, @RequestParam("programId") Long programId,
                                                      @RequestParam("periodId") Long periodId,
                                                      HttpServletRequest request) {
        return OpenLmisResponse.response(ALERTS, this.lookupService.getAlerts(loggedInUserId(request), programId, periodId, zoneId));
    }

    @RequestMapping(value = "/stocked-out/alerts", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockedOutAlerts(@RequestParam("zoneId") Long zoneId, @RequestParam("programId") Long programId,
                                                                @RequestParam("periodId") Long periodId,
                                                                HttpServletRequest request) {
        return OpenLmisResponse.response(ALERTS, this.lookupService.getStockedOutAlerts(loggedInUserId(request), programId, periodId, zoneId));
    }

    @RequestMapping(value = "/notification/alerts", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getNotificationTypeAlerts(HttpServletRequest request) {
        return OpenLmisResponse.response(NOTIFICATIONS, this.lookupService.getNotificationAlerts());
    }

    @RequestMapping(value = "/notifications/{programId}/{periodId}/{zoneId}/{detailTable}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getNotificationsByCategory(@PathVariable("programId") Long programId, @PathVariable("periodId") Long periodId,
                                                                       @PathVariable("zoneId") Long zoneId,
                                                                       @PathVariable("detailTable") String detailTable,
                                                                       HttpServletRequest request) {
        return OpenLmisResponse.response(NOTIFICATIONS_DETAIL, this.lookupService.getNotificationsByCategory(loggedInUserId(request), programId, periodId, zoneId, detailTable));
    }

    @RequestMapping(value = "/notifications/{programId}/{periodId}/{zoneId}/{productId}/{detailTable}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getNotificationsDetail(@PathVariable("programId") Long programId, @PathVariable("periodId") Long periodId,
                                                                   @PathVariable("zoneId") Long zoneId, @PathVariable("productId") Long productId,
                                                                   @PathVariable("detailTable") String detailTable,
                                                                   HttpServletRequest request) {
        return OpenLmisResponse.response(NOTIFICATIONS_DETAIL, this.lookupService.getStockedOutNotificationDetails(loggedInUserId(request), programId, periodId, zoneId, productId, detailTable));
    }

    @RequestMapping(value = "/notification/send", method = POST, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> sendNotification(@RequestBody Notification notification, HttpServletRequest request) {
        try {
            this.lookupService.sendNotification(notification);

            return OpenLmisResponse.success(messageService.message("send.notification.success"));

        } catch (Exception e) {
            return OpenLmisResponse.success(messageService.message("send.notification.error"));
        }
    }

    @RequestMapping(value = "/period/{id}", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getYearOfPeriodById(@PathVariable("id") Long id) {
        return OpenLmisResponse.response("year", this.lookupService.getYearOfPeriodById(id));
    }

    @RequestMapping(value = "/productDetail/{id}/{periodId}", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getProductById(@PathVariable("id") Long id,@PathVariable("periodId") Long periodId) {
        DashboardLookUpReportHeader productName = this.lookupService.getProductNameById(id,periodId);
        return OpenLmisResponse.response("product_name", productName);
    }

    @RequestMapping(value = "/rnrstatusSummary/requisitionGroup/{requisitionGroupId}", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getRnRStatusSummary(@PathVariable("requisitionGroupId") Long requisitionGroupId) {
        return OpenLmisResponse.response(RNR_STATUS_SUMMARY, this.lookupService.getRnRStatusSummary(requisitionGroupId));

    }

    @RequestMapping(value = "/reportingPerformance", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getReportingPerformance(@RequestParam("periodId") Long periodId,
                                                                    @RequestParam("programId") Long programId,
                                                                    @RequestParam("zoneId") Long zoneId,
                                                                    HttpServletRequest request) {
        return OpenLmisResponse.response(REPORTING_PERFORMANCE, this.lookupService.getReportingPerformance(loggedInUserId(request), periodId, programId, zoneId));
    }

    @RequestMapping(value = "/reportingPerformance-detail", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getReportingPerformanceDetail(@RequestParam("periodId") Long periodId,
                                                                          @RequestParam("programId") Long programId,
                                                                          @RequestParam("zoneId") Long zoneId,
                                                                          @RequestParam("status") String status,
                                                                          HttpServletRequest request) {
        return OpenLmisResponse.response(REPORTING_DETAILS, this.lookupService.getReportingPerformanceDetail(loggedInUserId(request), periodId, programId, zoneId, status));
    }

    @RequestMapping(value = "/requisitionGroup/{requisitionGroupId}/program/{programId}/period/{periodId}/rnrDetails", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getRnRStatusData(
            @PathVariable("requisitionGroupId") Long requisitionGroupId,
            @PathVariable("programId") Long programId,
            @PathVariable("periodId") Long periodId
    ) {
        return OpenLmisResponse.response(RNR_STATUS_DETAILS, this.lookupService.getRnRStatusDetails(requisitionGroupId, programId, periodId));
    }

    @RequestMapping(value = "/RnRStatus/{zoneId}/{periodId}/{programId}/rnrStatus", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getRnRStatusSummary(@PathVariable("zoneId") Long zoneId,
                                                                @PathVariable("periodId") Long periodId,
                                                                @PathVariable("programId") Long programId,
                                                                HttpServletRequest request) {

        return OpenLmisResponse.response(RNR_STATUS_BY_REQUISITION_GROUP, this.lookupService.getRnRStatusSummary(loggedInUserId(request), zoneId, periodId, programId));
    }


    @RequestMapping(value = "/EmergencyRnRStatus/{zoneId}/{periodId}/{programId}/rnrStatus", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getEmergencyRnRStatusSummary(@PathVariable("zoneId") Long zoneId,
                                                                @PathVariable("periodId") Long periodId,
                                                                @PathVariable("programId") Long programId,
                                                                HttpServletRequest request) {

        return OpenLmisResponse.response(EMERGENCY_RNR_STATUS_SUMMARY, this.lookupService.getEmergencyRnRStatusSummary(loggedInUserId(request), zoneId, periodId, programId));
    }

    @RequestMapping(value = "/extraAnalyticsRnRStatus/{zoneId}/{periodId}/{programId}/statusData", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getExtraAnalyticsDataForRnRStatusSummary(@PathVariable("zoneId") Long zoneId,
                                                                         @PathVariable("periodId") Long periodId,
                                                                         @PathVariable("programId") Long programId,
                                                                         HttpServletRequest request) {

        return OpenLmisResponse.response(EXTRA_ANALYTICS_DATA_FOR_RNR_SUMMARY, this.lookupService.getExtraAnalyticsDataForRnRSummary(loggedInUserId(request), zoneId, periodId, programId));
    }

    @RequestMapping(value = "/rnrStatus-detail", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getRnRStatusDetail(@RequestParam("periodId") Long periodId,
                                                               @RequestParam("programId") Long programId,
                                                               @RequestParam("zoneId") Long zoneId,
                                                               @RequestParam("status") String status,
                                                               HttpServletRequest request) {
        return OpenLmisResponse.response(RNR_STATUS_DETAILS, this.lookupService.getRnRStatusDetail(loggedInUserId(request), periodId, programId, zoneId, status));
    }

    @RequestMapping(value = "/sync", method = PUT, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> syncDashboard(HttpServletRequest request) {
        try {
            dashboardUpdateService.startNightlyDashboardDataUpdate();
        } catch (DataException e) {
            return OpenLmisResponse.error(e, BAD_REQUEST);
        }
        return new OpenLmisResponse().response(OK);
    }


    @RequestMapping(value = "/program/{programId}/period/{periodId}/tracer-products-trend.json", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getProgramPeriodTracerProductsTrend(@PathVariable("programId") Long programId, @PathVariable("periodId") Long periodId,
                                                                                @RequestParam(value = "limit", required = false, defaultValue = "4") Long limit,
                                                                                HttpServletRequest request) {

        return OpenLmisResponse.response("tracerProducts", this.lookupService.getProgramPeriodTracerProductsTrend(programId, periodId, loggedInUserId(request), limit));
    }

    @RequestMapping(value = "/program/{programId}/period/{periodId}/reporting-performance.json", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getDashboardReportingPerformance(@PathVariable("programId") Long programId, @PathVariable("periodId") Long periodId,
                                                                                HttpServletRequest request) {

        return OpenLmisResponse.response("reportingPerformance", this.lookupService.getDashboardReportingPerformance(programId, periodId, loggedInUserId(request)));
    }

    @RequestMapping(value = "/program/{programId}/period/{periodId}/district-stock-summary.json", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getDistrictStockSummary(@PathVariable("programId") Long programId, @PathVariable("periodId") Long periodId,
                                                                             HttpServletRequest request) {

        return OpenLmisResponse.response("stockSummary", this.lookupService.getDistrictStockSummary(programId, periodId, loggedInUserId(request)));
    }

    @RequestMapping(value = "/program/{programId}/period/{periodId}/facility-stock-summary.json", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilityStockSummary(@PathVariable("programId") Long programId, @PathVariable("periodId") Long periodId,
                                                                    HttpServletRequest request) {

        return OpenLmisResponse.response("facilityStockSummary", this.lookupService.getFacilityStockSummary(programId, periodId, loggedInUserId(request)));
    }


    @RequestMapping(value = "/program/{programId}/period/{periodId}/product/{productCode}/stocked-out-facilities.json", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitiesStockedOutForProgramPeriodAndProductCode(@PathVariable("programId") Long programId,
                                                                                                  @PathVariable("periodId") Long periodId,
                                                                                                  @PathVariable("productCode") String productCode) {
        return OpenLmisResponse.response("facilities", this.lookupService.getFacilitiesStockedOut(programId, periodId, productCode));
    }

}
