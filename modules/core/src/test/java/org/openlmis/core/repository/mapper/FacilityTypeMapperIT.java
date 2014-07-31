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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class FacilityTypeMapperIT {
  @Autowired
  private FacilityTypeMapper mapper;

  private FacilityType facType;

  @Before
  public void setup() {
    // seed a facility type to use for testing
    facType = new FacilityType();
    facType.setCode("someCode");  // this code is used for later testing, don't change
    facType.setName("someName");
    facType.setDescription("someDescription");
    facType.setNominalMaxMonth(1);
    facType.setNominalEop(1D);
    facType.setDisplayOrder(1);
    facType.setActive(true);

    mapper.insert(facType);
  }

  @Test
  public void shouldGetById() {
    FacilityType persistentFacType = mapper.getByCode(facType.getCode());
    FacilityType facTypeFoundById = mapper.getById(persistentFacType.getId());
    assertThat(facTypeFoundById, notNullValue());
    assertThat(facTypeFoundById, is(persistentFacType));
  }

  @Test
  public void shouldUpdateByCodeCaseInsensitive() {
    // test get by code
    FacilityType retFacType = mapper.getByCode(facType.getCode());
    assertThat(retFacType, notNullValue());
    assertThat(retFacType, is(facType));

    // update and test get by code case insensitive
    retFacType.setName("someOtherName");
    mapper.update(retFacType);
    FacilityType updatedFacType = mapper.getByCode(facType.getCode().toUpperCase());
    assertThat(updatedFacType, is(retFacType));
  }

  @Test
  public void shouldGetAll() {
    List<FacilityType> all = mapper.getAll();
    assertThat(all.size(), greaterThan(0));
  }

  @Test
  public void shouldReturnNullIfGetByCodeIsNull() {
    assertThat(mapper.getByCode(null), nullValue());
  }

  @Test
  public void shouldReturnNullIfGetByCodeIsEmptyString() {
    assertThat(mapper.getByCode(""), nullValue());
  }
}
