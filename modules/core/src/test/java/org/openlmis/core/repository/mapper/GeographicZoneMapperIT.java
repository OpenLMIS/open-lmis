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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Pagination;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class GeographicZoneMapperIT {

  @Autowired
  private GeographicZoneMapper mapper;

  @Test
  public void shouldSaveGeographicZone() throws Exception {
    GeographicZone geographicZone = new GeographicZone(null, "code", "name",
      new GeographicLevel(2L, "state", "State", 2), null);
    geographicZone.setCatchmentPopulation(10000L);
    geographicZone.setLongitude(333.9874);
    geographicZone.setLatitude(-256.7249);
    Date date = new Date();
    geographicZone.setModifiedDate(date);

    mapper.insert(geographicZone);

    GeographicZone returnedZone = mapper.getGeographicZoneByCode("code");

    assertThat(returnedZone, is(geographicZone));
  }

  @Test
  public void shouldGetGeographicLevelByCode() throws Exception {
    String code = "state";
    GeographicLevel geographicLevel = mapper.getGeographicLevelByCode(code);
    assertThat(geographicLevel.getName(), is("State"));
    assertThat(geographicLevel.getId(), is(2L));
  }

  @Test
  public void shouldGetNullIfZoneNotPresent() throws Exception {
    GeographicZone nullZone = mapper.getGeographicZoneByCode("some random code");

    assertThat(nullZone, is(nullValue()));
  }

  @Test
  public void shouldGetAllGeographicZonesOfLowestLevelExceptRootGeographicZoneSortedByName() throws Exception {
    List<GeographicZone> allGeographicZones = mapper.getAllGeographicZones();
    assertThat(allGeographicZones.size(), is(10));
    GeographicZone geographicZone = allGeographicZones.get(0);

    assertThat(geographicZone.getCode(), is("District1"));
    assertThat(geographicZone.getName(), is("District1"));
    assertThat(geographicZone.getLevel().getName(), is("District"));
    assertThat(geographicZone.getLevel().getLevelNumber(), is(4));
  }

  @Test
  public void shouldGetAllGeographicZonesSortedAndSearchedByParentName() throws Exception {
    GeographicZone geographicZone1 = new GeographicZone(null, "code4", "nameA", new GeographicLevel(1L), null);
    mapper.insert(geographicZone1);

    GeographicZone geographicZone2 = new GeographicZone(null, "code5", "NameB", new GeographicLevel(1L),
      geographicZone1);
    mapper.insert(geographicZone2);

    GeographicZone geographicZone3 = new GeographicZone(null, "code6", "name-c", new GeographicLevel(1L),
      geographicZone2);
    mapper.insert(geographicZone3);

    GeographicZone geographicZone4 = new GeographicZone(null, "code1", "NameD", new GeographicLevel(3L),
      geographicZone1);
    mapper.insert(geographicZone4);

    GeographicZone geographicZone5 = new GeographicZone(null, "code2", "naameD", new GeographicLevel(3L),
      geographicZone1);
    mapper.insert(geographicZone5);

    GeographicZone geographicZone6 = new GeographicZone(null, "code3", "nameE", new GeographicLevel(4L),
      geographicZone3);
    mapper.insert(geographicZone6);

    List<GeographicZone> allGeographicZones = mapper.searchByParentName("ame", new Pagination(1, 10));
    assertThat(allGeographicZones.size(), is(5));

    assertThat(allGeographicZones.get(0).getCode(), is("code5"));
    assertThat(allGeographicZones.get(1).getCode(), is("code6"));
    assertThat(allGeographicZones.get(2).getCode(), is("code2"));
    assertThat(allGeographicZones.get(3).getCode(), is("code1"));
    assertThat(allGeographicZones.get(4).getCode(), is("code3"));
  }

  @Test
  public void shouldReturnSearchByParentNameResultsEqualToPageSize() throws Exception {
    GeographicZone geographicZone1 = new GeographicZone(null, "code1", "nameA", new GeographicLevel(1L), null);
    mapper.insert(geographicZone1);

    mapper.insert(new GeographicZone(null, "code2", "NameB", new GeographicLevel(1L), geographicZone1));
    mapper.insert(new GeographicZone(null, "code3", "name-c", new GeographicLevel(1L), geographicZone1));
    mapper.insert(new GeographicZone(null, "code4", "name-c", new GeographicLevel(1L), geographicZone1));
    mapper.insert(new GeographicZone(null, "code5", "name-c", new GeographicLevel(1L), geographicZone1));
    mapper.insert(new GeographicZone(null, "code6", "name-c", new GeographicLevel(1L), geographicZone1));

    List<GeographicZone> allGeographicZones = mapper.searchByParentName("nameA", new Pagination(1, 3));
    assertThat(allGeographicZones.size(), is(3));
  }

  @Test
  public void shouldReturnSearchByNameResultsEqualToPageSize() throws Exception {
    mapper.insert(new GeographicZone(null, "code2", "nameA", new GeographicLevel(1L), null));
    mapper.insert(new GeographicZone(null, "code3", "nameA", new GeographicLevel(1L), null));
    mapper.insert(new GeographicZone(null, "code4", "nameA", new GeographicLevel(1L), null));
    mapper.insert(new GeographicZone(null, "code5", "nameA", new GeographicLevel(1L), null));
    mapper.insert(new GeographicZone(null, "code6", "name-c", new GeographicLevel(1L), null));

    List<GeographicZone> allGeographicZones = mapper.searchByName("nameA", new Pagination(1, 3));
    assertThat(allGeographicZones.size(), is(3));
  }

  @Test
  public void shouldReturnTotalCountOfSearchByParentNameResults() throws Exception {
    GeographicZone geographicZone1 = new GeographicZone(null, "code1", "nameA", new GeographicLevel(1L), null);
    mapper.insert(geographicZone1);

    mapper.insert(new GeographicZone(null, "code2", "NameB", new GeographicLevel(1L), geographicZone1));
    mapper.insert(new GeographicZone(null, "code3", "name-c", new GeographicLevel(1L), geographicZone1));
    mapper.insert(new GeographicZone(null, "code4", "name-c", new GeographicLevel(1L), geographicZone1));
    mapper.insert(new GeographicZone(null, "code5", "name-c", new GeographicLevel(1L), geographicZone1));
    mapper.insert(new GeographicZone(null, "code6", "name-c", new GeographicLevel(1L), geographicZone1));

    Integer totalRecords = mapper.getTotalParentSearchResultCount("nameA");
    assertThat(totalRecords, is(5));
  }

  @Test
  public void shouldReturnTotalCountOfSearchByNameResults() throws Exception {
    mapper.insert(new GeographicZone(null, "code2", "nameA", new GeographicLevel(1L), null));
    mapper.insert(new GeographicZone(null, "code3", "nameA", new GeographicLevel(1L), null));
    mapper.insert(new GeographicZone(null, "code4", "nameA", new GeographicLevel(1L), null));
    mapper.insert(new GeographicZone(null, "code5", "nameA", new GeographicLevel(1L), null));
    mapper.insert(new GeographicZone(null, "code6", "name-c", new GeographicLevel(1L), null));

    Integer totalRecords = mapper.getTotalSearchResultCount("nameA");
    assertThat(totalRecords, is(4));
  }

  @Test
  public void shouldGetAllGeographicZonesSortedAndSearchedByGeoZoneName() throws Exception {
    GeographicZone geographicZone1 = new GeographicZone(null, "code4", "nameA", new GeographicLevel(1L), null);
    mapper.insert(geographicZone1);

    GeographicZone geographicZone2 = new GeographicZone(null, "code5", "NameB", new GeographicLevel(1L), null);
    mapper.insert(geographicZone2);

    GeographicZone geographicZone3 = new GeographicZone(null, "code6", "namec", new GeographicLevel(1L), null);
    mapper.insert(geographicZone3);

    GeographicZone geographicZone4 = new GeographicZone(null, "code1", "NameD", new GeographicLevel(3L),
      geographicZone1);
    mapper.insert(geographicZone4);

    GeographicZone geographicZone5 = new GeographicZone(null, "code2", "naameD", new GeographicLevel(3L),
      geographicZone1);
    mapper.insert(geographicZone5);

    GeographicZone geographicZone6 = new GeographicZone(null, "code3", "nameE", new GeographicLevel(4L),
      geographicZone3);
    mapper.insert(geographicZone6);

    List<GeographicZone> allGeographicZones = mapper.searchByName("ame", new Pagination(1, 10));
    assertThat(allGeographicZones.size(), is(6));

    assertThat(allGeographicZones.get(0).getCode(), is("code4"));
    assertThat(allGeographicZones.get(1).getCode(), is("code5"));
    assertThat(allGeographicZones.get(2).getCode(), is("code6"));
    assertThat(allGeographicZones.get(3).getCode(), is("code2"));
    assertThat(allGeographicZones.get(4).getCode(), is("code1"));
    assertThat(allGeographicZones.get(5).getCode(), is("code3"));
  }

  @Test
  public void shouldGetGeographicZoneWithParent() throws Exception {
    GeographicZone parent = new GeographicZone(null, "Dodoma", "Dodoma", new GeographicLevel(null, "province", "Province", null), null);
    GeographicZone expectedZone = new GeographicZone(5L, "Ngorongoro", "Ngorongoro",
      new GeographicLevel(null, "district", "District", 4), parent);

    GeographicZone zone = mapper.getWithParentById(5L);

    assertThat(zone, is(expectedZone));
  }

  @Test
  public void shouldUpdateGeographicZone() throws Exception {
    GeographicZone geographicZone = new GeographicZone(null, "code", "name",
      new GeographicLevel(2L, "state", "State", 2), null);
    geographicZone.setLongitude(123.9878);

    mapper.insert(geographicZone);

    geographicZone.setName("new name");
    geographicZone.setLevel(new GeographicLevel(1L, "country", "Country", 1));
    geographicZone.setLongitude(-111.9877);

    mapper.update(geographicZone);

    GeographicZone returnedZone = mapper.getGeographicZoneByCode("code");
    returnedZone.setModifiedDate(null);

    assertThat(returnedZone, is(geographicZone));
  }

  @Test
  public void shouldReturnGeographicZonesAboveGivenLevel() throws Exception {
    GeographicLevel level = new GeographicLevel(4L, "district", "level3", 3);
    List<GeographicZone> geographicZones = mapper.getAllGeographicZonesAbove(level);
    assertThat(geographicZones.size(), is(10));
  }

  @Test
  public void shouldGetGeographicZonesByCodeOrName() {
    GeographicZone geographicZone1 = new GeographicZone(null, "code5", "nameE", new GeographicLevel(1L), null);
    mapper.insert(geographicZone1);

    GeographicZone geographicZone2 = new GeographicZone(null, "code2", "nameB", new GeographicLevel(3L), null);
    mapper.insert(geographicZone2);

    GeographicZone geographicZone3 = new GeographicZone(null, "code3", "nameC", new GeographicLevel(2L), null);
    mapper.insert(geographicZone3);

    GeographicZone geographicZone4 = new GeographicZone(null, "code4", "nameA", new GeographicLevel(4L), null);
    mapper.insert(geographicZone4);

    GeographicZone geographicZone5 = new GeographicZone(null, "code1", "nameD", new GeographicLevel(1L), null);
    mapper.insert(geographicZone5);

    GeographicZone geographicZone6 = new GeographicZone(null, "code6", "gameC", new GeographicLevel(1L), null);
    mapper.insert(geographicZone6);

    String searchParam = "name";

    List<GeographicZone> geographicZones = mapper.getGeographicZonesByCodeOrName(searchParam);

    assertThat(geographicZones.size(), is(5));
    assertThat(geographicZones.get(0).getCode(), is(geographicZone5.getCode()));
    assertThat(geographicZones.get(1).getCode(), is(geographicZone1.getCode()));
    assertThat(geographicZones.get(2).getCode(), is(geographicZone3.getCode()));
    assertThat(geographicZones.get(3).getCode(), is(geographicZone2.getCode()));
    assertThat(geographicZones.get(4).getCode(), is(geographicZone4.getCode()));
  }

  @Test
  public void getGeographicZoneCountBySearchParam() {
    Integer count = mapper.getGeographicZonesCountBy("district");

    assertThat(count, is(9));
  }
}