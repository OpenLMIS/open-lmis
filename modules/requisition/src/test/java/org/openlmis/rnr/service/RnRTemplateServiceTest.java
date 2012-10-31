package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.rnr.dao.RnRColumnMapper;
import org.openlmis.rnr.dao.RnRDAO;
import org.openlmis.rnr.domain.RnRColumn;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RnRTemplateServiceTest {

    private RnRColumnMapper mapper;
    private RnRTemplateService service;
    private RnRDAO dao;

    @Before
    public void setUp() throws Exception {
        dao = mock(RnRDAO.class);
        mapper = mock(RnRColumnMapper.class);
        service = new RnRTemplateService(mapper, dao);
    }

    @Test
    public void shouldFetchAllRnRColumns() {
        List<RnRColumn> allColumns = new ArrayList<RnRColumn>();
        when(mapper.fetchAllMasterRnRColumns()).thenReturn(allColumns);
        List<RnRColumn> rnRColumns = service.fetchAllMasterColumns();
        assertThat(rnRColumns, is(equalTo(allColumns)));
    }

    @Test
    public void shouldFetchEmptyListIfListReturnedIsNull() throws Exception {
        List<RnRColumn> nullList=null;
        when(mapper.fetchAllMasterRnRColumns()).thenReturn(nullList);
        List<RnRColumn> returnedList = service.fetchAllMasterColumns();
        assertThat(returnedList,not(nullValue()));
    }

    @Test
    public void shouldCreateARnRTemplateForAProgramWithGivenColumns() throws Exception {
        String programId = "10";
        List<RnRColumn> rnrColumns = new ArrayList<RnRColumn>();
        service.createRnRTemplateForProgram(programId, rnrColumns);
        verify(dao).insertAllRnRColumns(10, rnrColumns);
    }
}
