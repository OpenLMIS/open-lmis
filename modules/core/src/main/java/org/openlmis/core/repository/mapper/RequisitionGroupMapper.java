package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.RequisitionGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupMapper {

    @Select("INSERT INTO requisition_groups" +
            "(code, name, description, supervisoryNodeId, modifiedBy, modifiedDate) " +
            "values (#{code}, #{name}, #{description}, #{supervisoryNode.id}, #{modifiedBy}, #{modifiedDate}) " +
            "returning id")
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
