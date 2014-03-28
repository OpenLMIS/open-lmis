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
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
@Category(IntegrationTests.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ProgramProductIsaMapperIT {


  private Product product;
  private Program program;
  @Autowired
  ProgramMapper programMapper;
  @Autowired
  ProductMapper productMapper;

  @Autowired
  private ProgramProductMapper programProductMapper;

  @Autowired
  private ProgramProductIsaMapper mapper;

  @Before
  public void setUp() throws Exception {
    product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);
    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);
  }

  @Test
  public void testUpdate() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    ProgramProductISA programProductISA = new ProgramProductISA(programProduct.getId(), 23d, 4, 10d, 25d, 20, 50, 17);
    mapper.insert(programProductISA);

    programProductISA.setAdjustmentValue(23);
    mapper.update(programProductISA);

    ProgramProductISA returnedIsa = mapper.getIsaByProgramProductId(programProduct.getId());

    assertThat(returnedIsa, is(programProductISA));
  }

  @Test
  public void shouldInsertISAForAProgramProduct() {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    ProgramProductISA programProductISA = new ProgramProductISA(programProduct.getId(), 23d, 4, 10d, 25d, 20, 50, 17);
    mapper.insert(programProductISA);

    ProgramProductISA returnedIsa = mapper.getIsaByProgramProductId(programProduct.getId());

    assertThat(returnedIsa, is(programProductISA));
  }
}
