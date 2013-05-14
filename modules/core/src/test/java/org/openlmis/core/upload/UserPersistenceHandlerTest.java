/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.upload.UserPersistenceHandler.RESET_PASSWORD_PATH;
import static org.powermock.api.mockito.PowerMockito.whenNew;
@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(UserPersistenceHandler.class)
public class UserPersistenceHandlerTest {

  private UserPersistenceHandler userPersistenceHandler;

  @Mock
  private UserService userService;

  @Rule
  public ExpectedException exException = ExpectedException.none();

  String baseUrl;

  private ArrayList users;

  @Before
  public void setUp() throws Exception {
    baseUrl = "http://localhost:9091/";
  }

  @Test
  public void shouldSaveAUser() throws Exception {
    User user = new User();
    user.setEmail("abc@def.com");
    user.setId(1l);
    userPersistenceHandler = new UserPersistenceHandler(userService, baseUrl);
    userPersistenceHandler.save(user);
    verify(userService).createUser(user, baseUrl + RESET_PASSWORD_PATH);
  }
}

