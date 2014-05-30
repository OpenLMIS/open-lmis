package org.openlmis.report.service.lookup;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.mapper.lookup.RnRStatusSummaryReportMapper;
import org.openlmis.report.model.dto.ItemFillRate;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;


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

        when(mapper.getItemFillRate(1L,1L,1L, DashboardLookupService.getCommaSeparatedIds(productsId))).thenReturn(expectedItemFillRate);
        assertThat(lookupService.getItemFillRate(1L,1L,1L,productsId),is(expectedItemFillRate));

    }

}
