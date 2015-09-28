/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.repository.UserRepository;
import org.openlmis.email.exception.EmailException;
import org.openlmis.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Exposes the services for handling User entity.
 */

@Service
public class UserService {

  static final String USER_EMAIL_NOT_FOUND = "user.email.not.found";
  static final String USER_EMAIL_INCORRECT = "user.email.incorrect";
  static final String PASSWORD_RESET_TOKEN_INVALID = "user.password.reset.token.invalid";
  static final String USER_USERNAME_INCORRECT = "user.username.incorrect";
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private EmailService emailService;
  @Autowired
  private RoleAssignmentService roleAssignmentService;
  @Autowired
  private MessageService messageService;

  public static String getCommaSeparatedIds(List<Long> idList) {

    return idList == null ? "{}" : idList.toString().replace("[", "").replace("]", "").replace(", ", ",");
  }

  @Transactional
  public void create(User user, String resetPasswordLink) {
    save(user);
    if (!user.isMobileUser()) {
      sendUserCreationEmail(user, resetPasswordLink);
    }
  }

  public void createUser(User user, String passwordResetLink) {
    save(user);
    if (!user.isMobileUser()) {
      prepareForEmailNotification(user, passwordResetLink);
    }
  }

  @Transactional
  public void update(User user) {
    user.validate();
    userRepository.update(user);
    roleAssignmentService.saveRolesForUser(user);
  }

  public LinkedHashMap getPreferences(Long userId){
   List<LinkedHashMap> preferences =  userRepository.getPreferences(userId);
   LinkedHashMap preference = new LinkedHashMap();
   // transform the shape of the list
   for(LinkedHashMap map: preferences){
     preference.put(map.get("key"), map.get("value"));
   }

   return preference;
  }

  public void sendForgotPasswordEmail(User user, String resetPasswordLink) {
    user = getValidatedUser(user);

    userRepository.deletePasswordResetTokenForUser(user.getId());

    String subject = messageService.message("forgot.password.email.subject");
    SimpleMailMessage emailMessage = createEmailMessage(user, resetPasswordLink, subject);

    sendEmail(emailMessage);
  }

  public List<User> searchUser(String searchParam, Pagination pagination) {
    return userRepository.searchUser(searchParam,pagination);
  }

  public User getUserWithRolesById(Long id) {
    User user = userRepository.getById(id);
    user.setHomeFacilityRoles(roleAssignmentService.getHomeFacilityRoles(id));
    user.setSupervisorRoles(roleAssignmentService.getSupervisorRoles(id));
    user.setAdminRole(roleAssignmentService.getAdminRole(id));
    user.setReportingRole(roleAssignmentService.getReportingRole(id));
    user.setAllocationRoles(roleAssignmentService.getAllocationRoles(id));
    user.setReportRoles(roleAssignmentService.getReportRole(id));
    user.setFulfillmentRoles(roleAssignmentService.getFulfilmentRoles(id));
    return user;
  }

  public User getById(Long id) {
    return userRepository.getById(id);
  }

  public Long getUserIdByPasswordResetToken(String token) {
    Long userId = userRepository.getUserIdForPasswordResetToken(token);
    if (userId == null) {
      throw new DataException(PASSWORD_RESET_TOKEN_INVALID);
    }
    return userId;
  }

  @Transactional
  public void updateUserPassword(String token, String password) {
    Long userId = getUserIdByPasswordResetToken(token);
    userRepository.updateUserPasswordAndActivate(userId, Encoder.hash(password));
    userRepository.deletePasswordResetTokenForUser(userId);
  }

  public User getByUserName(String userName) {
    return userRepository.getByUserName(userName);
  }

  public User selectUserByUserNameAndPassword(String userName, String password) {
    return userRepository.selectUserByUserNameAndPassword(userName, password);
  }

  private void prepareForEmailNotification(User user, String passwordResetLink) {
    String subject = messageService.message("account.created.email.subject");
    SimpleMailMessage emailMessage = createEmailMessage(user, passwordResetLink, subject);
    userRepository.insertEmailNotification(emailMessage);
  }

  public void updateUserPassword(Long userId, String password) {
    userRepository.updateUserPassword(userId, Encoder.hash(password));
  }

  @Transactional
  public void disable(Long userId, Long modifiedBy) {
    userRepository.disable(userId, modifiedBy);
    userRepository.deletePasswordResetTokenForUser(userId);
  }

  public List<User> getUsersWithRightInNodeForProgram(Program program, SupervisoryNode node, String rightName) {
    return userRepository.getUsersWithRightInNodeForProgram(program, node, rightName);
  }

  public List<User> getUsersWithRightInHierarchyUsingBaseNode(Long nodeId, Program program, String rightName) {
    return userRepository.getUsersWithRightInHierarchyUsingBaseNode(nodeId, program.getId(), rightName);
  }

  public List<User> getUsersWithRightOnWarehouse(Long id, String rightName) {
    return userRepository.getUsersWithRightOnWarehouse(id, rightName);
  }

  private void sendUserCreationEmail(User user, String resetPasswordLink) {
    String subject = messageService.message("account.created.email.subject");
    SimpleMailMessage emailMessage = createEmailMessage(user, resetPasswordLink, subject);
    sendEmail(emailMessage);
  }

  private void save(User user) {
    user.validate();
    userRepository.create(user);
    roleAssignmentService.saveRolesForUser(user);
  }

  private void sendEmail(SimpleMailMessage emailMessage) {
    try {
      emailService.queueMessage(emailMessage);
    } catch (EmailException e) {
      throw new DataException(USER_EMAIL_NOT_FOUND);
    }
  }

  private User getValidatedUser(User user) {
    if (user.getEmail() != null && !user.getEmail().equals("")) {
      user = userRepository.getByEmail(user.getEmail());
      if (user == null || !user.getActive()) throw new DataException(USER_EMAIL_INCORRECT);
    } else {
      user = userRepository.getByUserName(user.getUserName());
      if (user == null || !user.getActive()) throw new DataException(USER_USERNAME_INCORRECT);
    }
    return user;
  }

  private SimpleMailMessage createEmailMessage(User user, String resetPasswordLink, String subject) {
    String passwordResetToken = generateUUID();
    String[] passwordResetLink = new String[]{user.getFirstName(), user.getLastName(), user.getUserName(), resetPasswordLink + passwordResetToken};
    String mailBody = messageService.message("password.reset.email.body", (Object[]) passwordResetLink);

    userRepository.insertPasswordResetToken(user, passwordResetToken);

    SimpleMailMessage emailMessage = new SimpleMailMessage();
    emailMessage.setSubject(subject);
    emailMessage.setText(mailBody);
    emailMessage.setTo(user.getEmail());

    return emailMessage;
  }

  private String generateUUID() {
    return Encoder.hash(UUID.randomUUID().toString());
  }

  public ArrayList<User> filterForActiveUsers(List<User> userList) {
    Set<User> users = new LinkedHashSet<>(userList);
    CollectionUtils.filter(users, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((User) o).getActive();
      }
    });
    return new ArrayList<>(users);
  }

  public Integer getTotalSearchResultCount(String searchParam) {
    return userRepository.getTotalSearchResultCount(searchParam);
  }

  @Transactional
  public String updateUserPreferences(Long userId, User user, Long programId, Long facilityId, List<Long> products) {
    return userRepository.updateUserPreferences(userId, user, programId, facilityId, getCommaSeparatedIds(products));

  }

  public List<String> getSupervisoryRights(Long userId) {
    return userRepository.getSupervisoryRights(userId);
  }
}