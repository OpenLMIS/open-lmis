/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.configurationReader.StaticReferenceDataReader;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.StaticReferenceDataController.CURRENCY;
@Category(UnitTests.class)
public class StaticReferenceDataControllerTest {

  private StaticReferenceDataController staticReferenceDataController;

  @Mock
  StaticReferenceDataReader staticReferenceDataReader;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    staticReferenceDataController = new StaticReferenceDataController(staticReferenceDataReader);
  }

  @Test
  public void shouldGetCurrency() throws Exception {
    when(staticReferenceDataReader.getCurrency()).thenReturn("$");
    ResponseEntity<OpenLmisResponse> response = staticReferenceDataController.getCurrency();

    OpenLmisResponse openLmisResponse = response.getBody();
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat((String) openLmisResponse.getData().get(CURRENCY), is("$"));
  }
}
