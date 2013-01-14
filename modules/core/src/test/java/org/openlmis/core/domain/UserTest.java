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
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
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
}
