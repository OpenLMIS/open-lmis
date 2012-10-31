package org.openlmis.admin.controller;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.admin.form.ProgramRnRTemplateForm;
import org.openlmis.rnr.domain.RnRColumn;
import org.openlmis.rnr.service.RnRTemplateService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RnRTemplateControllerTest {
    private RnRTemplateService rnrTemplateService;
    private RnRTemplateController rnrTemplateController;

    @Before
    public void setUp() throws Exception {
        rnrTemplateService = mock(RnRTemplateService.class);
        rnrTemplateController = new RnRTemplateController(rnrTemplateService);
    }

    @Test
    public void shouldGetMasterColumnListForRnR() {
        List<RnRColumn> allColumns = new ArrayList<RnRColumn>();
        when(rnrTemplateService.fetchAllMasterColumns()).thenReturn(allColumns);
        List<RnRColumn> rnrColumns = rnrTemplateController.fetchMasterColumnList();
        verify(rnrTemplateService).fetchAllMasterColumns();
        assertThat(rnrColumns,is(allColumns));
    }

    @Test
    public void shouldCreateARnRTemplateForAGivenProgramWithSpecifiedColumns() throws Exception {
        ArrayList<RnRColumn> rnRColumns = new ArrayList<RnRColumn>();
        ProgramRnRTemplateForm programRnRTemplateForm = mock(ProgramRnRTemplateForm.class);
        String programId = "programId";
        when(programRnRTemplateForm.getRnRColumns()).thenReturn(rnRColumns);
        rnrTemplateController.createRnRTemplateForProgram(programId, programRnRTemplateForm);
        verify(rnrTemplateService).createRnRTemplateForProgram(programId, rnRColumns);
    }
}
