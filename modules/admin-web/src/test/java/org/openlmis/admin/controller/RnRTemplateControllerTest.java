package org.openlmis.admin.controller;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.admin.form.ProgramRnRTemplateForm;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.service.RnRTemplateService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class RnRTemplateControllerTest {

    private RnRTemplateService rnrTemplateService;
    private RnRTemplateController rnrTemplateController;

    int existingProgramId = 1;
    @Before
    public void setUp() throws Exception {
        rnrTemplateService = mock(RnRTemplateService.class);
        rnrTemplateController = new RnRTemplateController(rnrTemplateService);
    }

    @Test
    public void shouldGetMasterColumnListForRnR() {
        List<RnrColumn> allColumns = new ArrayList<RnrColumn>();

        when(rnrTemplateService.fetchAllRnRColumns(existingProgramId)).thenReturn(allColumns);
        List<RnrColumn> rnrColumns = rnrTemplateController.fetchMasterColumnList(existingProgramId);
        verify(rnrTemplateService).fetchAllRnRColumns(existingProgramId);
        assertThat(rnrColumns,is(allColumns));
    }

    @Test
    public void shouldCreateARnRTemplateForAGivenProgramWithSpecifiedColumns() throws Exception {
        ProgramRnRTemplateForm programRnRTemplateForm = new ProgramRnRTemplateForm();
        rnrTemplateController.createRnRTemplateForProgram(existingProgramId, programRnRTemplateForm);
        verify(rnrTemplateService).saveRnRTemplateForProgram(existingProgramId, programRnRTemplateForm);
    }
}
