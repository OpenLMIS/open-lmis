/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.repository.mapper.UserMapper;
import org.openlmis.email.domain.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class UserRepository {

  private UserMapper userMapper;

  public static final String USER_DATA_LENGTH_INCORRECT = "user.data.length.incorrect";
  public static final String SUPERVISOR_USER_NOT_FOUND = "supervisor.user.not.found";
  public static final String DUPLICATE_EMPLOYEE_ID_FOUND = "error.duplicate.employee.id";
  public static final String DUPLICATE_EMAIL_FOUND = "error.duplicate.email";
  public static final String DUPLICATE_USER_NAME_FOUND = "error.duplicate.user.name";

  @Autowired
  public UserRepository(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  public List<User> getUsersWithRightInNodeForProgram(Program program, SupervisoryNode node, Right right) {
    return userMapper.getUsersWithRightInNodeForProgram(program, node, right);
  }

  public void create(User user) {
    validateAndSetSupervisor(user);
    try {
      userMapper.insert(user);
    } catch (DuplicateKeyException e) {
      handleException(e);
    } catch (DataIntegrityViolationException e) {
      throw new DataException(USER_DATA_LENGTH_INCORRECT);
    }
  }

  public void update(User user) {
    validateAndSetSupervisor(user);
    try {
      userMapper.update(user);
    } catch (DuplicateKeyException e) {
      handleException(e);
    } catch (DataIntegrityViolationException e) {
      throw new DataException(USER_DATA_LENGTH_INCORRECT);
    }
  }

  private void handleException(DuplicateKeyException e) {
    String message = e.getMessage().toLowerCase();
    if (message.contains("duplicate key value violates unique constraint \"uc_users_employeeId\"".toLowerCase()))
      throw new DataException(new OpenLmisMessage(DUPLICATE_EMPLOYEE_ID_FOUND));
    if (message.contains("duplicate key value violates unique constraint \"uc_users_email\"".toLowerCase()))
      throw new DataException(new OpenLmisMessage(DUPLICATE_EMAIL_FOUND));
    if (message.contains("duplicate key value violates unique constraint \"uc_users_userName\"".toLowerCase()))
      throw new DataException(new OpenLmisMessage(DUPLICATE_USER_NAME_FOUND));
    if (message.contains(" duplicate key value violates unique constraint \"uc_users_username_vendor\"".toLowerCase())) //TODO:Change the message key after vendor is added on upload
      throw new DataException(new OpenLmisMessage(DUPLICATE_USER_NAME_FOUND));
  }

  private void validateAndSetSupervisor(User user) {
    User supervisor = null;

    if (user.getSupervisor() != null && user.getSupervisor().getUserName() != null
        && !user.getSupervisor().getUserName().isEmpty()) {

      supervisor = userMapper.getByUsernameAndVendorId(user.getSupervisor());
      if (supervisor == null)
        throw new DataException(new OpenLmisMessage(SUPERVISOR_USER_NOT_FOUND));
    }

    user.setSupervisor(supervisor);
  }

  public User getByEmail(String email) {
    return userMapper.getByEmail(email);
  }

  public User getByUsernameAndVendorId(User user) {
    return userMapper.getByUsernameAndVendorId(user);
  }

  public List<User> searchUser(String userSearchParam) {
    return userMapper.getUserWithSearchedName(userSearchParam);
  }

  public User getById(Long id) {
    return userMapper.getById(id);
  }

  public void insertPasswordResetToken(User user, String passwordResetToken) {
    userMapper.insertPasswordResetToken(user, passwordResetToken);
  }

  public Long getUserIdForPasswordResetToken(String token) {
    return userMapper.getUserIdForPasswordResetToken(token);
  }

  public void deletePasswordResetTokenForUser(Long userId) {
    userMapper.deletePasswordResetTokenForUser(userId);
  }

  public void updateUserPassword(Long userId, String password) {
    userMapper.updateUserPassword(userId, password);
  }

  public User selectUserByUserNameAndPassword(String userName, String password) {
    return userMapper.selectUserByUserNameAndPassword(userName, password);
  }

  public void insertEmailNotification(EmailMessage emailMessage) {
    userMapper.insertEmailNotification(emailMessage);
  }
}
