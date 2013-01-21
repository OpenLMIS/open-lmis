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

public class ProgramProductPricePersistenceHandlerTest {
  private ProgramProductPricePersistenceHandler programProductCostPersistenceHandler;
  @Mock
  private ProgramProductService programProductService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    programProductCostPersistenceHandler = new ProgramProductPricePersistenceHandler(programProductService);
  }


  @Test
  public void shouldSaveProgramProductPrice() {
    ProgramProductPrice programProductPrice = new ProgramProductPrice();
    programProductCostPersistenceHandler.save(programProductPrice, "user");
    verify(programProductService).save(programProductPrice);
    assertThat(programProductPrice.getModifiedBy(), is("user"));
  }
}
