/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
@Category(UnitTests.class)
public class BaseControllerTest {
  @Test
  public void shouldResolveUnhandledException() throws Exception {
    BaseController baseController = new BaseController();
    final ResponseEntity<OpenLmisResponse> response = baseController.handleException(new Exception());
    final OpenLmisResponse body = response.getBody();
    assertThat(body.getErrorMsg(), is("Oops, something has gone wrong. Please try again later"));
  }
}
