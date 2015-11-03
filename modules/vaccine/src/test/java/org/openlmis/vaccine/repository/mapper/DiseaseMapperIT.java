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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.vaccine.domain.VaccineDisease;
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
public class DiseaseMapperIT {

  @Autowired
  DiseaseMapper mapper;

  @Test
  public void shouldGetAll() throws Exception {
    List<VaccineDisease> result = mapper.getAll();
    assertThat(result.size(), is(3));
  }

  @Test
  public void shouldInsert() throws Exception {
    VaccineDisease disease = new VaccineDisease();
    disease.setName("Ebola");
    disease.setDisplayOrder(2);
    disease.setDescription("The deadly disease");
    Integer count = mapper.insert(disease);
    assertThat(count, is(notNullValue()));
    assertThat(disease.getId(),is(notNullValue()));
  }

  @Test
  public void shouldUpdate() throws Exception {
    VaccineDisease disease = new VaccineDisease();
    disease.setName("Ebola");
    disease.setDisplayOrder(2);
    disease.setDescription("The deadly disease");
    mapper.insert(disease);

    disease.setDescription("The new Description");
    mapper.update(disease);

    VaccineDisease returnedObject = mapper.getById(disease.getId());
    assertThat(returnedObject.getDescription(), is(disease.getDescription()));
  }

  @Test
  public void shouldGetById() throws Exception {
    VaccineDisease disease = new VaccineDisease();
    disease.setName("Ebola");
    disease.setDisplayOrder(2);
    disease.setDescription("The deadly disease");
    mapper.insert(disease);

    VaccineDisease returnedObject = mapper.getById(disease.getId());
    assertThat(returnedObject.getName(), is(disease.getName()));
    assertThat(returnedObject.getId(), is(disease.getId()));
  }
}