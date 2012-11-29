package org.openlmis.authentication.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.authentication.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    @Select(value = "SELECT * FROM users WHERE LOWER(user_name)=LOWER(#{userName}) AND password=#{password}")
    @Results(value = {
            @Result(property = "userName", column = "user_name"),
            @Result(property = "role", column = "role")
    })
    User selectUserByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

}
