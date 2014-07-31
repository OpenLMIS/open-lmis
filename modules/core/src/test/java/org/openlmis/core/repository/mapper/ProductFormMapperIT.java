/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
@Category(IntegrationTests.class)
public class ProductFormMapperIT {

  @Autowired
  private ProductFormMapper pfMapper;

  @Test
  public void shouldInsertProductFormByCode() {
    ProductForm pf = new ProductForm();
    pf.setCode("somecode");
    pf.setDisplayOrder(1);
    pfMapper.insert(pf);
    
    ProductForm retPf = pfMapper.getByCode("somecode");

    assertThat(retPf.getCode(), is(pf.getCode()));
    assertThat(retPf.getDisplayOrder(), is(pf.getDisplayOrder()));
  }

  @Test
  public void shouldUpdateProductFormByCode() {
    // insert seed
    ProductForm pf = new ProductForm();
    pf.setCode("somecode");
    pf.setDisplayOrder(1);
    pfMapper.insert(pf);

    // update seed
    pf.setDisplayOrder(2);
    pfMapper.update(pf);

    // pull from DB and check update worked
    ProductForm pfRet = pfMapper.getByCode("somecode");
    assertThat(pfRet.getDisplayOrder(), is(2));
  }

  @Test
  public void shouldGetProductFormById() throws Exception {
    ProductForm returnedProductForm = pfMapper.getById(1l);
    assertThat(returnedProductForm.getCode(), is("Tablet"));
  }
  
  @Test
  public void shouldGetAllProductForms() throws Exception {
    List<ProductForm> result = pfMapper.getAll();
    assertThat(result.size(), is(17));
  }
  
  @Test
  public void shouldReturnProductFormIdForCode() {
    ProductForm form = pfMapper.getByCode("Capsule");
    assertThat(form.getCode(), is("Capsule"));
    assertThat(form.getDisplayOrder(), is(2));
  }
}
