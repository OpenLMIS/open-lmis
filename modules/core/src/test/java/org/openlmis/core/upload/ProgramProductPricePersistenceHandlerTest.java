/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.ProgramProduct;
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
  private ProgramProductPersistenceHandler programProductPersistanceHandler;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    programProductCostPersistenceHandler = new ProgramProductPricePersistenceHandler(programProductService);
    programProductPersistanceHandler = new ProgramProductPersistenceHandler(programProductService);
  }


  @Test
  public void shouldSaveProgramProductPrice() {
    ProgramProductPrice programProductPrice = new ProgramProductPrice();
    programProductCostPersistenceHandler.save(programProductPrice);
    verify(programProductService).updateProgramProductPrice(programProductPrice);
  }

  @Test
  public void shouldInsertProgramProduct() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();

    programProductPersistanceHandler.save(programProduct);

    verify(programProductService).save(programProduct);

  }
}
