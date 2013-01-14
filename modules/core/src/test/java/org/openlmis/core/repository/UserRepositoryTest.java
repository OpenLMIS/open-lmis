package org.openlmis.core.repository;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.UserBuilder;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.UserMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;
import static org.openlmis.core.repository.UserRepository.*;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

  @Rule
  public ExpectedException exException = none();

  @Mock
  UserMapper userMapper;

  UserRepository userRepository;

  @Before
  public void setUp() throws Exception {
    userRepository = new UserRepository(userMapper);
  }

  @Test
  public void shouldGetUsersWithRightInNodeForProgram() throws Exception {
    List<User> users = new ArrayList<>();
    when(userMapper.getUsersWithRightInNodeForProgram(1, 1, APPROVE_REQUISITION)).thenReturn(users);

    List<User> result = userRepository.getUsersWithRightInNodeForProgram(1, 1, APPROVE_REQUISITION);

    verify(userMapper).getUsersWithRightInNodeForProgram(1, 1, APPROVE_REQUISITION);
    assertThat(result, is(users));
  }

  @Test
  public void shouldInsertAUser() throws Exception {
    User user = new User();
    userRepository.insert(user);
    verify(userMapper).insert(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserIfSupervisorIdDoesNotExist() throws Exception {
    User user = make(a(UserBuilder.defaultUser));
    when(userMapper.get(UserBuilder.defaultSupervisorUserName)).thenReturn(null);
    exException.expect(DataException.class);
    exException.expectMessage(SUPERVISOR_USER_NOT_FOUND);
    userRepository.insert(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserOnDuplicateEmployeeId() throws Exception {
    User user = make(a(UserBuilder.defaultUser));
    when(userMapper.get(user.getSupervisor().getUserName())).thenReturn(mock(User.class));
    doThrow(new DuplicateKeyException("duplicate key value violates unique constraint \"uc_users_employeeId\"")).when(userMapper).insert(user);

    exException.expect(DataException.class);
    exException.expectMessage(DUPLICATE_EMPLOYEE_ID_FOUND);
    userMapper.get(user.getSupervisor().getUserName());
    userRepository.insert(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserOnDuplicateEmail() throws Exception {
    User user = make(a(UserBuilder.defaultUser));
    when(userMapper.get(user.getSupervisor().getUserName())).thenReturn(mock(User.class));
    doThrow(new DuplicateKeyException("duplicate key value violates unique constraint \"uc_users_email\"")).when(userMapper).insert(user);

    exException.expect(DataException.class);
    exException.expectMessage(DUPLICATE_EMAIL_FOUND);
    userMapper.get(user.getSupervisor().getUserName());
    userRepository.insert(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserOnDuplicateUserName() throws Exception {
    User user = make(a(UserBuilder.defaultUser));
    when(userMapper.get(user.getSupervisor().getUserName())).thenReturn(mock(User.class));
    doThrow(new DuplicateKeyException("duplicate key value violates unique constraint \"uc_users_userName\"")).when(userMapper).insert(user);

    exException.expect(DataException.class);
    exException.expectMessage(DUPLICATE_USER_NAME_FOUND);
    userMapper.get(user.getSupervisor().getUserName());
    userRepository.insert(user);
  }

  @Test
  public void shouldReturnUserWithValidUsername() {
    String username = "Admin";
    User user = make(a(UserBuilder.defaultUser, with(UserBuilder.email, "John_Doe@openlmis.com")));
    when(userMapper.get(username)).thenReturn(user);

    User returnedUser = userRepository.getByUsername(username);

    assertThat(returnedUser, is(user));
  }

  @Test
  public void shouldReturnUserIdWithValidUserEmail() throws Exception {

    String email = "abc@openlmis.org";
    User expectedUser = new User();
    when(userMapper.getByEmail(email)).thenReturn(expectedUser);

    User returnedUser = userRepository.getByEmail(email);

    assertThat(expectedUser,is(returnedUser));

  }
}
