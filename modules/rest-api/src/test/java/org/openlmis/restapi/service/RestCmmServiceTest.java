package org.openlmis.restapi.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.stockmanagement.domain.CMMEntry;
import org.openlmis.stockmanagement.service.CMMService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestFacilityService.class)
public class RestCmmServiceTest {

  @Mock
  private CMMService cmmService;

  @InjectMocks
  private RestCmmService service;

  @Test
  public void shouldAssignFacilityIdsToCMMAndCallServiceUpdate() {
    List<CMMEntry> entries = asList(createCMMEntry(1.0F, "P1"), createCMMEntry(1.0F, "P2"));

    service.updateCMMsForFacility(entries, 123L, 456L);

    ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
    verify(cmmService).updateCMMEntries(captor.capture());
    List<List> captorAllValues = captor.getAllValues();
    assertEquals(123L, ((CMMEntry) captorAllValues.get(0).get(0)).getFacilityId().longValue());
    assertEquals(456L, ((CMMEntry) captorAllValues.get(0).get(0)).getCreatedBy().longValue());
    assertEquals(456L, ((CMMEntry) captorAllValues.get(0).get(0)).getModifiedBy().longValue());
  }

  private CMMEntry createCMMEntry(Float cmmValue, String productCode) {
    CMMEntry entry = new CMMEntry();
    entry.setProductCode(productCode);
    entry.setCmmValue(cmmValue);
    return entry;
  }

}