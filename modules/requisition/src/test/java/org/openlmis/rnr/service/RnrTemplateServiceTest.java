package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.openlmis.rnr.domain.RnrColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RnrTemplateServiceTest {

    @Mock
    private RnrTemplateRepository repository;

    private RnrTemplateService service;

    private String existingProgramCode = "HIV";

    @Before
    public void setUp() throws Exception {
        service = new RnrTemplateService(repository);
    }

    @Test
    public void shouldFetchAllRnRColumnsFromMasterIfNotAlreadyConfigured() {
        List<RnrColumn> allColumns = new ArrayList<RnrColumn>();
        when(repository.fetchAllMasterRnRColumns()).thenReturn(allColumns);
        when(repository.isRnRTemPlateDefinedForProgram(existingProgramCode)).thenReturn(false);
        List<RnrColumn> rnrColumns = service.fetchAllRnRColumns(existingProgramCode);
        assertThat(rnrColumns, is(equalTo(allColumns)));
        verify(repository).fetchAllMasterRnRColumns();
        verify(repository, never()).fetchProgramRnrColumns(existingProgramCode);
        verify(repository).isRnRTemPlateDefinedForProgram(existingProgramCode);
    }

    @Test
    public void shouldFetchRnRColumnsDefinedForAProgramIfAlreadyConfigured() {
        List<RnrColumn> rnrTemplateColumns = new ArrayList<RnrColumn>();
        when(repository.fetchProgramRnrColumns(existingProgramCode)).thenReturn(rnrTemplateColumns);
        when(repository.isRnRTemPlateDefinedForProgram(existingProgramCode)).thenReturn(true);
        List<RnrColumn> rnrColumns = service.fetchAllRnRColumns(existingProgramCode);
        assertThat(rnrColumns, is(equalTo(rnrTemplateColumns)));
        verify(repository, never()).fetchAllMasterRnRColumns();
        verify(repository).fetchProgramRnrColumns(existingProgramCode);
        verify(repository).isRnRTemPlateDefinedForProgram(existingProgramCode);
    }

    @Test
    public void shouldFetchEmptyListIfRnRColumnListReturnedIsNull() throws Exception {
        when(repository.fetchAllMasterRnRColumns()).thenReturn(null);
        when(repository.isRnRTemPlateDefinedForProgram(existingProgramCode)).thenReturn(false);
        List<RnrColumn> rnrColumns = service.fetchAllRnRColumns(existingProgramCode);
        assertThat(rnrColumns, is(notNullValue()));
    }

    @Test
    public void shouldCreateARnRTemplateForAProgramWithGivenColumnsIfNotAlreadyDefined() throws Exception {
        List<RnrColumn> rnrColumns = new ArrayList<RnrColumn>();
        when(repository.isRnRTemPlateDefinedForProgram(existingProgramCode)).thenReturn(false);
        service.saveRnRTemplateForProgram(existingProgramCode, rnrColumns);
        verify(repository).insertAllProgramRnRColumns(existingProgramCode, rnrColumns);
    }

    @Test
    public void shouldUpdateExistingRnRTemplateForAProgramWithGivenColumnsIfAlreadyDefined() throws Exception {
        List<RnrColumn> rnrColumns = new ArrayList<RnrColumn>();
        when(repository.isRnRTemPlateDefinedForProgram(existingProgramCode)).thenReturn(true);
        service.saveRnRTemplateForProgram(existingProgramCode, rnrColumns);
        verify(repository).updateAllProgramRnRColumns(existingProgramCode, rnrColumns);
    }

    @Test
    public void shouldReturnErrorOnSaveWithReferentialDependencyMissing() {
        List<RnrColumn> rnrColumns = new ArrayList<>();
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setName("Rnr");
        rnrColumn.setVisible(false);
        RnrColumn dependentRnrColumn = new RnrColumn();
        dependentRnrColumn.setName("dependentRnr");
        dependentRnrColumn.setDependencies(Arrays.asList(new RnrColumn[]{rnrColumn}));
        dependentRnrColumn.setVisible(true);
        rnrColumns.add(rnrColumn);
        rnrColumns.add(dependentRnrColumn);

        List errors = service.saveRnRTemplateForProgram(existingProgramCode, rnrColumns);
        assertThat(errors.size(), is(not(0)));
    }

    @Test
    public void shouldReturnNullOnSaveWithSatisfiedReferentialDependency() {
        List<RnrColumn> rnrColumns = new ArrayList<>();
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setName("Rnr");
        rnrColumn.setVisible(true);
        RnrColumn dependentRnrColumn = new RnrColumn();
        dependentRnrColumn.setName("dependentRnr");
        dependentRnrColumn.setDependencies(Arrays.asList(new RnrColumn[]{rnrColumn}));
        dependentRnrColumn.setVisible(true);
        rnrColumns.add(rnrColumn);
        rnrColumns.add(dependentRnrColumn);

        List errors = service.saveRnRTemplateForProgram(existingProgramCode, rnrColumns);
        assertThat(errors, is(equalTo(null)));

    }

}
