/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.hash.Encoder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.userName;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
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
