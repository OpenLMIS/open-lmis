package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

  @Select("SELECT EXISTS (SELECT TRUE FROM users WHERE username=#{userName} AND password=#{password})")
  boolean authenticate(@Param("userName") String userName, @Param("password") String password);

  @Insert(value = {"INSERT INTO users",
      "(userName, password, facilityId) VALUES",
      "(#{userName}, #{password}, #{facilityId})"})
  @Options(useGeneratedKeys = true)
  Integer insert(User user);
}
