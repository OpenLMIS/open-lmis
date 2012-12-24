package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.repository.FacilityRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProgramSupportedPersistenceHandlerTest {

    @Mock
    FacilityRepository facilityRepository;

    private ProgramSupportedPersistenceHandler programSupportedPersistenceHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        programSupportedPersistenceHandler = new ProgramSupportedPersistenceHandler(facilityRepository);
    }


    @Test
    public void shouldSaveProgramSupported(){
        ProgramSupported programSupported = new ProgramSupported();
        programSupportedPersistenceHandler.save(programSupported, "user");
        verify(facilityRepository).addSupportedProgram(programSupported);
        assertThat(programSupported.getModifiedBy(), is("user"));
        assertThat(programSupported.getModifiedDate(), is(notNullValue()));
    }

}
