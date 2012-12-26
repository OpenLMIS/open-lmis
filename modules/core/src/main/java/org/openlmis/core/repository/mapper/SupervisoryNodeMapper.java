package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupervisoryNode;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupervisoryNodeMapper {

    @Select("SELECT * FROM supervisory_nodes where id = #{id}")
    @Results(value = {
            @Result(property = "parent.id", column = "parentId"),
            @Result(property = "facility.id", column = "facilityId")
    })
    SupervisoryNode getSupervisoryNode(Integer id);

    @Insert("INSERT INTO supervisory_nodes " +
            "(code, name, parentId, facilityId, description, modifiedBy, modifiedDate)" +
            " VALUES (#{code}, #{name}, #{parent.id}, #{facility.id}, #{description}, #{modifiedBy}, #{modifiedDate})")
    @Options(useGeneratedKeys = true)
    Integer insert(SupervisoryNode supervisoryNode);

    @Select("SELECT id FROM supervisory_nodes WHERE LOWER(code) = LOWER(#{code})")
    Integer getIdForCode(String code);

    @Select("WITH  recursive  supervisoryNodesRec AS " +
            "   (" +
            "   SELECT *" +
            "   FROM supervisory_nodes " +
            "   WHERE id in  (SELECT s.id FROM  " +
            "       supervisory_nodes s " +
            "       INNER JOIN role_assignments ra ON s.id = ra.supervisoryNodeId  " +
            "       INNER JOIN role_rights rr ON ra.roleId = rr.roleId  " +
            "       WHERE rr.rightName = #{right}  " +
            "       AND ra.userId = #{userId}  " +
            "       AND ra.programId = #{programId}) " +
            "   UNION " +
            "   SELECT sn.* " +
            "   FROM supervisory_nodes sn " +
            "   JOIN supervisoryNodesRec " +
            "   ON sn.parentId = supervisoryNodesRec.id " +
            "   )" +
            "SELECT * FROM supervisoryNodesRec")
    List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(@Param(value = "userId") Integer userId, @Param(value = "programId") Integer programId,
                                                              @Param(value = "right") Right right);
}
