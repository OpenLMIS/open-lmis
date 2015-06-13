/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductFormMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductFormRepositoryTest {

  @Mock
  private ProductFormMapper pfMapper;

  private ProductFormRepository pfRep;  

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    pfRep = new ProductFormRepository(pfMapper);
  }

  @Test
  public void shouldThrowExceptionIfInsertingInValid() {
    ProductForm pf = new ProductForm();
    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.reference.data.missing");
    pfRep.insert(pf);
  }


  @Test
  public void shouldGetAll() {
    pfRep.getAll();
    verify(pfMapper).getAll();
  }
  
  @Test
  public void shouldThrowExceptionIfInsertingDuplicate() {
    ProductForm pf = new ProductForm();
    pf.setCode("somecode");
    pf.setDisplayOrder(2);

    when(pfMapper.getByCode(pf.getCode())).thenReturn(pf);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.duplicate.dosage.unit.code");

    pfRep.insert(pf);
  }

  @Test
  public void ShouldGetByCode() {
    pfRep.getByCode("code");
    verify(pfMapper).getByCode("code");
  }
  
  @Test  
  public void shouldThrowExceptionIfUpdatingInvalid() {
    ProductForm pf = new ProductForm();
    expectedException.expect(DataException.class);
    pfRep.update(pf);
  }
}
