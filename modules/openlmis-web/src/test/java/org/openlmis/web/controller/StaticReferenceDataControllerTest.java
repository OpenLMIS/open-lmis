/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class StaticReferenceDataControllerTest {

  @Mock
  StaticReferenceDataService service;

  @InjectMocks
  StaticReferenceDataController staticReferenceDataController;

  @Test
  public void shouldGetPageSize() throws Exception {

    when(service.getPropertyValue(StaticReferenceDataController.RNR_LINEITEM_PAGE_SIZE)).thenReturn("2");

    ResponseEntity<OpenLmisResponse> response = staticReferenceDataController.getPageSize();

    OpenLmisResponse openLmisResponse = response.getBody();
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat((String) openLmisResponse.getData().get("pageSize"), is("2"));
  }
}
