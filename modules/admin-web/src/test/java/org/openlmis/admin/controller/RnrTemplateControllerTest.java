package org.openlmis.admin.controller;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.admin.form.RnrColumnList;
import org.openlmis.admin.form.RnrTemplateForm;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.service.RnrTemplateService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class RnrTemplateControllerTest {

    private RnrTemplateService rnrTemplateService;
    private RnrTemplateController rnrTemplateController;

    String existingProgramCode = "HIV";

    @Before
    public void setUp() throws Exception {
        rnrTemplateService = mock(RnrTemplateService.class);
        rnrTemplateController = new RnrTemplateController(rnrTemplateService);
    }

    @Test
    public void shouldGetMasterColumnListForRnR() {
        List<RnrColumn> allColumns = new ArrayList<RnrColumn>();

        when(rnrTemplateService.fetchAllRnRColumns(existingProgramCode)).thenReturn(allColumns);
        RnrTemplateForm rnrColumns = rnrTemplateController.fetchAllProgramRnrColumnList(existingProgramCode);
        verify(rnrTemplateService).fetchAllRnRColumns(existingProgramCode);
        assertThat(rnrColumns.getRnrColumns(),is(allColumns));
    }

    @Test
    public void shouldCreateARnRTemplateForAGivenProgramWithSpecifiedColumns() throws Exception {
        RnrColumnList rnrColumns = new RnrColumnList();
        rnrTemplateController.saveRnRTemplateForProgram(existingProgramCode, rnrColumns);
        verify(rnrTemplateService).saveRnRTemplateForProgram(existingProgramCode, rnrColumns);
    }

}
