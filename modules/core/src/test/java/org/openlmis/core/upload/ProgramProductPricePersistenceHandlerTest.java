/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramProductPricePersistenceHandlerTest {

  @InjectMocks
  private ProgramProductPricePersistenceHandler programProductCostPersistenceHandler;

  @Mock
  private ProgramProductService programProductService;

  @InjectMocks
  private ProgramProductPersistenceHandler programProductPersistanceHandler;

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
