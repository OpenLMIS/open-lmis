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
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * UserMapper maps the SupplyLine User to corresponding representation in database. Apart from CRUD provides methods related
 * to password resets, sending email etc.
 */
@Repository
public interface UserMapper {

  @Select(value = "SELECT userName, id, restrictLogin FROM users WHERE LOWER(userName)=LOWER(#{userName}) AND password=#{password} AND verified = TRUE and active = TRUE")
  User selectUserByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

  @Insert({"INSERT INTO users",
    "(userName, facilityId, firstName, lastName, employeeId, restrictLogin, jobTitle,",
          "primaryNotificationMethod, officePhone, cellPhone, email, supervisorId, createdBy, modifiedBy, modifiedDate,createdDate, verified, ismobileuser)",
    "VALUES",
    "(#{userName}, #{facilityId}, #{firstName}, #{lastName}, #{employeeId}, COALESCE(#{restrictLogin}, FALSE), #{jobTitle},",
    "#{primaryNotificationMethod}, #{officePhone}, #{cellPhone}, #{email}, #{supervisor.id}, ",
          "#{createdBy}, #{modifiedBy}, COALESCE(#{modifiedDate}, NOW()),COALESCE(#{modifiedDate}, NOW()), #{verified}, #{isMobileUser})"})
  @Options(useGeneratedKeys = true)
  Integer insert(User user);

  @Select(value = {"SELECT id, userName, facilityId, firstName, lastName, employeeId, restrictLogin, jobTitle, ",
          "primaryNotificationMethod, officePhone, cellPhone, email, supervisorId, verified, active, modifiedDate, ismobileuser",
    " FROM users where LOWER(userName) = LOWER(#{userName}) AND active = TRUE"})
  @Results(
    @Result(property = "supervisor.id", column = "supervisorId")
  )
  User getByUserName(String userName);

  @Select(value = "SELECT * FROM users where LOWER(email) = LOWER(#{email})")
  @Results(@Result(property = "supervisor.id", column = "supervisorId"))
  User getByEmail(String email);

  @Select({"SELECT id, userName, facilityId, firstName, lastName, employeeId, restrictLogin, jobTitle, primaryNotificationMethod, ",
          "officePhone, cellPhone, email, supervisorId ,verified, active, ismobileuser " +
      "FROM users U INNER JOIN role_assignments RA ON U.id = RA.userId INNER JOIN role_rights RR ON RA.roleId = RR.roleId ",
    "WHERE RA.programId = #{program.id} AND COALESCE(RA.supervisoryNodeId, -1) = COALESCE(#{supervisoryNode.id}, -1) AND RR.rightName = #{right}"})
  @Results(@Result(property = "supervisor.id", column = "supervisorId"))
  List<User> getUsersWithRightInNodeForProgram(@Param("program") Program program, @Param("supervisoryNode") SupervisoryNode supervisoryNode,
                                               @Param("right") String right);

  @Update("UPDATE users SET userName = #{userName}, firstName = #{firstName}, lastName = #{lastName}, " +
    "employeeId = #{employeeId},restrictLogin = #{restrictLogin}, facilityId=#{facilityId}, jobTitle = #{jobTitle}, " +
    "primaryNotificationMethod = #{primaryNotificationMethod}, officePhone = #{officePhone}, cellPhone = #{cellPhone}, " +
    "email = #{email}, active = #{active}, " +
          "verified = #{verified}, ismobileuser = #{isMobileUser}, " +
    "modifiedBy = #{modifiedBy}, modifiedDate = (COALESCE(#{modifiedDate}, NOW())) WHERE id=#{id}")
  void update(User user);

  @Select("SELECT id, userName, firstName, lastName, employeeId, restrictLogin, facilityId, jobTitle, officePhone, " +
          "primaryNotificationMethod, cellPhone, email, verified, active, ismobileuser FROM users WHERE id=#{id}")
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
  void updateUserPassword(@Param(value = "userId") Long userId, @Param(value = "password") String password);


