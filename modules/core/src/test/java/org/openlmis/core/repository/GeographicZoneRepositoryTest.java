/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.mapper.GeographicLevelMapper;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class GeographicZoneRepositoryTest {

  GeographicZoneRepository repository;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private GeographicZoneMapper mapper;

  @Mock
  private GeographicLevelMapper geographicLevelMapper;

  private GeographicZone geographicZone;

  @Before
  public void setUp() throws Exception {
    repository = new GeographicZoneRepository(mapper, geographicLevelMapper);
    geographicZone = new GeographicZone();
    geographicZone.setCode("some code");
    geographicZone.setModifiedDate(new Date());
    geographicZone.setLevel(new GeographicLevel("abc", null, null));
    geographicZone.setParent(new GeographicZone(1L, "xyz", null, null, null));
  }

  @Test
  public void shouldThrowErrorIfIncorrectDataLengthWhileInserting() throws Exception {
    expectedEx.expect(dataExceptionMatcher("error.incorrect.length"));

    doThrow(new DataIntegrityViolationException("Incorrect Data Length")).when(mapper).insert(geographicZone);

    repository.save(geographicZone);
  }

  @Test
  public void shouldThrowErrorIfDuplicateZoneInsert() throws Exception {
    expectedEx.expect(dataExceptionMatcher("error.duplicate.geographic.zone.code"));

    doThrow(new DuplicateKeyException("duplicate key")).when(mapper).insert(geographicZone);

    repository.save(geographicZone);
  }

  @Test
  public void shouldThrowErrorIfIncorrectDataLengthWhileUpdating() throws Exception {
    geographicZone.setId(1l);

    expectedEx.expect(dataExceptionMatcher("error.incorrect.length"));

    doThrow(new DataIntegrityViolationException("Incorrect Data Length")).when(mapper).update(geographicZone);

    repository.save(geographicZone);
  }

  @Test
  public void shouldGetZoneByCode() throws Exception {
    GeographicZone expected = new GeographicZone();
    when(mapper.getGeographicZoneByCode("code")).thenReturn(expected);

    GeographicZone zone = repository.getByCode("code");

    assertThat(expected, is(zone));
    verify(mapper).getGeographicZoneByCode("code");
  }

  @Test
  public void shouldGetLowestGeographicLevel() {
    when(geographicLevelMapper.getLowestGeographicLevel()).thenReturn(1);
    assertThat(repository.getLowestGeographicLevel(), is(1));
  }

  @Test
  public void shouldGetLevelByCode() throws Exception {
    GeographicLevel level = new GeographicLevel();
    when(mapper.getGeographicLevelByCode("code")).thenReturn(level);

    GeographicLevel actualLevel = repository.getGeographicLevelByCode("code");

    assertThat(actualLevel, is(level));
    verify(mapper).getGeographicLevelByCode("code");
  }

  @Test
  public void shouldGetGeographicZoneById() {
    GeographicZone expectedGeographicZone = new GeographicZone();
    Long geographicZoneId = 1l;
    when(mapper.getWithParentById(geographicZoneId)).thenReturn(expectedGeographicZone);

    GeographicZone actualGeographicZone = repository.getById(geographicZoneId);

    assertThat(actualGeographicZone, is(expectedGeographicZone));
    verify(mapper).getWithParentById(geographicZoneId);
  }
}
