/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.vaccine.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.vaccine.domain.VaccineDose;
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
    assertThat(doses.get(0).getId(), is(dose.getId()));
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
    assertThat(doses.get(0).getId(),is(dose.getId()));
  }

  @Test
  public void shouldGetAllPossibleDoses() throws Exception{
    List<VaccineDose> doses = mapper.getAllDoses();
    assertThat(doses.size(), is(6));
  }

  @Test
  public void shouldDeleteProgramById() throws Exception{
    mapper.deleteByProgram(1L);
  }
}