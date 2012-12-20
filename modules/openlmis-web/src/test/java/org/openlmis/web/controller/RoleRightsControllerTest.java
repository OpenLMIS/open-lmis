package org.openlmis.web.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoleRightsControllerTest {

    @Mock
    RoleRightsService roleRightsService;

    @Test
    public void shouldFetchAllRightsInSystem() throws Exception {
        List<Right> rights = new ArrayList<>();
        when(roleRightsService.getAllRights()).thenReturn(rights);
        List<Right> result = new RoleRightsController(roleRightsService).getAllRights();
        assertThat(result,is(rights));
        verify(roleRightsService).getAllRights();
    }
}
