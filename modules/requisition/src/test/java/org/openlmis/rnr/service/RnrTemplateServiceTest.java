package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.RnrTemplateRepository;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RnrTemplateServiceTest {

    @Mock
    private RnrTemplateRepository repository;

    private RnrTemplateService service;

    private final static Integer EXISTING_PROGRAM_ID = 1;


    @Before
    public void setUp() throws Exception {
        service = new RnrTemplateService(repository);
    }

    @Test
    public void shouldFetchAllRnRColumns() throws Exception {
        service.fetchAllRnRColumns(EXISTING_PROGRAM_ID);
        verify(repository).fetchRnrTemplateColumns(EXISTING_PROGRAM_ID);
    }

    @Test
    public void shouldSaveRnRTemplateForAProgramWithGivenColumns() throws Exception {
        ProgramRnrTemplate programRnrTemplate =mock(ProgramRnrTemplate.class);
        ArrayList<RnrColumn> rnrColumns = new ArrayList<>();
        when(programRnrTemplate.getRnrColumns()).thenReturn(rnrColumns);
        when(programRnrTemplate.validate()).thenReturn(new HashMap<String,String>());
        service.saveRnRTemplateForProgram(programRnrTemplate);
        verify(repository).saveProgramRnrTemplate(programRnrTemplate);
    }

    @Test
    public void shouldNotSaveIfErrorInTemplate() throws Exception {
        ProgramRnrTemplate programRnrTemplate =mock(ProgramRnrTemplate.class);
        ArrayList<RnrColumn> rnrColumns = new ArrayList<>();
        when(programRnrTemplate.getRnrColumns()).thenReturn(rnrColumns);
        HashMap<String, String> errors = new HashMap<>();
        errors.put("error-code","error-message");
        when(programRnrTemplate.validate()).thenReturn(errors);
        service.saveRnRTemplateForProgram(programRnrTemplate);
        verify(repository, never()).saveProgramRnrTemplate(programRnrTemplate);
    }
}
