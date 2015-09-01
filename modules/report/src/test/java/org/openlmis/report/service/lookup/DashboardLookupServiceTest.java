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

package org.openlmis.report.service.lookup;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.mapper.lookup.RnRStatusSummaryReportMapper;
import org.openlmis.report.model.dto.ItemFillRate;
import org.openlmis.report.model.dto.ReportingPerformance;
import org.openlmis.report.model.dto.ShipmentLeadTime;
import org.openlmis.report.model.dto.StockingInfo;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.openlmis.report.service.lookup.DashboardLookupService.getCommaSeparatedIds;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(DashboardLookupService.class)
public class DashboardLookupServiceTest {

  @Mock
  DashboardMapper mapper;

  @Mock
  RnRStatusSummaryReportMapper rnRStatusSummaryReportMapper;

  @InjectMocks
  DashboardLookupService lookupService;


  @Test
  public void shouldReturnItemFillRate() throws Exception {
    List<Long> productsId = new ArrayList<>(2);
    productsId.add(1L);
    productsId.add(2L);

    List<ItemFillRate> expectedItemFillRate = new ArrayList<>(1);
    expectedItemFillRate.add(new ItemFillRate(50f, "Product A"));

    when(mapper.getItemFillRate(1L, 1L, 1L, getCommaSeparatedIds(productsId))).thenReturn(expectedItemFillRate);
    assertThat(lookupService.getItemFillRate(1L, 1L, 1L, productsId), is(expectedItemFillRate));
    verify(mapper).getItemFillRate(1L, 1L, 1L, getCommaSeparatedIds(productsId));

  }

  @Test
  public void shouldReturnOrderFillRate() throws Exception {

    when(mapper.getOrderFillRate(1L, 1L, 1L)).thenReturn(null);
    assertThat(lookupService.getOrderFillRate(1L, 1L, 1L), is(nullValue()));
    verify(mapper).getOrderFillRate(1L, 1L, 1L);

  }

  @Test
  public void shouldReturnShipmentLeadTime() throws Exception {
    List<ShipmentLeadTime> expectedShipmentLeadTimeList = new ArrayList<>(1);
    when(mapper.getShipmentLeadTime(1L, 1L, 1L, 1L)).thenReturn(expectedShipmentLeadTimeList);
    assertThat(lookupService.getShipmentLeadTime(1L, 1L, 1L, 1L), is(expectedShipmentLeadTimeList));
    verify(mapper).getShipmentLeadTime(1L, 1L, 1L, 1L);
  }

  @Test
  public void shouldReturnStockEfficiencyData() throws Exception {
    List<StockingInfo> expectedStockingInfoList = new ArrayList<>(1);
    when(mapper.getStockEfficiencyData(1L, 1L, 1L, 1L, getCommaSeparatedIds(null))).thenReturn(expectedStockingInfoList);
    assertThat(lookupService.getStockEfficiencyData(1L, 1L, 1L, 1L, null), is(expectedStockingInfoList));
    verify(mapper).getStockEfficiencyData(1L, 1L, 1L, 1L, getCommaSeparatedIds(null));

  }

  @Test
  public void shouldReturnStockEfficiencyDetailData() throws Exception {
    List<StockingInfo> expectedStockingInfoDetailList = new ArrayList<>(1);
    when(mapper.getStockEfficiencyDetailData(1L, 1L, 1L, 1L, getCommaSeparatedIds(null))).thenReturn(expectedStockingInfoDetailList);
    assertThat(lookupService.getStockEfficiencyDetailData(1L, 1L, 1L, 1L, null), is(expectedStockingInfoDetailList));
    verify(mapper).getStockEfficiencyDetailData(1L, 1L, 1L, 1L, getCommaSeparatedIds(null));
  }

  @Test
  public void shouldReturnStockOutFacilities() throws Exception {
    when(mapper.getStockOutFacilities(1L, 1L, 1L, 1L, 1L)).thenReturn(null);
    assertThat(lookupService.getStockOutFacilities(1L, 1L, 1L, 1L, 1L), is(nullValue()));
    verify(mapper).getStockOutFacilities(1L, 1L, 1L, 1L, 1L);
  }

  @Test
  public void shouldReturnStockOutFacilitiesByRequisitionGroup() throws Exception {
    when(mapper.getStockOutFacilitiesForGeographicZone(1L, 1L, 1L, 1L, 1L)).thenReturn(null);
    assertThat(lookupService.getStockOutFacilitiesByGeographicZoneFilter(1L, 1L, 1L, 1L, 1L), is(nullValue()));
    verify(mapper).getStockOutFacilitiesForGeographicZone(1L, 1L, 1L, 1L, 1L);

  }

  @Test
  public void shouldReturnAlerts() throws Exception {
    when(mapper.getAlerts(1L, 1L, 1L, 1L)).thenReturn(null);
    assertThat(lookupService.getAlerts(1L, 1L, 1L, 1L), is(nullValue()));
    verify(mapper).getAlerts(1L, 1L, 1L, 1L);
  }

  @Test
  public void shouldReturnNotificationAlerts() throws Exception {
    when(mapper.getNotificationAlerts()).thenReturn(null);
    assertThat(lookupService.getNotificationAlerts(), is(nullValue()));
    verify(mapper).getNotificationAlerts();
  }

  @Test
  public void shouldReturnNotificationsByCategory() throws Exception {
    List<HashMap> expectedFacilityStockedOutList = new ArrayList<>(1);
    when(mapper.getNotificationDetails(1L, 1L, 1L, 1L, "alert_facility_stockedout")).thenReturn(expectedFacilityStockedOutList);
    assertThat(lookupService.getNotificationsByCategory(1L, 1L, 1L, 1L, "alert_facility_stockedout"), is(expectedFacilityStockedOutList));
    verify(mapper).getNotificationDetails(1L, 1L, 1L, 1L, "alert_facility_stockedout");

  }

  @Test
  public void shouldReturnYearOfPeriodById() throws Exception {
    when(mapper.getYearOfPeriodById(1L)).thenReturn("2013");
    assertThat(lookupService.getYearOfPeriodById(1L), is("2013"));
    verify(mapper).getYearOfPeriodById(1L);
  }

  @Test
  public void shouldReturnReportingPerformance() throws Exception {
    when(mapper.getReportingPerformance(1L, 1L, 1L, 1L)).thenReturn(null);
    assertThat(lookupService.getReportingPerformance(1L, 1L, 1L, 1L), is(nullValue()));
    verify(mapper).getReportingPerformance(1L, 1L, 1L, 1L);
  }

  @Test
  public void shouldReportingPerformanceDetail() throws Exception {
    List<ReportingPerformance> expectedNonReportingPerformance = new ArrayList<>(1);
    when(mapper.getReportingPerformanceDetail(1L, 1L, 1L, 1L, "non_reporting")).thenReturn(expectedNonReportingPerformance);
    assertThat(lookupService.getReportingPerformanceDetail(1L, 1L, 1L, 1L, "non_reporting"), is(expectedNonReportingPerformance));
    verify(mapper).getReportingPerformanceDetail(1L, 1L, 1L, 1L, "non_reporting");

  }
}
