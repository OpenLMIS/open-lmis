/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

  @Select(value = "SELECT userName, id FROM users WHERE LOWER(userName)=LOWER(#{userName}) AND password=#{password} AND active = TRUE AND vendorId=(SELECT id FROM vendors WHERE name = 'openLmis')")
  User selectUserByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

  @Insert({"INSERT INTO users",
    "(userName, facilityId, firstName, lastName, employeeId, jobTitle,",
    "primaryNotificationMethod, officePhone, cellPhone, email, supervisorId, vendorId, createdBy, modifiedBy, modifiedDate,createdDate)",
    "VALUES",
    "(#{userName}, #{facilityId}, #{firstName}, #{lastName}, #{employeeId}, #{jobTitle},",
    "#{primaryNotificationMethod}, #{officePhone}, #{cellPhone}, #{email}, #{supervisor.id}, COALESCE(#{vendorId},(SELECT id FROM vendors WHERE name = 'openLmis')), #{createdBy}, #{modifiedBy}, COALESCE(#{modifiedDate}, NOW()),COALESCE(#{modifiedDate}, NOW()))"})
  @Options(useGeneratedKeys = true)
  Integer insert(User user);

  @Select(value = "SELECT id, userName, vendorId,facilityId, firstName, lastName, employeeId, jobTitle, primaryNotificationMethod, officePhone, cellPhone, email, supervisorId, modifiedDate" +
    " FROM users where LOWER(userName) = LOWER(#{userName}) AND vendorId=COALESCE(#{vendorId},(SELECT id FROM vendors WHERE name = 'openLmis'))")
  @Results(
    @Result(property = "supervisor.id", column = "supervisorId")
  )
  User getByUsernameAndVendorId(User user);

  @Select(value = "SELECT * FROM users where LOWER(email) = LOWER(#{email})")
  @Results(@Result(property = "supervisor.id", column = "supervisorId"))
  User getByEmail(String email);

  @Select({"SELECT id, userName, facilityId, firstName, lastName, employeeId, jobTitle, primaryNotificationMethod, officePhone, cellPhone, email, supervisorId " +
    "FROM users U INNER JOIN role_assignments RA ON U.id = RA.userId INNER JOIN role_rights RR ON RA.roleId = RR.roleId ",
    "WHERE RA.programId = #{program.id} AND RA.supervisoryNodeId = #{supervisoryNode.id} AND RR.rightName = #{right}"})
  @Results(@Result(property = "supervisor.id", column = "supervisorId"))
  List<User> getUsersWithRightInNodeForProgram(@Param("program") Program program, @Param("supervisoryNode") SupervisoryNode supervisoryNode,
                                               @Param("right") Right right);

  @Select(value = "SELECT id, firstName, lastName, email, username FROM users WHERE LOWER(firstName) LIKE '%'|| LOWER(#{userSearchParam}) ||'%' OR LOWER(lastName) LIKE '%'|| " +
    "LOWER(#{userSearchParam}) ||'%' OR LOWER(email) LIKE '%'|| LOWER(#{userSearchParam}) ||'%' OR LOWER(username) LIKE '%'|| LOWER(#{userSearchParam}) ||'%'")
  List<User> getUserWithSearchedName(String userSearchParam);

  @Update("UPDATE users SET userName = #{userName}, firstName = #{firstName}, lastName = #{lastName}, employeeId = #{employeeId},facilityId=#{facilityId}, jobTitle = #{jobTitle}, " +
    "primaryNotificationMethod = #{primaryNotificationMethod}, officePhone = #{officePhone}, cellPhone = #{cellPhone}, email = #{email}, modifiedBy = #{modifiedBy}, modifiedDate = (COALESCE(#{modifiedDate}, NOW())) WHERE id=#{id}")
  void update(User user);

  @Select("SELECT id, userName, firstName, lastName, employeeId, facilityId, jobTitle, officePhone, primaryNotificationMethod, cellPhone, email FROM users WHERE id=#{id}")
  User getById(Long id);

  @Insert("INSERT INTO user_password_reset_tokens (userId, token) VALUES (#{user.id}, #{token})")
  void insertPasswordResetToken(@Param(value = "user") User user, @Param(value = "token") String token);

  @Select("SELECT userId FROM user_password_reset_tokens WHERE token = #{token}")
  Long getUserIdForPasswordResetToken(String token);

  @Delete("DELETE FROM user_password_reset_tokens WHERE userId = #{userId}")
  void deletePasswordResetTokenForUser(Long userId);

  @Update("UPDATE users SET password = #{password}, active = TRUE WHERE id = #{userId}")
  void updateUserPasswordAndActivate(@Param(value = "userId") Long userId, @Param(value = "password") String password);

  @Insert("INSERT INTO email_notifications(receiver, subject, content) VALUES (#{receiver}, #{subject}, #{content})")
  int insertEmailNotification(@Param(value = "receiver") String receiver, @Param(value = "subject") String subject,
                              @Param(value = "content") String content);

  @Update("UPDATE users SET password = #{password} WHERE id = #{userId}")
  void updateUserPassword(@Param(value = "userId")Long userId, @Param(value = "password")String password);
}
