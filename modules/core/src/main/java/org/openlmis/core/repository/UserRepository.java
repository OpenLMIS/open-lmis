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

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * UserRepository is Repository class for User related database operations.
 */

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

  public List<User> getUsersWithRightInNodeForProgram(Program program, SupervisoryNode node, String rightName) {
    return userMapper.getUsersWithRightInNodeForProgram(program, node, rightName);
  }

  public List<User> getUsersWithRightInHierarchyUsingBaseNode(Long nodeId, Long programId, String rightName) {
    return userMapper.getUsersWithRightInHierarchyUsingBaseNode(nodeId, programId, rightName);
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
  }

  private void validateAndSetSupervisor(User user) {
    User supervisor = null;

    if (user.getSupervisor() != null && user.getSupervisor().getUserName() != null
      && !user.getSupervisor().getUserName().isEmpty()) {

      supervisor = userMapper.getByUserName(user.getSupervisor().getUserName());
      if (supervisor == null)
        throw new DataException(new OpenLmisMessage(SUPERVISOR_USER_NOT_FOUND));
    }

    user.setSupervisor(supervisor);
  }

  public User getByEmail(String email) {
    return userMapper.getByEmail(email);
  }

  public List<User> searchUser(String searchParam, Pagination pagination) {
    return userMapper.search(searchParam, pagination);
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

  public void updateUserPasswordAndActivate(Long userId, String password) {
    userMapper.updateUserPasswordAndVerify(userId, password);
  }

  public User selectUserByUserNameAndPassword(String userName, String password) {
    return userMapper.selectUserByUserNameAndPassword(userName, password);
  }

  public void insertEmailNotification(SimpleMailMessage emailMessage) {
    userMapper.insertEmailNotification(emailMessage.getTo()[0], emailMessage.getSubject(), emailMessage.getText());
  }

  public void updateUserPassword(Long userId, String password) {
    userMapper.updateUserPassword(userId, password);
  }

  public void disable(Long userId, Long modifiedBy) {
    userMapper.disable(userId, modifiedBy);
  }

  public User getByUserName(String userName) {
    return userMapper.getByUserName(userName);
  }

  public List<User> getUsersWithRightOnWarehouse(Long id, String rightName) {
    return userMapper.getUsersWithRightOnWarehouse(id, rightName);
  }

  public Integer getTotalSearchResultCount(String searchParam) {
    return userMapper.getTotalSearchResultCount(searchParam);
  }

  public List<LinkedHashMap> getPreferences(Long userId){
    return userMapper.getPreferences(userId);
  }

  public String updateUserPreferences(Long userId, User user, Long programId, Long facilityId, String products) {
     userMapper.update(user);
     return userMapper.updateUserPreferences(userId, programId, facilityId, products);
  }

  public List<String> getSupervisoryRights(Long userId) {
    return userMapper.getSupervisoryRights(userId);
  }
}
