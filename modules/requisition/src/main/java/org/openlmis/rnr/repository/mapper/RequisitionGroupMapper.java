package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionGroupMapper {


    @Select("INSERT INTO requisition_group(code,name,description,supervisory_node_id,modified_by) " +
            "values (#{code},#{name},#{description},#{supervisoryNode.id},#{modifiedBy}) returning id")
    @Options(useGeneratedKeys = true)
    Integer insert(RequisitionGroup requisitionGroup);

    @Select("SELECT rg.id, rg.code, rg.name, rg.description, " +
            "rg.supervisory_node_id, rg.modified_by, rg.modified_date FROM " +
            "requisition_group rg WHERE rg.id = #{id}")
    @Results(value={
            @Result(property = "id", column = "id"),
            @Result(property = "code", column = "code"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "supervisoryNode.id", column = "supervisory_node_id"),
            @Result(property = "modifiedBy", column = "modified_by"),
            @Result(property = "modifiedDate", column = "modified_date")
    })
    RequisitionGroup getRequisitionGroupById(int id);
}
