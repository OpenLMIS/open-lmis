/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.web.form.RnrColumnList;
import org.openlmis.web.form.RnrTemplateForm;
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

    private Long existingProgramId = 1L;

    @Before
    public void setUp() throws Exception {
        rnrTemplateService = mock(RnrTemplateService.class);
        rnrTemplateController = new RnrTemplateController(rnrTemplateService);
    }

    @Test
    public void shouldGetMasterColumnListForRnR() {
        List<RnrColumn> allColumns = new ArrayList<>();

        when(rnrTemplateService.fetchAllRnRColumns(existingProgramId)).thenReturn(allColumns);
        RnrTemplateForm rnrColumns = rnrTemplateController.fetchAllProgramRnrColumnList(existingProgramId);
        verify(rnrTemplateService).fetchAllRnRColumns(existingProgramId);
        assertThat(rnrColumns.getRnrColumns(),is(allColumns));
    }

    @Test
    public void shouldCreateARnRTemplateForAGivenProgramWithSpecifiedColumns() throws Exception {
        final RnrColumnList rnrColumns = new RnrColumnList();

        rnrTemplateController.saveRnRTemplateForProgram(existingProgramId, rnrColumns);
        Matcher<ProgramRnrTemplate> matcher = new ArgumentMatcher<ProgramRnrTemplate>() {
            @Override
            public boolean matches(Object argument) {
                ProgramRnrTemplate programRnrTemplate1 = (ProgramRnrTemplate)argument;
                return programRnrTemplate1.getRnrColumns().equals(rnrColumns);
            }
        };
        verify(rnrTemplateService).saveRnRTemplateForProgram(argThat(matcher));
    }
}
