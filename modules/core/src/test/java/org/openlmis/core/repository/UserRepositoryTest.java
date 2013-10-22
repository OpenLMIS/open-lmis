/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.UserMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.email;
import static org.openlmis.core.builder.UserBuilder.userName;
import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;
import static org.openlmis.core.repository.UserRepository.*;

@Category(UnitTests.class)
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
    SupervisoryNode node = new SupervisoryNode(1L);
    Program program = new Program(1L);
    when(userMapper.getUsersWithRightInNodeForProgram(program, node, APPROVE_REQUISITION)).thenReturn(users);

    List<User> result = userRepository.getUsersWithRightInNodeForProgram(program, node, APPROVE_REQUISITION);

    verify(userMapper).getUsersWithRightInNodeForProgram(program, node, APPROVE_REQUISITION);
    assertThat(result, is(users));
  }

  @Test
  public void shouldInsertAUser() throws Exception {
    User user = new User();
    userRepository.create(user);
    verify(userMapper).insert(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserIfSupervisorIdDoesNotExist() throws Exception {
    User user = make(a(defaultUser));
    when(userMapper.getByUsername(user)).thenReturn(null);
    exException.expect(DataException.class);
    exException.expectMessage(SUPERVISOR_USER_NOT_FOUND);
    userRepository.create(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserOnDuplicateEmployeeId() throws Exception {
    User user = make(a(defaultUser));
    when(userMapper.getByUsername(user.getSupervisor())).thenReturn(mock(User.class));
    doThrow(new DuplicateKeyException("duplicate key value violates unique constraint \"uc_users_employeeId\"")).when(userMapper).insert(user);

    exException.expect(DataException.class);
    exException.expectMessage(DUPLICATE_EMPLOYEE_ID_FOUND);
    userMapper.getByUsername(user.getSupervisor());
    userRepository.create(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserOnDuplicateEmail() throws Exception {
    User user = make(a(defaultUser));
    when(userMapper.getByUsername(user.getSupervisor())).thenReturn(mock(User.class));
    doThrow(new DuplicateKeyException("duplicate key value violates unique constraint \"uc_users_email\"")).when(userMapper).insert(user);

    exException.expect(DataException.class);
    exException.expectMessage(DUPLICATE_EMAIL_FOUND);
    userMapper.getByUsername(user.getSupervisor());
    userRepository.create(user);
  }

  @Test
  public void shouldThrowExceptionAndNotInsertUserOnDuplicateUserName() throws Exception {
    User user = make(a(defaultUser));
    when(userMapper.getByUsername(user.getSupervisor())).thenReturn(mock(User.class));
    doThrow(new DuplicateKeyException("duplicate key value violates unique constraint \"uc_users_userName\"")).when(userMapper).insert(user);

    exException.expect(DataException.class);
    exException.expectMessage(DUPLICATE_USER_NAME_FOUND);
    userMapper.getByUsername(user.getSupervisor());
    userRepository.create(user);
  }

  @Test
  public void shouldReturnUserWithValidUsername() {
    String username = "Admin";
    User user = make(a(defaultUser, with(email, "John_Doe@openlmis.com")));
    user.setUserName(username);
    when(userMapper.getByUsername(user)).thenReturn(user);

    User returnedUser = userRepository.getByUsername(user);

    assertThat(returnedUser, is(user));
  }

  @Test
  public void shouldReturnUserIdWithValidUserEmail() throws Exception {

    String email = "abc@openlmis.org";
    User expectedUser = new User();
    when(userMapper.getByEmail(email)).thenReturn(expectedUser);

    User returnedUser = userRepository.getByEmail(email);

    assertThat(expectedUser, is(returnedUser));

  }

  @Test
  public void shouldReturnUserIfUserExistsWithSearchCriteria() throws Exception {
    String userSearchParam = "abc";
    User user = new User();
    List<User> listOfUsers = new ArrayList<User>();
    listOfUsers.add(user);

    when(userMapper.getUserWithSearchedName(userSearchParam)).thenReturn(listOfUsers);

    List<User> listOfUsersReturned = userRepository.searchUser(userSearchParam);

    assertTrue(listOfUsersReturned.contains(user));
  }

  @Test
  public void shouldReturnMessageIfNoUserExistsWithTheSearchCriteria() throws Exception {
    String userSearchParam = "xyz";
    when(userMapper.getUserWithSearchedName(userSearchParam)).thenReturn(null);

    List<User> userList = userRepository.searchUser(userSearchParam);

    assertThat(userList, is(nullValue()));
  }

  @Test
  public void shouldUpdateUserIfUserDataIsValid() throws Exception {
    User user = new User();
    user.setId(1L);
    userRepository.update(user);
    verify(userMapper).update(user);
  }

  @Test
  public void shouldReturnUserIfIdIsValid() throws Exception {
    User user = new User();

    when(userMapper.getById(1L)).thenReturn(user);

    User userReturned = userRepository.getById(1L);

    assertThat(userReturned, is(user));
  }

  @Test
  public void getUserByUserName() throws Exception {
    String userName = "userName";
    User user = new User();
    when(userMapper.getByUserName(userName)).thenReturn(user);

    User userReturned = userRepository.getByUserName(userName);

    assertThat(userReturned, is(user));
  }

  @Test
  public void shouldReturnUserIdForPasswordResetTokens() throws Exception {
    String passwordResetToken = "test";
    when(userMapper.getUserIdForPasswordResetToken(passwordResetToken)).thenReturn(1L);

    assertThat(userRepository.getUserIdForPasswordResetToken(passwordResetToken), is(1L));
  }


  @Test
  public void shouldUpdateUserIfUserWithUserNameAlreadyExist() throws Exception {
    User user = make(a(defaultUser));
    user.setUserName("userBeingUploaded");
    user.setId(1L);

    User supervisorUser = new User();

    when(userMapper.getByUsername(user)).thenReturn(user);
    when(userMapper.getByUsername(user.getSupervisor())).thenReturn(supervisorUser);

    userRepository.update(user);

    verify(userMapper).update(user);
  }

  @Test
  public void shouldUpdateUserPasword(){
    Long userId = 1l;
    String password = "newPassword";

    userRepository.updateUserPassword(userId, password);

    verify(userMapper).updateUserPassword(userId, password);
  }

  @Test
  public void shouldSetActiveForUser(){
    Long userId = 3l;
    Long modifiedBy = 1L;

    userRepository.disable(userId, modifiedBy);

    verify(userMapper).disable(userId, modifiedBy);
  }

}
