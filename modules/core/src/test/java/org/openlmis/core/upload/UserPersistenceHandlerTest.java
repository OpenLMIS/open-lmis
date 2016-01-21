/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mock;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.openlmis.core.upload.UserPersistenceHandler.RESET_PASSWORD_PATH;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
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

