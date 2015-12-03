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
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class GeographicLevelMapperIT {

  @Autowired
  private GeographicLevelMapper mapper;

  private GeographicLevel provGeoLevel;

  @Before
  public void init() {
    provGeoLevel = new GeographicLevel();
    provGeoLevel.setCode("myprov");
    provGeoLevel.setName("Province");
    provGeoLevel.setLevelNumber(2);
    mapper.insert(provGeoLevel);
  }

  @Test
  public void shouldGetLowestGeographicLevel() {
    // get a list of all the geo levels and the lowest level that should be present
    List<GeographicLevel> allGeoLevels = mapper.getAll();
    int lowestLevel = mapper.getLowestGeographicLevel();

    // sort all geo levels by their level number
    Collections.sort(allGeoLevels, new Comparator<GeographicLevel>() {
      public int compare(GeographicLevel o1, GeographicLevel o2) {
        return Integer.compare(o1.getLevelNumber(), o2.getLevelNumber());
      }
    });
    GeographicLevel lowestGeoLevel = allGeoLevels.get(allGeoLevels.size() - 1);

    assertThat(lowestLevel, is(lowestGeoLevel.getLevelNumber()));
  }

  @Test
  public void shouldGetGeographicLevelByCode() throws Exception {
    String code = provGeoLevel.getCode();
    GeographicLevel geographicLevel = mapper.getGeographicLevelByCode(code);
    assertThat(geographicLevel, is(provGeoLevel));
  }

  @Test
  public void shouldReturnAllTheGeoLevels() {
    List<GeographicLevel> levels = mapper.getAll();
    assertThat(levels.size(), not(0));
  }

  @Test
  public void shouldRetrieveGeographicLevelByCode() {
    String code = provGeoLevel.getCode();
    GeographicLevel geoRet = mapper.getByCode(code);

    assertThat(geoRet, notNullValue());
    assertThat(geoRet, is(provGeoLevel));
  }

  @Test
  public void shouldUpdateGeographicLevel() {
    GeographicLevel geoRet = mapper.getByCode(provGeoLevel.getCode());
    geoRet.setName("Some new name");
    geoRet.setLevelNumber(3);

    mapper.update(geoRet);

    GeographicLevel geoUpdated = mapper.getByCode(provGeoLevel.getCode());
    assertThat(geoUpdated, notNullValue());
    assertThat(geoUpdated, is(geoRet));
  }
}
