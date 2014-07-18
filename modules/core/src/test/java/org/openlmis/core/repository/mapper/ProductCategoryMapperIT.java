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
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ProductCategoryMapperIT {

  @Autowired
  ProductCategoryMapper mapper;

  @Test
  public void shouldInsertProductCategoryCode() {
    ProductCategory productCategory = new ProductCategory("category code", "category name", 1);
    mapper.insert(productCategory);

    ProductCategory returnedProductCategory = mapper.getById(productCategory.getId());

    assertThat(returnedProductCategory.getId(), is(productCategory.getId()));
    assertThat(returnedProductCategory.getCode(), is(productCategory.getCode()));
    assertThat(returnedProductCategory.getName(), is(productCategory.getName()));
    assertThat(returnedProductCategory.getDisplayOrder(), is(productCategory.getDisplayOrder()));
  }

  @Test
  public void shouldGetProductCategoryByCode() {
    ProductCategory productCategory = new ProductCategory("category code", "category name", 1);
    mapper.insert(productCategory);

    ProductCategory returnedProductCategory = mapper.getByCode(productCategory.getCode());

    assertThat(returnedProductCategory.getId(), is(productCategory.getId()));
    assertThat(returnedProductCategory.getName(), is(productCategory.getName()));
    assertThat(returnedProductCategory.getDisplayOrder(), is(productCategory.getDisplayOrder()));
  }

  @Test
  public void shouldUpdateProductCategory() {
    ProductCategory productCategory = new ProductCategory("category code", "category name", 1);
    productCategory.setModifiedBy(1L);
    mapper.insert(productCategory);

    productCategory.setName("updated category name");
    productCategory.setModifiedBy(2L);
    productCategory.setDisplayOrder(2);
    mapper.update(productCategory);

    ProductCategory returnedProductCategory = mapper.getByCode(productCategory.getCode());

    assertThat(returnedProductCategory.getName(), is(productCategory.getName()));
    assertThat(returnedProductCategory.getModifiedBy(), is(productCategory.getModifiedBy()));
    assertThat(returnedProductCategory.getDisplayOrder(), is(productCategory.getDisplayOrder()));
  }

  @Test
  public void shouldReturnProductCategoryIdByCode() {
    ProductCategory productCategory = new ProductCategory("category code", "category name", 1);
    mapper.insert(productCategory);

    Long categoryId = mapper.getIdByCode(productCategory.getCode());

    assertThat(categoryId, is(productCategory.getId()));
  }

  @Test
  public void shouldGetAll() {
    ProductCategory productCategory = new ProductCategory("category code", "category name", 1);
    mapper.insert(productCategory);

    List<ProductCategory> allCategories = mapper.getAll();

    assertThat(allCategories.size(), is(1));
    assertThat(allCategories.get(0).getName(), is(productCategory.getName()));
    assertThat(allCategories.get(0).getDisplayOrder(), is(productCategory.getDisplayOrder()));
  }
}


