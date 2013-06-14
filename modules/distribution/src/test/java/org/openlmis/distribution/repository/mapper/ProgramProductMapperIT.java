/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.repository.mapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProductBuilder.displayOrder;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;



public class ProgramProductMapperIT {

  private Product product;
  private Program program;
  @Autowired
  ProgramMapper programMapper;
  @Autowired
  ProductMapper productMapper;

  @Autowired
  private ProgramProductMapper programProductMapper;

  private AllocationProgramProductMapper mapper;

  @Before
  public void setup() {
    product = make(a(ProductBuilder.defaultProduct, with(displayOrder, 1)));
    productMapper.insert(product);
    program = make(a(defaultProgram));
    programMapper.insert(program);
  }


  @Test
  public void shouldInsertISAForAProgramProduct() {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    ProgramProductISA programProductISA = new ProgramProductISA(0.039f, 4, 10f, 25f, 50, 17);
    mapper.insertISA(programProductISA);

    assertNotNull(programProductISA.getId());
  }

  @Test
  public void shouldGetProgramProductISAById() {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    ProgramProductISA programProductISA = new ProgramProductISA(0.039f, 4, 10f, 25f, 50, 17);
    mapper.insertISA(programProductISA);

    ProgramProductISA savedISA = mapper.getISAById(programProductISA.getId());

    assertNotNull(savedISA.getModifiedDate());
    savedISA.setModifiedDate(null);
    assertThat(savedISA, is(programProductISA));
  }

  @Test
  public void shouldGetProgramProductWithISAByProgram() {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    ProgramProductISA programProductISA = new ProgramProductISA(0.039f, 4, 10f, 25f, 50, 17);
    mapper.insertISA(programProductISA);
    mapper.updateProgramProductForISA(programProduct.getId(), programProductISA);

    List<AllocationProgramProduct> savedProgramProducts = mapper.getWithISAByProgram(program.getId());

    assertThat(savedProgramProducts.size(), is(1));
    Assert.assertNotNull(savedProgramProducts.get(0));
  }

  @Test
  public void shouldUpdateProgramProductISA() {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    ProgramProductISA programProductISA = new ProgramProductISA(0.039f, 4, 10f, 25f, 50, 17);
    mapper.insertISA(programProductISA);

    programProductISA.setWhoRatio(0.50f);
    programProductISA.setDosesPerYear(5);
    programProductISA.setWastageRate(5f);
    programProductISA.setBufferPercentage(10f);
    programProductISA.setMinimumValue(25);
    programProductISA.setAdjustmentValue(40);

    mapper.updateISA(programProductISA);

    ProgramProductISA savedProgramProductISA = mapper.getISAById(programProductISA.getId());

    Assert.assertNotNull(savedProgramProductISA.getModifiedDate());
    savedProgramProductISA.setModifiedDate(null);
    assertThat(savedProgramProductISA, is(programProductISA));

  }

  @Test
  public void shouldUpdateProgramProductForISA() {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    ProgramProductISA programProductISA = new ProgramProductISA(0.039f, 4, 10f, 25f, 50, 17);
    mapper.insertISA(programProductISA);

    mapper.updateProgramProductForISA(programProduct.getId(), programProductISA);

    List<AllocationProgramProduct> savedProgramProduct = mapper.getWithISAByProgram(program.getId());

    assertThat(savedProgramProduct.get(0).getProgramProductISA(), is(programProductISA));
  }
}