  @Update("UPDATE users SET active = FALSE, modifiedBy = #{modifiedBy}, modifiedDate = NOW() WHERE id = #{userId}")
  void disable(@Param(value = "userId") Long userId, @Param(value = "modifiedBy") Long modifiedBy);

  @Select({"SELECT id, userName, facilityId, firstName, lastName, employeeId, restrictLogin, jobTitle, primaryNotificationMethod,",
    "officePhone, cellPhone, email, supervisorId, verified, active from users inner join role_assignments on users.id = role_assignments.userId ",
    "INNER JOIN role_rights ON role_rights.roleId = role_assignments.roleId ",
    "where supervisoryNodeId IN (WITH RECURSIVE supervisoryNodesRec(id, parentId) ",
    "AS (SELECT sn.id, sn.parentId FROM supervisory_nodes AS sn WHERE sn.id = #{nodeId}",
    "UNION ALL ",
    "SELECT c.id, c.parentId  FROM supervisoryNodesRec AS p, supervisory_nodes AS c WHERE p.parentId = c.id)",
    "SELECT id FROM supervisoryNodesRec) ",
    "AND programId = #{programId} AND role_rights.rightName = #{rightName}"})
  List<User> getUsersWithRightInHierarchyUsingBaseNode(@Param(value = "nodeId") Long nodeId, @Param(value = "programId") Long programId, @Param(value = "rightName") String right);

  @Select({"SELECT id, userName, u.facilityId, firstName, lastName, employeeId, restrictLogin, jobTitle, primaryNotificationMethod," +
    "officePhone, cellPhone, email, supervisorId, verified, active FROM users u INNER JOIN fulfillment_role_assignments f ON u.id = f.userId " +
    "INNER JOIN role_rights rr ON f.roleId = rr.roleId",
    "WHERE f.facilityId = #{facilityId} AND rr.rightName = #{rightName}"})
  List<User> getUsersWithRightOnWarehouse(@Param("facilityId") Long facilityId, @Param("rightName") String rightName);

  @Select({"SELECT COUNT(*) FROM users",
    "WHERE LOWER(firstName) LIKE '%'|| LOWER(#{searchParam}) ||'%'",
    "OR LOWER(lastName) LIKE '%' || LOWER(#{searchParam}) ||'%' ",
    "OR LOWER(email) LIKE '%'|| LOWER(#{searchParam}) || '%' ",
    "OR LOWER(username) LIKE '%'|| LOWER(#{searchParam}) ||'%'"})
  Integer getTotalSearchResultCount(String searchParam);

  @Select({"SELECT id, firstName, lastName, email, username, active, verified, ismobileuser FROM users",
    "WHERE LOWER(firstName) LIKE '%'|| LOWER(#{searchParam}) ||'%'",
    "OR LOWER(lastName) LIKE '%' || LOWER(#{searchParam}) ||'%' ",
    "OR LOWER(email) LIKE '%'|| LOWER(#{searchParam}) || '%' ",
    "OR LOWER(username) LIKE '%'|| LOWER(#{searchParam}) ||'%'",
    "ORDER BY LOWER(firstName), LOWER(lastName)"})
  List<User> search(String searchParam, RowBounds rowBounds);

  @Select("select userPreferenceKey as key, value from user_preferences where userId = #{userId} " +
    "UNION " +
    "select key, defaultValue as value from user_preference_master " +
    "   where key not in (select userPreferenceKey from user_preferences where userId = #{userId})")
  List<LinkedHashMap> getPreferences(@Param(value = "userId") Long userId);


  @Select("select * from fn_save_user_preference(#{userId}::int,#{programId}::int,#{facilityId}::int,#{products})")
  String updateUserPreferences(@Param(value = "userId") Long userId, @Param("programId") Long programId, @Param("facilityId") Long facilityId, @Param(value = "products") String products);

  @Select("select distinct rr.rightName " +
    "from  rights r join role_rights rr on r.name = rr.rightName " +
    "   join role_assignments ras on ras.roleid = rr.roleId " +
    "where r.righttype = 'REQUISITION' and ras.userId = #{userId}")
  List<String> getSupervisoryRights(@Param("userId") Long userId);
}
