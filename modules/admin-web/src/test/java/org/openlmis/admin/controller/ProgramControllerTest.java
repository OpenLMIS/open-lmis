package org.openlmis.admin.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.authentication.service.UserAuthenticationService;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProgramService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProgramControllerTest {

    @Mock
    @SuppressWarnings("unused")
    private ProgramService programService;

    @Before
    public void init(){
        initMocks(this);
    }

    @Test
    public void shouldGetListOfPrograms() throws Exception {
        ProgramController controller = new ProgramController(programService);
        List<Program> expectedPrograms = new ArrayList<Program>();

        when(programService.getAllActive()).thenReturn(expectedPrograms);

        List<Program> result = controller.getAllActivePrograms();

        verify(programService).getAllActive();
        assertThat(result, is(equalTo(expectedPrograms)));
    }

}
