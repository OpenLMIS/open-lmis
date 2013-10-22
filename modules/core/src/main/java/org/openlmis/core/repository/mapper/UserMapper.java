/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

  @Select(value = "SELECT userName, id FROM users WHERE LOWER(userName)=LOWER(#{userName}) AND password=#{password} AND verified = TRUE and active = TRUE AND vendorId=(SELECT id FROM vendors WHERE name = 'openLmis')")
  User selectUserByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

  @Insert({"INSERT INTO users",
    "(userName, facilityId, firstName, lastName, employeeId, jobTitle,",
    "primaryNotificationMethod, officePhone, cellPhone, email, supervisorId, vendorId, createdBy, modifiedBy, modifiedDate,createdDate, verified)",
    "VALUES",
    "(#{userName}, #{facilityId}, #{firstName}, #{lastName}, #{employeeId}, #{jobTitle},",
    "#{primaryNotificationMethod}, #{officePhone}, #{cellPhone}, #{email}, #{supervisor.id}, COALESCE(#{vendorId},(SELECT id FROM vendors WHERE name = 'openLmis')), " ,
      "#{createdBy}, #{modifiedBy}, COALESCE(#{modifiedDate}, NOW()),COALESCE(#{modifiedDate}, NOW()), #{verified})"})
  @Options(useGeneratedKeys = true)
  Integer insert(User user);

  @Select(value = "SELECT id, userName, vendorId,facilityId, firstName, lastName, employeeId, jobTitle, " +
    "primaryNotificationMethod, officePhone, cellPhone, email, supervisorId, verified, active, modifiedDate" +
    " FROM users where LOWER(userName) = LOWER(#{userName}) AND active = TRUE AND " +
    "vendorId=COALESCE(#{vendorId},(SELECT id FROM vendors WHERE name = 'openLmis'))")
  @Results(
    @Result(property = "supervisor.id", column = "supervisorId")
  )
  User getByUsernameAndVendorId(User user);

  @Select(value = "SELECT * FROM users where LOWER(email) = LOWER(#{email})")
  @Results(@Result(property = "supervisor.id", column = "supervisorId"))
  User getByEmail(String email);

  @Select({"SELECT id, userName, facilityId, firstName, lastName, employeeId, jobTitle, primaryNotificationMethod, " ,
    "officePhone, cellPhone, email, supervisorId ,verified, active " +
    "FROM users U INNER JOIN role_assignments RA ON U.id = RA.userId INNER JOIN role_rights RR ON RA.roleId = RR.roleId ",
    "WHERE RA.programId = #{program.id} AND RA.supervisoryNodeId = #{supervisoryNode.id} AND RR.rightName = #{right}"})
  @Results(@Result(property = "supervisor.id", column = "supervisorId"))
  List<User> getUsersWithRightInNodeForProgram(@Param("program") Program program, @Param("supervisoryNode") SupervisoryNode supervisoryNode,
                                               @Param("right") Right right);

  @Select(value = "SELECT id, firstName, lastName, email, username, active FROM users WHERE LOWER(firstName) LIKE '%'|| LOWER(#{userSearchParam}) ||'%' OR LOWER(lastName) LIKE '%'|| " +
    "LOWER(#{userSearchParam}) ||'%' OR LOWER(email) LIKE '%'|| LOWER(#{userSearchParam}) ||'%' OR LOWER(username) LIKE '%'|| LOWER(#{userSearchParam}) ||'%'")
  List<User> getUserWithSearchedName(String userSearchParam);





  @Update("UPDATE users SET userName = #{userName}, firstName = #{firstName}, lastName = #{lastName}, " +
    "employeeId = #{employeeId},facilityId=#{facilityId}, jobTitle = #{jobTitle}, " +
    "primaryNotificationMethod = #{primaryNotificationMethod}, officePhone = #{officePhone}, cellPhone = #{cellPhone}, " +
    "email = #{email}, active = #{active}, " +
    "modifiedBy = #{modifiedBy}, modifiedDate = (COALESCE(#{modifiedDate}, NOW())) WHERE id=#{id}")
  void update(User user);

  @Select("SELECT id, userName, firstName, lastName, employeeId, facilityId, jobTitle, officePhone, " +
    "primaryNotificationMethod, cellPhone, email, verified, active FROM users WHERE id=#{id}")
  User getById(Long id);

  @Insert("INSERT INTO user_password_reset_tokens (userId, token) VALUES (#{user.id}, #{token})")
  void insertPasswordResetToken(@Param(value = "user") User user, @Param(value = "token") String token);

  @Select("SELECT userId FROM user_password_reset_tokens WHERE token = #{token}")
  Long getUserIdForPasswordResetToken(String token);

  @Delete("DELETE FROM user_password_reset_tokens WHERE userId = #{userId}")
  void deletePasswordResetTokenForUser(Long userId);

  @Update("UPDATE users SET password = #{password}, verified = TRUE WHERE id = #{userId}")
  void updateUserPasswordAndVerify(@Param(value = "userId") Long userId, @Param(value = "password") String password);

  @Insert("INSERT INTO email_notifications(receiver, subject, content) VALUES (#{receiver}, #{subject}, #{content})")
  int insertEmailNotification(@Param(value = "receiver") String receiver, @Param(value = "subject") String subject,
                              @Param(value = "content") String content);

  @Update("UPDATE users SET password = #{password} WHERE id = #{userId}")
  void updateUserPassword(@Param(value = "userId")Long userId, @Param(value = "password")String password);


  @Update("UPDATE users SET active = FALSE, modifiedBy = #{modifiedBy}, modifiedDate = NOW() WHERE id = #{userId}")
  void disable(@Param(value = "userId") Long userId, @Param(value = "modifiedBy") Long modifiedBy);

  @Select("SELECT id, userName, firstName, lastName, employeeId, facilityId, jobTitle, officePhone, " +
    "primaryNotificationMethod, cellPhone, email, verified, active FROM users WHERE userName=#{userName}")
  User getByUserName(String userName);
}
