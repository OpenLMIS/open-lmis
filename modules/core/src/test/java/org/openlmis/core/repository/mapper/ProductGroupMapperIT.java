/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;


import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ProductGroupMapperIT {

  @Autowired
  ProductGroupMapper mapper;

  ProductGroup productGroup;

  @Before
  public void setUp() throws Exception {
    productGroup = new ProductGroup();
    productGroup.setCode("PG1");
    productGroup.setName("Vaccines");
  }


  @Test
  public void shouldInsertProductGroup() throws Exception {
    mapper.insert(productGroup);

    ProductGroup returnedProductGroup = mapper.getByCode(productGroup.getCode());

    assertThat(returnedProductGroup.getName(), is("Vaccines"));
  }

  @Test
  public void shouldUpdateProductGroup() throws Exception {
    mapper.insert(productGroup);

    productGroup.setName("Medicines");
    mapper.update(productGroup);
    ProductGroup returnedProductGroup = mapper.getByCode(productGroup.getCode());

    assertThat(returnedProductGroup.getName(), is("Medicines"));
  }

  @Test
  public void shouldGetProductGroupByCode() throws Exception {
    mapper.insert(productGroup);

    ProductGroup returnedProductGroup = mapper.getByCode(productGroup.getCode());

    assertThat(returnedProductGroup.getName(), is("Vaccines"));
  }
}
