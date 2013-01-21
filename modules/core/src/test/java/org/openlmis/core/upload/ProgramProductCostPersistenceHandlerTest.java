package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.service.ProgramProductService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProgramProductCostPersistenceHandlerTest {
  private ProgramProductCostPersistenceHandler programProductCostPersistenceHandler;
  @Mock
  private ProgramProductService programProductService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    programProductCostPersistenceHandler = new ProgramProductCostPersistenceHandler(programProductService);
  }


  @Test
  public void shouldSaveProgramSupported() {
    ProgramProductPrice programSupported = new ProgramProductPrice();
    programProductCostPersistenceHandler.save(programSupported, "user");
    verify(programProductService).save(programSupported);
    assertThat(programSupported.getModifiedBy(), is("user"));
  }
}
