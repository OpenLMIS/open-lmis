package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

  @Select(value = "SELECT userName, id FROM users WHERE LOWER(userName)=LOWER(#{userName}) AND password=#{password}")
  User selectUserByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

  @Insert(value = {"INSERT INTO users",
    "(userName, password, facilityId, firstName, lastName, employeeId, jobTitle, " +
      "primaryNotificationMethod, officePhone, cellPhone, email, supervisorId, modifiedBy, modifiedDate) VALUES",
    "(#{userName}, #{password}, #{facilityId},#{firstName},#{lastName},#{employeeId},#{jobTitle}," +
      "#{primaryNotificationMethod},#{officePhone},#{cellPhone},#{email},#{supervisor.id}, #{modifiedBy}, DEFAULT)"})
  @Options(useGeneratedKeys = true)
  Integer insert(User user);

  @Select(value = "SELECT id, userName, facilityId, firstName, lastName, employeeId, jobTitle, primaryNotificationMethod, officePhone, cellPhone, email, supervisorId" +
    " FROM users where LOWER(userName) = LOWER(#{userName})")
  @Results(@Result(property = "supervisor.id", column = "supervisorId"))
  User get(String userName);

  @Select(value = "SELECT * FROM users where LOWER(email) = LOWER(#{email})")
  @Results(@Result(property = "supervisor.id", column = "supervisorId"))
  User getByEmail(String email);

  @Select({"SELECT id, userName, facilityId, firstName, lastName, employeeId, jobTitle, primaryNotificationMethod, officePhone, cellPhone, email, supervisorId " +
    "FROM users U INNER JOIN role_assignments RA ON U.id = RA.userId INNER JOIN role_rights RR ON RA.roleId = RR.roleId ",
    "WHERE RA.programId = #{programId} AND RA.supervisoryNodeId = #{supervisoryNodeId} AND RR.rightName = #{right}"})
  @Results(@Result(property = "supervisor.id", column = "supervisorId"))
  List<User> getUsersWithRightInNodeForProgram(@Param("programId") Integer programId, @Param("supervisoryNodeId") Integer supervisoryNodeId, @Param("right") Right right);

  @Select(value = "SELECT id,firstName,lastName,email FROM users where LOWER(firstName) like '%'|| LOWER(#{userSearchParam}) ||'%' OR LOWER(lastName) like '%'|| LOWER(#{userSearchParam}) ||'%' OR LOWER(email) like '%'|| LOWER(#{userSearchParam}) ||'%'")
  List<User> getUserWithSearchedName(String userSearchParam);

  @Update("UPDATE users SET userName = #{userName}, firstName = #{firstName}, lastName = #{lastName}, employeeId = #{employeeId}, jobTitle = #{jobTitle}, " +
    "primaryNotificationMethod = #{primaryNotificationMethod}, officePhone = #{officePhone}, cellPhone = #{cellPhone}, email = #{email}, modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT where id=#{id}")
  void update(User user);

  @Select("SELECT id, userName, firstName, lastName , employeeId, jobTitle, officePhone, primaryNotificationMethod, cellPhone, email FROM users WHERE id=#{id}")
  User getById(Integer id);
}
