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
        when(rnrTemplateService.fetchAllMasterColumns()).thenReturn(allColumns);
        List<RnrColumn> rnrColumns = rnrTemplateController.fetchMasterColumnList();
        verify(rnrTemplateService).fetchAllMasterColumns();
        assertThat(rnrColumns,is(allColumns));
    }

    @Test
    public void shouldCreateARnRTemplateForAGivenProgramWithSpecifiedColumns() throws Exception {
        ArrayList<RnrColumn> rnrColumns = new ArrayList<RnrColumn>();
        ProgramRnRTemplateForm programRnRTemplateForm = mock(ProgramRnRTemplateForm.class);
        when(programRnRTemplateForm.getRnrColumns()).thenReturn(rnrColumns);
        rnrTemplateController.createRnRTemplateForProgram("1", programRnRTemplateForm);
        verify(rnrTemplateService).createRnRTemplateForProgram(1, rnrColumns);
    }
}
