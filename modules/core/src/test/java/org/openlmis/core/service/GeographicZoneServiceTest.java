/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.GeographicZoneRepository;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    geographicZone.setParent(new GeographicZone("xyz", null, null, null));
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
    when(repository.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(
      new GeographicLevel(1L, "abc", "abc", 1));
    when(repository.getByCode(geographicZone.getParent().getCode())).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Geographic Zone Parent Code");

    service.save(geographicZone);
  }

  @Test
  public void shouldThrowAnExceptionIfGeographicLevelCodeIsInvalid() throws Exception {
    when(repository.getByCode(geographicZone.getLevel().getCode())).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Geographic Level Code");

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
}
