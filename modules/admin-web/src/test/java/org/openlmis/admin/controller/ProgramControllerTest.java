package org.openlmis.admin.controller;

import org.junit.Test;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProgramService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class ProgramControllerTest {

    @Test
    //TODO: use mockito annotations
    public void shouldGetListOfPrograms() throws Exception {
        ProgramService programService = mock(ProgramService.class);
        ProgramController controller = new ProgramController(programService);
        List<Program> expectedPrograms = new ArrayList<Program>();
        when(programService.getAll()).thenReturn(expectedPrograms);
        List<Program> result = controller.getAllPrograms();
        verify(programService).getAll();
        assertThat(result, is(equalTo(expectedPrograms)));
    }

}
