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
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProductBuilder.displayOrder;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProgramProductPriceMapperIT {

  @Autowired
  ProgramMapper programMapper;
  @Autowired
  ProductMapper productMapper;
  @Autowired
  ProgramProductMapper programProductMapper;
  @Autowired
  private ProgramProductPriceMapper programProductPriceMapper;

  private Product product;
  private Program program;
  ProgramProduct programProduct;

  @Before
  public void setup() {
    product = make(a(ProductBuilder.defaultProduct, with(displayOrder, 1)));
    productMapper.insert(product);
    program = make(a(defaultProgram));
    programMapper.insert(program);
    Money price = new Money("105.60");
    programProduct = new ProgramProduct(program, product, 10, true, price);
    programProductMapper.insert(programProduct);
  }

  @Test
  public void shouldCloseLastActivePriceWithEndDateAsCurrentDate() throws Exception {
    String source = "MoH";
    Money pricePerDosage = new Money("1.50");
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, pricePerDosage, source);
    programProductPrice.setModifiedBy(1L);

    programProductPriceMapper.insertNewCurrentPrice(programProductPrice);
    programProductPrice.setModifiedBy(2L);
    programProductPriceMapper.closeLastActivePrice(programProductPrice);
    ProgramProductPrice result = programProductPriceMapper.getById(programProductPrice.getId());
    assertThat(result.getEndDate(), is(notNullValue()));
    assertThat(result.getModifiedBy(), is(2L));
  }

  @Test
  public void shouldInsertNewActivePriceWithStartDateAsCurrentDate() throws Exception {
    String source = "MoH";
    Money pricePerDosage = new Money("1.50");
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, pricePerDosage, source);
    programProductPrice.setModifiedBy(1L);
    programProductPriceMapper.insertNewCurrentPrice(programProductPrice);
    ProgramProductPrice result = programProductPriceMapper.getById(programProductPrice.getId());
    assertThat(result.getEndDate(), is(nullValue()));
    assertThat(result.getStartDate(), is(notNullValue()));
    assertThat(result.getModifiedBy(), is(1L));
    assertThat(result.getPricePerDosage(), is(pricePerDosage));
    assertThat(result.getProgramProduct().getCurrentPrice(), is(programProduct.getCurrentPrice()));
  }

  @Test
  public void shouldGetProgramProductPriceForAProgramProduct() throws Exception {
    String source = "MoH";
    Money pricePerDosage = new Money("1.50");
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, pricePerDosage, source);
    programProductPrice.setModifiedBy(1L);

    programProductPriceMapper.insertNewCurrentPrice(programProductPrice);

    ProgramProductPrice programProductPriceReturned = programProductPriceMapper.get(programProduct);

    assertThat(programProductPriceReturned.getProgramProduct().getCurrentPrice(), is(programProduct.getCurrentPrice()));
  }
}
