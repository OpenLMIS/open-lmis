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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.service.lookup.DashboardLookupService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.report.controller.DashboardController.*;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
@PrepareForTest(OpenLmisResponse.class)
public class DashboardControllerTest {

    public static final Long userId = 1L;

    @Mock
    DashboardLookupService lookupService;

    @Mock
    MessageService messageService;

    @InjectMocks
    DashboardController dashboardController;

    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

    @Before
    public void setup(){
        MockHttpSession mockHttpSession = new MockHttpSession();
        httpServletRequest.setSession(mockHttpSession);
        mockHttpSession.setAttribute(USER, USER);
        mockHttpSession.setAttribute(USER_ID, userId);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnItemFillRateForSelectedFacilityAndProducts() throws Exception{
        Long programId = 1L, periodId = 1L,facilityId = 1L;
        List<Long> productsId = new ArrayList<>(2);
        productsId.add(1L);
        productsId.add(2L);

        List<ItemFillRate> expectedItemFillRate = new ArrayList<>(1);
        expectedItemFillRate.add(new ItemFillRate(50f, "Product A"));

        when(lookupService.getItemFillRate(periodId, facilityId, programId, productsId)).thenReturn(expectedItemFillRate);

        ResponseEntity<OpenLmisResponse> fetchedItemFillRate = dashboardController.getItemFillRate(periodId,facilityId,programId,productsId);

        verify(lookupService).getItemFillRate(periodId,facilityId,programId,productsId);

        assertThat((List<ItemFillRate>)fetchedItemFillRate.getBody().getData().get(ITEM_FILL_RATE), is(expectedItemFillRate));

    }

    @Test
    public void shouldReturnOrderFillRateForSelectedFacility() throws Exception{
        Long programId = 1L, periodId = 1L,facilityId = 1L;
        OrderFillRate expectedOrderFillRate = new OrderFillRate(45.5f);
        when(lookupService.getOrderFillRate(periodId, facilityId, programId)).thenReturn(expectedOrderFillRate);
        ResponseEntity<OpenLmisResponse> fetchedOrderFillRate = dashboardController.getOrderFillRate(periodId, facilityId, programId);

        verify(lookupService).getOrderFillRate(periodId, facilityId, programId);

        assertThat((OrderFillRate)fetchedOrderFillRate.getBody().getData().get(ORDER_FILL_RATE), is(expectedOrderFillRate));
    }

    @Test
    public void shouldReturnStockEfficiencyStatics() throws Exception{
        List<Long> productIdList = new ArrayList<>();
        List<StockingInfo> expectedStockingInfo = new ArrayList<>(1);
        expectedStockingInfo.add(new StockingInfo());

        when(lookupService.getStockEfficiencyData(userId, 1L, 1L, 1L, productIdList)).thenReturn(expectedStockingInfo);

        ResponseEntity<OpenLmisResponse> fetchedStockingInfoStat = dashboardController.getStockEfficiencyData(1L, 1L, 1L, productIdList, httpServletRequest);

        verify(lookupService).getStockEfficiencyData(userId, 1L, 1L, 1L, productIdList);

        assertThat((List<StockingInfo>) fetchedStockingInfoStat.getBody().getData().get(STOCKING_EFFICIENCY_STATICS), is(expectedStockingInfo));
    }

    @Test
    public void shouldReturnStockEfficiencyDetail() throws Exception{
        List<Long> productIdList = new ArrayList<>();
        List<StockingInfo> expectedStockingDetail = new ArrayList<>(1);
        expectedStockingDetail.add(new StockingInfo());

        when(lookupService.getStockEfficiencyDetailData(userId, 1L, 1L, 1L , productIdList)).thenReturn(expectedStockingDetail);

        ResponseEntity<OpenLmisResponse> fetchedStockingInfoStat = dashboardController.getStockEfficiencyDetailData(1L,1L, 1L,productIdList, httpServletRequest);

        verify(lookupService).getStockEfficiencyDetailData(userId,1L,1L, 1L,productIdList);

        assertThat((List<StockingInfo>) fetchedStockingInfoStat.getBody().getData().get(STOCKING_EFFICIENCY_DETAIL), is(expectedStockingDetail));
    }
    @Test
    public void shouldReturnStockOutFacilities() throws Exception{
        Long programId = 1L, periodId = 1L, productId = 1L, zoneId = 1L;
        List<StockOut> expectedStockedOutFacilityList = new ArrayList<>(1);

        when(lookupService.getStockOutFacilities(userId,periodId,programId,productId,zoneId)).thenReturn(expectedStockedOutFacilityList);

        ResponseEntity<OpenLmisResponse> fetchedStockedOutFacilityList = dashboardController.getStockedOutFacilities(periodId,programId,productId,zoneId, httpServletRequest);

        verify(lookupService).getStockOutFacilities(userId,periodId,programId,productId,zoneId);

        assertThat((List<StockOut>) fetchedStockedOutFacilityList.getBody().getData().get(STOCKED_OUT_FACILITIES), is(expectedStockedOutFacilityList));
    }

    @Test
    public  void shouldReturnAlerts() throws Exception{
        List<AlertSummary> expectedAlertList = new ArrayList<>(1);
        when(lookupService.getAlerts(userId,1L,1L,1L)).thenReturn(expectedAlertList);

        ResponseEntity<OpenLmisResponse> fetchedAlertList = dashboardController.getAlerts(1L,1L,1L,httpServletRequest);
        verify(lookupService).getAlerts(userId,1L,1L,1L);
        assertThat((List<AlertSummary>) fetchedAlertList.getBody().getData().get(ALERTS),is(expectedAlertList));

    }

    @Test
    public void shouldReturnNotificationTypeAlerts() throws Exception{
        List<AlertSummary> expectedNotificationAlerts = new ArrayList<>(2);
        expectedNotificationAlerts.add(new AlertSummary(1L,"10",null,1L,1L,1L,1L,"Muheza","NOTIFICATION","SUMMARY",false,true,null,null,null));
        expectedNotificationAlerts.add(new AlertSummary(2L,"20",null,1L,1L,1L,1L,"Muheza","NOTIFICATION","SUMMARY",false,true,null,null,null));

        when(lookupService.getNotificationAlerts()).thenReturn(expectedNotificationAlerts);

        ResponseEntity<OpenLmisResponse> fetchedNotificationAlertList = dashboardController.getNotificationTypeAlerts(httpServletRequest);

        verify(lookupService).getNotificationAlerts();

        assertThat((List<AlertSummary>) fetchedNotificationAlertList.getBody().getData().get(NOTIFICATIONS), is(expectedNotificationAlerts));
    }

    @Test
    public void shouldReturnNotificationsByCategory() throws Exception{
        String alertFacilityStockOut = "alert_facility_stockout";
        List<HashMap> alertFacilityStockOutList = new ArrayList<>(1);
        when(lookupService.getNotificationsByCategory(userId,1L,1L,1L,alertFacilityStockOut)).thenReturn(alertFacilityStockOutList);

        ResponseEntity<OpenLmisResponse> fetchedNotificationsByCategory = dashboardController.getNotificationsByCategory(1L,1L,1L,alertFacilityStockOut,httpServletRequest);
        verify(lookupService).getNotificationsByCategory(userId,1L,1L,1L,alertFacilityStockOut);
        assertThat((List<HashMap>) fetchedNotificationsByCategory.getBody().getData().get(NOTIFICATIONS_DETAIL), is(alertFacilityStockOutList));
    }

    @Test
    public void shouldSendNotification() throws Exception {
        when(messageService.message("send.notification.success")).thenReturn("Notification is successfully queued for delivery");

        Notification notification = new Notification();

        ResponseEntity<OpenLmisResponse> sendNotificationResponse = dashboardController.sendNotification(notification, httpServletRequest);
        assertThat(sendNotificationResponse.getStatusCode(),is(HttpStatus.OK));
        assertThat(sendNotificationResponse.getBody().getSuccessMsg(),is("Notification is successfully queued for delivery"));
        verify(lookupService).sendNotification(notification);
    }

    @Test
    public void shouldReturnReportingPerformance() throws Exception {
        ReportingStatus expectedReportingPerformance = new ReportingStatus();

        when(lookupService.getReportingPerformance(userId,1L,1L, 1L)).thenReturn(expectedReportingPerformance);
        ResponseEntity<OpenLmisResponse> fetchedReportingPerformance = dashboardController.getReportingPerformance(1L,1L,1L,httpServletRequest);
        assertThat((ReportingStatus) fetchedReportingPerformance.getBody().getData().get(REPORTING_PERFORMANCE), is(expectedReportingPerformance));
        verify(lookupService).getReportingPerformance(userId,1L,1L,1L);

    }

    @Test
    public void shouldReturnReportingPerformanceDetail() throws Exception {
        List<ReportingPerformance> expectedReportingPerformanceDetail = new ArrayList<>(1);
        when(lookupService.getReportingPerformanceDetail(userId,1L,1L,1L,"non-reporting")).thenReturn(expectedReportingPerformanceDetail);

        ResponseEntity<OpenLmisResponse> fetchedReportingPerformanceDetailList = dashboardController.getReportingPerformanceDetail(1L,1L,1L,"non-reporting",httpServletRequest);

        assertThat((List<ReportingPerformance>) fetchedReportingPerformanceDetailList.getBody().getData().get(REPORTING_DETAILS), is(expectedReportingPerformanceDetail));
        verify(lookupService).getReportingPerformanceDetail(userId,1L,1L,1L,"non-reporting");

    }

    @Test
    public void shouldReturnYearOfPeriodById() throws Exception {
        when(lookupService.getYearOfPeriodById(1L)).thenReturn("2013");
        ResponseEntity<OpenLmisResponse> fetchedYearOfPeriod = dashboardController.getYearOfPeriodById(1L);

        assertThat((String) fetchedYearOfPeriod.getBody().getData().get("year"), is("2013"));
        verify(lookupService).getYearOfPeriodById(1L);

    }
}
