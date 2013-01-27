package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.FacilityService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProgramSupportedPersistenceHandlerTest {

  @Mock
  FacilityService facilityService;

  private ProgramSupportedPersistenceHandler programSupportedPersistenceHandler;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    programSupportedPersistenceHandler = new ProgramSupportedPersistenceHandler(facilityService);
  }

  @Test
  public void shouldSaveProgramSupported() {
    ProgramSupported programSupported = new ProgramSupported();
    programSupportedPersistenceHandler.save(programSupported, "user");
    verify(facilityService).uploadSupportedProgram(programSupported);
    assertThat(programSupported.getModifiedBy(), is("user"));
  }

}
