package org.openlmis.report.service.lookup;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.mapper.lookup.RnRStatusSummaryReportMapper;
import org.openlmis.report.model.dto.ItemFillRate;
import org.openlmis.report.model.dto.ShipmentLeadTime;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.openlmis.report.service.lookup.DashboardLookupService.getCommaSeparatedIds;



/**
 * User: Issa
 * Date: 5/30/14
 * Time: 10:09 AM
 */
@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
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
        expectedItemFillRate.add(new ItemFillRate(50, "Product A"));

        when(mapper.getItemFillRate(1L,1L,1L, getCommaSeparatedIds(productsId))).thenReturn(expectedItemFillRate);
        assertThat(lookupService.getItemFillRate(1L,1L,1L,productsId),is(expectedItemFillRate));
        Mockito.verify(mapper).getItemFillRate(1L,1L,1L,getCommaSeparatedIds(productsId));

    }
    @Test
    public void shouldReturnOrderFillRate() throws Exception {

        when(mapper.getOrderFillRate(1L,1L,1L)).thenReturn(null);
        assertThat(lookupService.getOrderFillRate(1L, 1L, 1L), is(nullValue()));
        Mockito.verify(mapper).getOrderFillRate(1L,1L,1L);

    }
    @Test
    public void shouldReturnShipmentLeadTime() throws Exception {
        List<ShipmentLeadTime> expectedShipmentLeadTimeList = new ArrayList<>(1);
        when(mapper.getShipmentLeadTime(1L,1L,getCommaSeparatedIds(null))).thenReturn(expectedShipmentLeadTimeList);
        assertThat(lookupService.getShipmentLeadTime(1L,1L, null), is(expectedShipmentLeadTimeList));
        Mockito.verify(mapper.getShipmentLeadTime(1L,1L,getCommaSeparatedIds(null)));
    }

}
