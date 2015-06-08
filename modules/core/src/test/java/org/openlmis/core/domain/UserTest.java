/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.hash.Encoder;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
@PrepareForTest(Encoder.class)
public class UserTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowErrorOnInvalidEmail() {
    User user = make(a(defaultUser));
    user.setUserName("mohan");
    user.setEmail("invalidEmail@asda121");

    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.email.invalid");

    user.validate();
  }

  @Test
  public void shouldHashThePasswordAndSetPassword() throws Exception {
    mockStatic(Encoder.class);
    when(Encoder.hash("password")).thenReturn("hashedPassword");

    User user = new User();
    user.setPassword("password");

    assertThat(user.getPassword(), is("hashedPassword"));
  }

  @Test
  public void shouldThrowErrorOnInvalidUsername() throws Exception {
    User user = make(a(defaultUser));
    user.setUserName("Mohan Das");
    user.setEmail("abc@gmail.com");

    expectedException.expect(DataException.class);
    expectedException.expectMessage("user.userName.invalid");

    user.validate();
  }

  @Test
  public void shouldSetBasicInformation() throws Exception {
    User user = make(a(defaultUser));
    User expectedUser = new User(user.getId(), user.getUserName());

    User userReturned = user.basicInformation();

    assertThat(userReturned, is(expectedUser));
  }
}
