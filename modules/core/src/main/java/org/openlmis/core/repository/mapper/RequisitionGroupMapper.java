package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.RequisitionGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupMapper {

    @Insert("INSERT INTO requisition_groups" +
            "(code, name, description, supervisoryNodeId, modifiedBy, modifiedDate) " +
            "values (#{code}, #{name}, #{description}, #{supervisoryNode.id}, #{modifiedBy}, #{modifiedDate}) ")
    @Options(useGeneratedKeys = true)
    Integer insert(RequisitionGroup requisitionGroup);

    @Select("SELECT id, code, name, description, supervisoryNodeId, modifiedBy, modifiedDate " +
            "FROM requisition_groups WHERE id = #{id}")
    @Results(value = {
            @Result(property = "supervisoryNode.id", column = "supervisoryNodeId")
    })
    RequisitionGroup getRequisitionGroupById(Integer id);

    @Select("SELECT id FROM requisition_groups where LOWER(code) = LOWER(#{code})")
    Integer getIdForCode(String code);

    List<RequisitionGroup> getRequisitionGroupBySupervisoryNodes(String supervisoryNodeIdsAsString);
}
