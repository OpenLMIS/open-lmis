package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.rnr.dao.RnrColumnMapper;
import org.openlmis.rnr.dao.RnrDao;
import org.openlmis.rnr.domain.RnrColumn;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class RnRTemplateServiceTest {

    private RnrColumnMapper mapper;
    private RnRTemplateService service;
    private RnrDao dao;

    @Before
    public void setUp() throws Exception {
        dao = mock(RnrDao.class);
        mapper = mock(RnrColumnMapper.class);
        service = new RnRTemplateService(mapper, dao);
    }

    @Test
    public void shouldFetchAllRnRColumns() {
        List<RnrColumn> allColumns = new ArrayList<RnrColumn>();
        when(mapper.fetchAllMasterRnRColumns()).thenReturn(allColumns);
        List<RnrColumn> rnrColumns = service.fetchAllMasterColumns();
        assertThat(rnrColumns, is(equalTo(allColumns)));
    }

    @Test
    public void shouldFetchEmptyListIfListReturnedIsNull() throws Exception {
        List<RnrColumn> nullList=null;
        when(mapper.fetchAllMasterRnRColumns()).thenReturn(nullList);
        List<RnrColumn> returnedList = service.fetchAllMasterColumns();
        assertThat(returnedList,not(nullValue()));
    }

    @Test
    public void shouldCreateARnRTemplateForAProgramWithGivenColumns() throws Exception {
        List<RnrColumn> rnrColumns = new ArrayList<RnrColumn>();
        service.createRnRTemplateForProgram(1, rnrColumns);
        verify(dao).insertAllProgramRnRColumns(1, rnrColumns);
    }
}
