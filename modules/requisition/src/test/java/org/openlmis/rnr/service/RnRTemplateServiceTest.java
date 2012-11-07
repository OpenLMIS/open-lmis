package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.rnr.dao.RnrRepository;
import org.openlmis.rnr.domain.RnrColumn;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@RunWith(MockitoJUnitRunner.class)
public class RnRTemplateServiceTest {
    @Mock @SuppressWarnings("unused")
    private RnrRepository repository;

    private RnRTemplateService service;

    @Before
    public void setUp() throws Exception {
        service = new RnRTemplateService(repository);
    }

    @Test
    public void shouldFetchAllRnRColumnsFromMasterIfNotAlreadyConfigured() {
        int existingProgramId = 1;
        List<RnrColumn> allColumns = new ArrayList<RnrColumn>();
        when(repository.fetchAllMasterRnRColumns()).thenReturn(allColumns);
        when(repository.isRnRTemPlateDefinedForProgram(existingProgramId)).thenReturn(false);
        List<RnrColumn> rnrColumns = service.fetchAllRnRColumns(existingProgramId);
        assertThat(rnrColumns, is(equalTo(allColumns)));
        verify(repository).fetchAllMasterRnRColumns();
        verify(repository,never()).fetchRnrColumnsDefinedForAProgram(existingProgramId);
        verify(repository).isRnRTemPlateDefinedForProgram(existingProgramId);
    }

    @Test
    public void shouldFetchRnRColumnsDefinedForAProgramIfAlreadyConfigured() {
        int existingProgramId = 1;
        List<RnrColumn> rnrTemplateColumns = new ArrayList<RnrColumn>();
        when(repository.fetchRnrColumnsDefinedForAProgram(existingProgramId)).thenReturn(rnrTemplateColumns);
        when(repository.isRnRTemPlateDefinedForProgram(existingProgramId)).thenReturn(true);
        List<RnrColumn> rnrColumns = service.fetchAllRnRColumns(existingProgramId);
        assertThat(rnrColumns, is(equalTo(rnrTemplateColumns)));
        verify(repository, never()).fetchAllMasterRnRColumns();
        verify(repository).fetchRnrColumnsDefinedForAProgram(existingProgramId);
        verify(repository).isRnRTemPlateDefinedForProgram(existingProgramId);
    }

    @Test
    public void shouldFetchEmptyListIfRnRColumnListReturnedIsNull() throws Exception {
        int existingProgramId = 1;
        when(repository.fetchAllMasterRnRColumns()).thenReturn(null);
        when(repository.isRnRTemPlateDefinedForProgram(existingProgramId)).thenReturn(false);
        List<RnrColumn> rnrColumns = service.fetchAllRnRColumns(existingProgramId);
        assertThat(rnrColumns, is(notNullValue()));
    }

    @Test
    public void shouldCreateARnRTemplateForAProgramWithGivenColumns() throws Exception {
        List<RnrColumn> rnrColumns = new ArrayList<RnrColumn>();
        service.createRnRTemplateForProgram(1, rnrColumns);
        verify(repository).insertAllProgramRnRColumns(1, rnrColumns);
    }
}
