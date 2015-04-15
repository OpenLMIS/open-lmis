/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Maker;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ProductDoseMapperIT {

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  ProductMapper productMapper;

  @Autowired
  ProductDoseMapper mapper;

  Product product;

  Program program;

  @Before
  public void setup(){
    product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);

    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);
  }

  private VaccineProductDose getVaccineProductDose() {
    VaccineProductDose dose = new VaccineProductDose();
    dose.setDoseId(1L);
    dose.setDisplayName("dose 1");
    dose.setDisplayOrder(1L);
    dose.setProgramId(program.getId());
    dose.setProductId(product.getId());
    return dose;
  }

  @Test
  public void shouldGetDoseSettingByProduct() throws Exception {
    VaccineProductDose dose = getVaccineProductDose();

    mapper.insert(dose);

    List<VaccineProductDose> doses = mapper.getDoseSettingByProduct(program.getId(), product.getId());
    assertThat(doses.size(), is(1));
    assertThat(doses.get(0),is(dose));
  }

  @Test
  public void shouldInsert() throws Exception {
    VaccineProductDose dose = getVaccineProductDose();

    Integer result = mapper.insert(dose);

    assertThat(result, is(1));
    assertThat(dose.getId(), is(notNullValue()));
  }



  @Test
  public void shouldUpdate() throws Exception {
    VaccineProductDose dose = getVaccineProductDose();
    Integer result = mapper.insert(dose);

    assertThat(result, is(1));
  }

  @Test
  public void shouldGetProgramProductDoses() throws Exception {
    VaccineProductDose dose = getVaccineProductDose();

    mapper.insert(dose);
    List<VaccineProductDose> doses = mapper.getProgramProductDoses(program.getId());
    assertThat(doses.size(), is(1));
    assertThat(doses.get(0),is(dose));

  }
}