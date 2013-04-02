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
import org.openlmis.upload.model.AuditFields;

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
    ProgramProductPrice existing = new ProgramProductPrice();
    programProductCostPersistenceHandler.save(existing, programProductPrice, new AuditFields(1, null));
    verify(programProductService).updateProgramProductPrice(programProductPrice);
    assertThat(programProductPrice.getModifiedBy(), is(1));
  }

  @Test
  public void shouldInsertProgramProduct() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();

    ProgramProduct existing = new ProgramProduct();
    programProductPersistanceHandler.save(existing, programProduct, new AuditFields(1, null));

    verify(programProductService).save(programProduct);

  }
}
