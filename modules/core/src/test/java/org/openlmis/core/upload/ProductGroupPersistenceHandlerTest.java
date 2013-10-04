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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.service.ProductGroupService;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductGroupPersistenceHandlerTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  private ProductGroupPersistenceHandler productGroupPersistenceHandler;

  @Mock
  private ProductGroupService productGroupService;

  @Before
  public void setUp() throws Exception {
    productGroupPersistenceHandler = new ProductGroupPersistenceHandler(productGroupService);
  }

  @Test
  public void shouldSaveImportedProductGroup() throws Exception {
    ProductGroup productGroup = new ProductGroup();
    productGroupPersistenceHandler.save(productGroup);
    verify(productGroupService).save(productGroup);
  }
}
