package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleMapper {
    @Insert("INSERT INTO roles" +
            "(name, description) VALUES " +
            "(#{name}, #{description})")
    @Options(useGeneratedKeys = true)
    int insert(Role role);

    @Select("SELECT id, name, description from roles WHERE id = #{id}")
    Role get(Integer id);
}
