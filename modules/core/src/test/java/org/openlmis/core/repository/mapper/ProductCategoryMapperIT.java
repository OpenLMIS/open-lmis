/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProductCategoryMapperIT {

  @Autowired
  ProductCategoryMapper productCategoryMapper;

  @Test
  public void shouldInsertProductCategoryCode() {
    ProductCategory productCategory = new ProductCategory();
    productCategory.setCode("category code");
    productCategory.setName("category name");
    productCategory.setDisplayOrder(1);
    productCategoryMapper.insert(productCategory);

    ProductCategory returnedProductCategory = productCategoryMapper.getProductCategoryById(productCategory.getId());

    assertThat(returnedProductCategory.getId(), is(productCategory.getId()));
    assertThat(returnedProductCategory.getCode(), is(productCategory.getCode()));
    assertThat(returnedProductCategory.getName(), is(productCategory.getName()));
    assertThat(returnedProductCategory.getDisplayOrder(), is(productCategory.getDisplayOrder()));
  }

  @Test
  public void shouldGetProductCategoryByCode() {
    ProductCategory productCategory = new ProductCategory();
    productCategory.setCode("category code");
    productCategory.setName("category name");
    productCategory.setDisplayOrder(1);
    productCategoryMapper.insert(productCategory);

    ProductCategory returnedProductCategory = productCategoryMapper.getProductCategoryByCode(productCategory.getCode());

    assertThat(returnedProductCategory.getId(), is(productCategory.getId()));
    assertThat(returnedProductCategory.getName(), is(productCategory.getName()));
    assertThat(returnedProductCategory.getDisplayOrder(), is(productCategory.getDisplayOrder()));
  }

  @Test
  public void shouldUpdateProductCategory() {
    ProductCategory productCategory = new ProductCategory();
    productCategory.setCode("category code");
    productCategory.setName("category name");
    productCategory.setModifiedBy(1L);
    productCategory.setDisplayOrder(1);
    productCategoryMapper.insert(productCategory);

    productCategory.setName("updated category name");
    productCategory.setModifiedBy(2L);
    productCategory.setDisplayOrder(2);
    productCategoryMapper.update(productCategory);

    ProductCategory returnedProductCategory = productCategoryMapper.getProductCategoryByCode(productCategory.getCode());

    assertThat(returnedProductCategory.getName(), is(productCategory.getName()));
    assertThat(returnedProductCategory.getModifiedBy(), is(productCategory.getModifiedBy()));
    assertThat(returnedProductCategory.getDisplayOrder(), is(productCategory.getDisplayOrder()));
  }

  @Test
  public void shouldReturnProductCategoryIdByCode() {
    ProductCategory productCategory = new ProductCategory();
    productCategory.setCode("category code");
    productCategory.setName("category name");
    productCategory.setDisplayOrder(1);
    productCategoryMapper.insert(productCategory);

    Long categoryId = productCategoryMapper.getProductCategoryIdByCode(productCategory.getCode());

    assertThat(categoryId, is(productCategory.getId()));
  }

}


