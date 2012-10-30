package org.openlmis.rnr.service;

import org.junit.Test;
import org.openlmis.rnr.dao.RnRColumnMapper;
import org.openlmis.rnr.domain.RnRColumn;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RnRTemplateServiceTest {

    @Test
    public void shouldFetchAllRnRColumns() {
        RnRColumnMapper mapper = mock(RnRColumnMapper.class);
        RnRTemplateService service = new RnRTemplateService(mapper);
        List<RnRColumn> allColumns = new ArrayList<RnRColumn>();
        when(mapper.fetchAllMasterRnRColumns()).thenReturn(allColumns);
        List<RnRColumn> rnRColumns = service.fetchAllMasterColumns();
        assertThat(rnRColumns, is(equalTo(allColumns)));
    }
}
