package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.SupervisoryNode;
import org.springframework.stereotype.Repository;

@Repository
public interface SupervisoryNodeMapper {

    @Select("SELECT * FROM supervisory_node where id = #{id}")
    @Results(value = {
            @Result(property = "code", column = "code"),
            @Result(property = "parent.id", column = "parent_id"),
            @Result(property = "facility.id", column = "facility_id"),
            @Result(property = "approvalPoint", column = "approval_point"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "modifiedBy", column = "modified_by"),
            @Result(property = "modifiedDate", column = "modified_date")
    })
    SupervisoryNode getSupervisoryNode(Integer id);

    @Select("INSERT INTO supervisory_node (code, name, parent_id, facility_id, approval_point, description, modified_by, modified_date)" +
            " VALUES (#{code}, #{name}, #{parent.id}, #{facility.id}, #{approvalPoint}, #{description}, #{modifiedBy}, #{modifiedDate}) returning id")
    @Options(useGeneratedKeys = true)
    Integer insert(SupervisoryNode supervisoryNode);

    @Select("SELECT id FROM supervisory_node WHERE LOWER(code) = LOWER(#{code})")
    Integer getIdForCode(String code);
}
