package org.openlmis.rnr.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.RequisitionGroup;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionGroupMapper {


    @Select("INSERT INTO requisition_group(code,name,description,level_id,head_facility_id,parent_id,active,modified_by) " +
            "values (#{code},#{name},#{description},#{levelId}, #{headFacility.id} ,#{parent.id},#{active},#{modifiedBy}) returning id")
    @Options(useGeneratedKeys = true)
    Long insert(RequisitionGroup requisitionGroup);

    @Select("SELECT rg.id, rg.code, rg.name, rg.description, rg.level_id, rg.head_facility_id, " +
            "rg.parent_id, rg.active, rg.modified_by, rg.modified_date FROM " +
            "requisition_group rg WHERE rg.id = #{id}")
    @Results(value={
            @Result(property = "id", column = "id"),
            @Result(property = "code", column = "code"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "levelId", column = "level_id"),
            @Result(property = "headFacility.id", column = "head_facility_id"),
            @Result(property = "parent.id", column = "parent_id"),
            @Result(property = "active", column = "active"),
            @Result(property = "modifiedBy", column = "modified_by"),
            @Result(property = "modifiedDate", column = "modified_date")
    })
    RequisitionGroup getRequisitionGroupById(Long id);
}
