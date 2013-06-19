/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.distribution.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.ProductBuilder.displayOrder;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-distribution.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class FacilityProgramProductMapperIT {

  private Product product;
  private Program program;
  ProgramProduct programProduct;
  Facility facility;

  @Autowired
  ProgramMapper programMapper;
  @Autowired
  ProductMapper productMapper;

  @Autowired
  private ProgramProductMapper programProductMapper;

  @Autowired
  FacilityProgramProductMapper mapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Before
  public void setUp() throws Exception {
    product = make(a(ProductBuilder.defaultProduct, with(displayOrder, 1)));
    productMapper.insert(product);
    program = make(a(defaultProgram));
    programMapper.insert(program);
    programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);
    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
  }

  @Test
  public void shouldGetOverriddenIsaForProgramProductIdAndFacilityId() throws Exception {
    AllocationProgramProduct facilityProgramProduct = new AllocationProgramProduct(programProduct.getId(), facility.getId(), 34, null);
    mapper.insert(facilityProgramProduct);

    Integer overriddenIsa = mapper.getOverriddenIsa(programProduct.getId(), facility.getId());

    assertThat(overriddenIsa, is(34));
  }

  @Test
  public void shouldRemoveFacilityProductMapping() throws Exception {
    AllocationProgramProduct facilityProgramProduct = new AllocationProgramProduct(programProduct.getId(), facility.getId(), 34, null);
    mapper.insert(facilityProgramProduct);

    mapper.removeFacilityProgramProductMapping(programProduct.getId(), facility.getId());

    assertThat(mapper.getOverriddenIsa(programProduct.getId(), facility.getId()), is(nullValue()));
  }
}
