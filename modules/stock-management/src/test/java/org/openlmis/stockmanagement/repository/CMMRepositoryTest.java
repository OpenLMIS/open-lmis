package org.openlmis.stockmanagement.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.stockmanagement.domain.CMMEntry;
import org.openlmis.stockmanagement.repository.mapper.CMMMapper;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class CMMRepositoryTest {

    @Mock
    private CMMMapper mapper;

    @InjectMocks
    private CMMRepository repository;

    @Test
    public void shouldUpdateCMMIfItAlreadyExists() {
        Date beginDate = new Date();
        Date endDate = new Date();
        CMMEntry cmm = createCMMEntry(1.0F, "P1", beginDate, endDate, 9L);
        cmm.setId(999L);
        when(mapper.getCMMEntryByFacilityAndPeriodAndProductCode(9L, "P1", beginDate, endDate)).thenReturn(cmm);

        repository.createOrUpdate(cmm);

        ArgumentCaptor<CMMEntry> captor = ArgumentCaptor.forClass(CMMEntry.class);
        verify(mapper).update(captor.capture());
        List<CMMEntry> captorAllValues = captor.getAllValues();
        assertEquals(999L, captorAllValues.get(0).getId().longValue());
    }

    @Test
    public void shouldInsertCMMIfItDoesNotExist() {
        Date beginDate = new Date();
        Date endDate = new Date();
        CMMEntry cmm = createCMMEntry(1.0F, "P1", beginDate, endDate, 9L);
        when(mapper.getCMMEntryByFacilityAndPeriodAndProductCode(9L, "P1", beginDate, endDate)).thenReturn(null);

        repository.createOrUpdate(cmm);
        verify(mapper).insert(cmm);
    }

    private CMMEntry createCMMEntry(Float cmmValue, String productCode, Date beginDate, Date endDate, Long facilityId) {
        CMMEntry entry = new CMMEntry();
        entry.setProductCode(productCode);
        entry.setCmmValue(cmmValue);
        entry.setPeriodBegin(beginDate);
        entry.setPeriodEnd(endDate);
        entry.setFacilityId(facilityId);
        return entry;
    }
}