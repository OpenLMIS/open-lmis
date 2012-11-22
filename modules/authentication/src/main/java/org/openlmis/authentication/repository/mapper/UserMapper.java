package org.openlmis.authentication.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.authentication.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    final String SELECT_USER_BY_USER_NAME_AND_PASSWORD = "SELECT * FROM OPEN_LMIS_USER WHERE LOWER(USER_NAME)=LOWER(#{userName}) AND PASSWORD=#{password}";

    @Select(value = SELECT_USER_BY_USER_NAME_AND_PASSWORD)
    @Results(value = {
            @Result(property = "userName", column = "USER_NAME"),
            @Result(property = "role", column = "ROLE")
    })
    User selectUserByUserNameAndPassword(@Param("userName")String userName, @Param("password")String password);

}
