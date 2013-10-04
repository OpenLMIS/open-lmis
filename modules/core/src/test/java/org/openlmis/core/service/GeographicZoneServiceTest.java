/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class GeographicZoneServiceTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  GeographicZoneRepository repository;

  @InjectMocks
  GeographicZoneService service;

  GeographicZone geographicZone;
  public static final String ROOT_GEOGRAPHIC_ZONE_CODE = "Root";
  public static final String ROOT_GEOGRAPHIC_ZONE_NAME = "Root";


  @Before
  public void setUp() throws Exception {
    geographicZone = new GeographicZone();
    geographicZone.setCode("some code");
    geographicZone.setModifiedDate(new Date());
    geographicZone.setLevel(new GeographicLevel(null, "abc", null, null));
    geographicZone.setParent(new GeographicZone(1L, "xyz", null, null, null));
  }

  @Test
  public void shouldSaveGeographicZone() throws Exception {
    when(repository.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(
        new GeographicLevel(1L, "abc", "abc", 1));
    when(repository.getByCode(geographicZone.getParent().getCode())).thenReturn(
        new GeographicZone(1L, "xyz", "xyz", null, null));

    service.save(geographicZone);

    verify(repository).getGeographicLevelByCode("abc");
    verify(repository).getByCode("xyz");
    assertThat(geographicZone.getLevel().getId(), is(1L));
    assertThat(geographicZone.getParent().getId(), is(1L));
    verify(repository).insert(geographicZone);
  }

  @Test
  public void shouldThrowAnExceptionIfParentCodeIsInvalid() throws Exception {
    when(repository.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(new GeographicLevel(1L, "abc", "abc", 1));
    when(repository.getByCode(geographicZone.getParent().getCode())).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.geo.zone.parent.invalid"));

    service.save(geographicZone);
  }

  @Test
  public void shouldThrowAnExceptionIfGeographicLevelCodeIsInvalid() throws Exception {
    when(repository.getByCode(geographicZone.getLevel().getCode())).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.geo.level.invalid"));

    service.save(geographicZone);
  }

  @Test
  public void shouldSetRootAsParentIfParentIsNull() throws Exception {
    GeographicZone expected = new GeographicZone(1L, "Root", "Root", null, null);
    when(repository.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(
        new GeographicLevel(1L, "abc", "abc", 1));
    when(repository.getByCode(ROOT_GEOGRAPHIC_ZONE_CODE)).thenReturn(expected);
    geographicZone.setParent(null);

    service.save(geographicZone);

    assertThat(geographicZone.getParent().getCode(), is(ROOT_GEOGRAPHIC_ZONE_CODE));
    assertThat(geographicZone.getParent().getName(), is(ROOT_GEOGRAPHIC_ZONE_NAME));
  }

  @Test
  public void shouldUpdateZoneIfZonePreviouslyPresent() throws Exception {
    GeographicLevel level = new GeographicLevel(1L, "abc", "abc", 1);
    GeographicZone parent = new GeographicZone(1L, "xyz", "xyz", null, null);

    when(repository.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(level);
    when(repository.getByCode(geographicZone.getParent().getCode())).thenReturn(parent);

    geographicZone.setId(1L);
    service.save(geographicZone);

    verify(repository).update(geographicZone);
    assertThat(geographicZone.getLevel(), is(level));
    assertThat(geographicZone.getParent(), is(parent));
  }

  @Test
  public void shouldGetGeoZoneById() throws Exception {
    GeographicZone expected = new GeographicZone(1L, "Root", "Root", null, null);
    when(repository.getById(1L)).thenReturn(expected);

    GeographicZone actual = service.getById(1L);

    assertThat(actual, is(expected));
    verify(repository).getById(1L);
  }
}
