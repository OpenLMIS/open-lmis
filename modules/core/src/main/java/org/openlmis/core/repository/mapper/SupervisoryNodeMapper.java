package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.SupervisoryNode;
import org.springframework.stereotype.Repository;

@Repository
public interface SupervisoryNodeMapper {

    @Select("SELECT * FROM supervisory_nodes where id = #{id}")
    @Results(value = {
            @Result(property = "parent.id", column = "parentId"),
            @Result(property = "facility.id", column = "facilityId")
    })
    SupervisoryNode getSupervisoryNode(Integer id);

    @Insert("INSERT INTO supervisory_nodes " +
            "(code, name, parentId, facilityId, approvalPoint, description, modifiedBy, modifiedDate)" +
            " VALUES (#{code}, #{name}, #{parent.id}, #{facility.id}, #{approvalPoint}, #{description}, #{modifiedBy}, #{modifiedDate})")
    @Options(useGeneratedKeys = true)
    Integer insert(SupervisoryNode supervisoryNode);

    @Select("SELECT id FROM supervisory_nodes WHERE LOWER(code) = LOWER(#{code})")
    Integer getIdForCode(String code);
}
