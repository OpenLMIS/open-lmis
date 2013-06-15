/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.BaseController.UNEXPECTED_EXCEPTION;

@Category(UnitTests.class)
public class BaseControllerTest {

  @Mock
  MessageService messageService;

  @InjectMocks
  BaseController baseController;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void shouldResolveUnhandledException() throws Exception {
    when(messageService.message(UNEXPECTED_EXCEPTION)).thenReturn("Oops, something has gone wrong. Please try again later");
    final ResponseEntity<OpenLmisResponse> response = baseController.handleException(new Exception());
    final OpenLmisResponse body = response.getBody();
    assertThat(body.getErrorMsg(), is("Oops, something has gone wrong. Please try again later"));
  }
}
