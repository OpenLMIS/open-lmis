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
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.upload.UserPersistenceHandler.RESET_PASSWORD_PATH;

@RunWith(MockitoJUnitRunner.class)
public class UserPersistenceHandlerTest {

  private UserPersistenceHandler userPersistenceHandler;

  @Mock
  private UserService userService;

  @Rule
  public ExpectedException exException = ExpectedException.none();

  String baseUrl;

  @Before
  public void setUp() throws Exception {
    baseUrl = "http://localhost:9091/";
    userPersistenceHandler = new UserPersistenceHandler(userService, baseUrl);
  }

  @Test
  public void shouldSaveAUser() throws Exception {
    User user = new User();
    userPersistenceHandler.save(user);
    verify(userService).create(user, baseUrl + RESET_PASSWORD_PATH);
  }

}
