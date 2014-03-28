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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

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
    product = make(a(ProductBuilder.defaultProduct));
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
    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct(programProduct, facility.getId(), 34);
    mapper.insert(facilityProgramProduct);

    Integer overriddenIsa = mapper.getOverriddenIsa(programProduct.getId(), facility.getId());

    assertThat(overriddenIsa, is(34));
  }

  @Test
  public void shouldRemoveFacilityProductMapping() throws Exception {
    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct(programProduct, facility.getId(), 34);
    mapper.insert(facilityProgramProduct);

    mapper.removeFacilityProgramProductMapping(programProduct.getId(), facility.getId());

    assertThat(mapper.getOverriddenIsa(programProduct.getId(), facility.getId()), is(nullValue()));
  }

}
