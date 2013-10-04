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

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.model.AuditFields;

import static org.mockito.Mockito.verify;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductPersistenceHandlerTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private ProductService productService;

  @Mock
  ProgramService programService;

  @InjectMocks
  private ProductPersistenceHandler productPersistenceHandler;

  @Test
  public void shouldSaveImportedProduct() throws Exception {
    Product product = new Product();
    productPersistenceHandler.save(product);
    verify(productService).save(product);
  }

  @Test
  public void shouldNotifyProgramServiceInPostProcess() throws Exception {
    productPersistenceHandler.postProcess(new AuditFields());

    verify(programService).notifyProgramChange();
  }
}



