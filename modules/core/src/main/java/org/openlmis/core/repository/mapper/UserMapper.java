package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    @Select(value = "SELECT * FROM users WHERE LOWER(userName)=LOWER(#{userName}) AND password=#{password}")
    User selectUserByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

    @Select(value = "INSERT INTO users " +
            "(userName, password, facilityId) VALUES " +
            "(#{userName}, #{password}, #{facilityId}) returning id")
    @Options(useGeneratedKeys = true)
    Integer insert(User user);
}
