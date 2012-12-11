package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    @Select(value = "SELECT * FROM users WHERE LOWER(user_name)=LOWER(#{userName}) AND password=#{password}")
    @Results(value = {
            @Result(property = "userName", column = "user_name"),
            @Result(property = "role", column = "role")
    })
    User selectUserByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

    @Select(value = "INSERT INTO users " +
            "(user_name, password, facility_id) VALUES " +
            "(#{userName}, #{password}, #{facilityId}) returning id")
    @Options(useGeneratedKeys = true)
    Long insert(User user);

    @Delete(value = "DELETE FROM users")
    void deleteAll();

}
