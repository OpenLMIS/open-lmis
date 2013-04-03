/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.service.GeographicZoneService;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GeographicZonePersistenceHandlerTest {

  private GeographicZonePersistenceHandler geographicZonePersistenceHandler;
  @Mock
  private GeographicZoneService service;
  @Rule
  public ExpectedException expectedEx  = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    geographicZonePersistenceHandler = new GeographicZonePersistenceHandler(service);
  }

  @Test
  public void shouldSaveGeographicZoneTaggedWithUserIdAndModifiedDate() throws Exception {
    GeographicZone geographicZone = new GeographicZone();
    geographicZonePersistenceHandler.save(geographicZone);
    verify(service).save(geographicZone);
  }

}
