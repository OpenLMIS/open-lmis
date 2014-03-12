package org.openlmis.report.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.matchers.Matchers;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.model.dto.ItemFillRate;
import org.openlmis.report.model.dto.OrderFillRate;
import org.openlmis.report.model.dto.StockingInfo;
import org.openlmis.report.response.OpenLmisResponse;
import org.openlmis.report.service.lookup.DashboardLookupService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.openlmis.report.controller.DashboardController.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


import java.util.ArrayList;
import java.util.List;

/**
 * User: Issa
 * Date: 3/11/14
 * Time: 8:20 PM
 */
@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
@PrepareForTest(OpenLmisResponse.class)
public class DashboardControllerTest {
    @Mock
    DashboardLookupService lookupService;

    @InjectMocks
    DashboardController dashboardController;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnItemFillRateForSelectedFacilityAndProducts(){
        Long geoId = 1L, programId = 1L, periodId = 1L,facilityId = 1L;
        List<Long> productsId = new ArrayList<>(2);
        productsId.add(1L);
        productsId.add(2L);

        List<ItemFillRate> expectedItemFillRate = new ArrayList<>(1);
        expectedItemFillRate.add(new ItemFillRate(50, "Product A"));

        when(lookupService.getItemFillRate(geoId, periodId, facilityId, programId, productsId)).thenReturn(expectedItemFillRate);

        ResponseEntity<OpenLmisResponse> fetchedItemFillRate = dashboardController.getItemFillRate(geoId,periodId,facilityId,programId,productsId);

        verify(lookupService).getItemFillRate(geoId,periodId,facilityId,programId,productsId);

       Assert.assertThat((List<ItemFillRate>)fetchedItemFillRate.getBody().getData().get(ITEM_FILL_RATE), is(expectedItemFillRate));

    }

    @Test
    public void shouldReturnOrderFillRateForSelectedFacility(){
        Long geoId = 1L, programId = 1L, periodId = 1L,facilityId = 1L;
        OrderFillRate expectedOrderFillRate = new OrderFillRate(45.5f);
        when(lookupService.getOrderFillRate(geoId, periodId, facilityId, programId)).thenReturn(expectedOrderFillRate);
        ResponseEntity<OpenLmisResponse> fetchedOrderFillRate = dashboardController.getOrderFillRate(geoId, periodId, facilityId, programId);

        verify(lookupService).getOrderFillRate(geoId, periodId, facilityId, programId);

        Assert.assertThat((OrderFillRate)fetchedOrderFillRate.getBody().getData().get(ORDER_FILL_RATE), is(expectedOrderFillRate));
    }

    @Test
    public void shouldReturnStockEfficiencyStatics(){
        List<Long> productIdList = new ArrayList<>();
        List<StockingInfo> expectedStockingInfo = new ArrayList<>(1);
        expectedStockingInfo.add(new StockingInfo());

        when(lookupService.getStockEfficiencyData(1L, 1L, 1L, productIdList)).thenReturn(expectedStockingInfo);

        ResponseEntity<OpenLmisResponse> fetchedStockingInfoStat = dashboardController.getStockEfficiencyData(1L,1L,1L,productIdList);

        verify(lookupService).getStockEfficiencyData(1L,1L,1L,productIdList);

        assertThat((List<StockingInfo>) fetchedStockingInfoStat.getBody().getData().get(STOCKING_EFFICIENCY_STATICS), is(expectedStockingInfo));
    }

    @Test
    public void shouldReturnStockEfficiencyDetail(){
        List<Long> productIdList = new ArrayList<>();
        List<StockingInfo> expectedStockingDetail = new ArrayList<>(1);
        expectedStockingDetail.add(new StockingInfo());

        when(lookupService.getStockEfficiencyDetailData(1L, 1L, 1L, productIdList)).thenReturn(expectedStockingDetail);

        ResponseEntity<OpenLmisResponse> fetchedStockingInfoStat = dashboardController.getStockEfficiencyDetailData(1L,1L,1L,productIdList);

        verify(lookupService).getStockEfficiencyDetailData(1L,1L,1L,productIdList);

        assertThat((List<StockingInfo>) fetchedStockingInfoStat.getBody().getData().get(STOCKING_EFFICIENCY_DETAIL), is(expectedStockingDetail));
    }

}
