package org.openlmis.admin.controller;

import org.junit.Test;
import org.openlmis.rnr.domain.RnRColumn;
import org.openlmis.rnr.service.RnRTemplateService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RnRTemplateControllerTest {
    @Test
    public void shouldGetMasterColumnListForRnR() {
        RnRTemplateService rnrTemplateService = mock(RnRTemplateService.class);
        RnRTemplateController rnRTemplateController = new RnRTemplateController(rnrTemplateService);
        List<RnRColumn> allColumns = new ArrayList<RnRColumn>();
        when(rnrTemplateService.fetchAllMasterColumns()).thenReturn(allColumns);
        List<RnRColumn> rnrColumns = rnRTemplateController.fetchMasterColumnList();
        assertThat(rnrColumns,is(allColumns));
    }
}
