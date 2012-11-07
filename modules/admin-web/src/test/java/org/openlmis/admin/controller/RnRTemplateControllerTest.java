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

    @Before
    public void setUp() throws Exception {
        rnrTemplateService = mock(RnRTemplateService.class);
        rnrTemplateController = new RnRTemplateController(rnrTemplateService);
    }

    @Test
    public void shouldGetMasterColumnListForRnR() {
        List<RnrColumn> allColumns = new ArrayList<RnrColumn>();
        String existingProgramId = "1";
        when(rnrTemplateService.fetchAllRnRColumns(1)).thenReturn(allColumns);
        List<RnrColumn> rnrColumns = rnrTemplateController.fetchMasterColumnList(existingProgramId);
        verify(rnrTemplateService).fetchAllRnRColumns(1);
        assertThat(rnrColumns,is(allColumns));
    }

    @Test
    public void shouldCreateARnRTemplateForAGivenProgramWithSpecifiedColumns() throws Exception {
        ProgramRnRTemplateForm programRnRTemplateForm = new ProgramRnRTemplateForm();
        rnrTemplateController.createRnRTemplateForProgram("1", programRnRTemplateForm);
        verify(rnrTemplateService).createRnRTemplateForProgram(1, programRnRTemplateForm);
    }
}
