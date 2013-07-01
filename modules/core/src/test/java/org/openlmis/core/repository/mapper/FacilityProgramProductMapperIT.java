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
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.core.domain.AllocationProgramProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
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
    product = make(a(ProductBuilder.defaultProduct, with(displayOrder, 2)));
    productMapper.insert(product);
    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);
    programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);
    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
  }

  @Test
  public void shouldGetOverriddenIsaForProgramProductIdAndFacilityId() throws Exception {
    AllocationProgramProduct facilityProgramProduct = new AllocationProgramProduct(programProduct, facility.getId(), 34);
    mapper.insert(facilityProgramProduct);

    Integer overriddenIsa = mapper.getOverriddenIsa(programProduct.getId(), facility.getId());

    assertThat(overriddenIsa, is(34));
  }

  @Test
  public void shouldRemoveFacilityProductMapping() throws Exception {
    AllocationProgramProduct facilityProgramProduct = new AllocationProgramProduct(programProduct, facility.getId(), 34);
    mapper.insert(facilityProgramProduct);

    mapper.removeFacilityProgramProductMapping(programProduct.getId(), facility.getId());

    assertThat(mapper.getOverriddenIsa(programProduct.getId(), facility.getId()), is(nullValue()));
  }

  @Test
  public void shouldGetProgramProductForFacilityAndProgram() throws Exception {
    AllocationProgramProduct facilityProgramProduct1 = new AllocationProgramProduct(programProduct, facility.getId(), 34);
    Product product2 = make(a(ProductBuilder.defaultProduct,with(ProductBuilder.code, "P1000"), with(displayOrder, 1)));
    productMapper.insert(product2);
    ProgramProduct programProduct2 = new ProgramProduct(program, product2, 10, true);
    programProductMapper.insert(programProduct2);
    AllocationProgramProduct facilityProgramProduct2 = new AllocationProgramProduct(programProduct2, facility.getId(), 34);
    mapper.insert(facilityProgramProduct1);
    mapper.insert(facilityProgramProduct2);

    List<AllocationProgramProduct> allocationProgramProducts = mapper.getByFacilityAndProgram(facility.getId(), program.getId());

    assertThat(allocationProgramProducts.size(),is(2));
    assertThat(allocationProgramProducts.get(0).getProduct().getCode(),is("P1000"));
    assertThat(allocationProgramProducts.get(1).getProduct().getCode(),is("P999"));
  }
}
