/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RequisitionGroupMapper maps the RequisitionGroup entity to corresponding representation in database. Also provides
 * methods like getting all requisition groups for a supervisory node, getting requisition group for a program and facility.
 */

@Repository
public interface RequisitionGroupMapper {

  @Insert("INSERT INTO requisition_groups" +
    "(code, name, description, supervisoryNodeId, createdBy, modifiedBy, modifiedDate) " +
    "VALUES (#{code}, #{name}, #{description}, #{supervisoryNode.id}, #{createdBy}, #{createdBy}, COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
  @Options(useGeneratedKeys = true)
  Integer insert(RequisitionGroup requisitionGroup);

  @Select(
    {"SELECT RG.id, RG.code, RG.name, RG.description, RG.supervisoryNodeId, SN.name AS supervisoryNodeName, SN.code AS supervisoryNodeCode",
      "FROM requisition_groups RG LEFT JOIN supervisory_nodes SN ON RG.supervisoryNodeId = SN.id WHERE RG.id = #{id}"})
  @Results(value = {
    @Result(property = "supervisoryNode.id", column = "supervisoryNodeId"),
    @Result(property = "supervisoryNode.code", column = "supervisoryNodeCode"),
    @Result(property = "supervisoryNode.name", column = "supervisoryNodeName")
  })
  RequisitionGroup getRequisitionGroupById(Long id);

  @Select("SELECT id FROM requisition_groups where LOWER(code) = LOWER(#{code})")
  Long getIdForCode(String code);

  @Select("SELECT id FROM requisition_groups where supervisoryNodeId = ANY(#{supervisoryNodeIdsAsString}::INTEGER[])")
  List<RequisitionGroup> getRequisitionGroupBySupervisoryNodes(String supervisoryNodeIdsAsString);

  @Select("SELECT * " +
    "FROM requisition_groups rg " +
    "INNER JOIN requisition_group_program_schedules rgps ON rg.id = rgps.requisitionGroupId " +
    "INNER JOIN requisition_group_members rgm ON rgps.requisitionGroupId = rgm.requisitionGroupId " +
    "WHERE rgps.programId = #{program.id} " +
    "AND RGM.facilityId = #{facility.id}")
  RequisitionGroup getRequisitionGroupForProgramAndFacility(@Param(value = "program") Program program,
                                                            @Param(value = "facility") Facility facility);

  @Select("SELECT * FROM requisition_groups where LOWER(code) = LOWER(#{code})")
  @Results(value = {
    @Result(property = "supervisoryNode.id", column = "supervisoryNodeId")
  })
  RequisitionGroup getByCode(String code);

  @Update({"UPDATE requisition_groups",
    "SET code = #{code}, name = #{name}, description =  #{description}, supervisoryNodeId = #{supervisoryNode.id},",
    "modifiedBy = #{modifiedBy}, modifiedDate = COALESCE(#{modifiedDate}, NOW())",
    "WHERE id = #{id}"})
  void update(RequisitionGroup requisitionGroup);

  @Select({"SELECT RG.id AS requisitionGroupId, RG.code AS requisitionGroupCode, RG.name AS requisitionGroupName, SN.name AS supervisoryNodeName,",
    "(SELECT COUNT(*) FROM requisition_group_members RGM INNER JOIN facilities F ON F.id = RGM.facilityId",
    "WHERE RG.id = RGM.requisitionGroupId AND F.enabled = true GROUP BY requisitionGroupId) AS memberCount",
    "FROM requisition_groups RG LEFT JOIN supervisory_nodes SN ON SN.id = RG.supervisoryNodeId",
    "WHERE LOWER(RG.name) LIKE '%'|| LOWER(#{searchParam}) ||'%'",
    "ORDER BY LOWER(SN.name), LOWER(RG.Name) NULLS LAST"})
  @Results(value = {
    @Result(property = "supervisoryNode.name", column = "supervisoryNodeName"),
    @Result(property = "id", column = "requisitionGroupId"),
    @Result(property = "name", column = "requisitionGroupName"),
    @Result(property = "code", column = "requisitionGroupCode"),
    @Result(property = "memberCount", column = "memberCount")
  })
  List<RequisitionGroup> searchByGroupName(@Param(value = "searchParam") String searchParam, RowBounds rowBounds);

  @Select(
    {"SELECT RG.id AS requisitionGroupId, RG.code AS requisitionGroupCode, RG.name AS requisitionGroupName, SN.name AS supervisoryNodeName,",
      "(SELECT COUNT(*) FROM requisition_group_members RGM INNER JOIN facilities F ON F.id = RGM.facilityId",
      "WHERE RG.id = RGM.requisitionGroupId AND F.enabled = true GROUP BY requisitionGroupId) AS memberCount",
      "FROM requisition_groups RG INNER JOIN supervisory_nodes SN ON SN.id = RG.supervisoryNodeId",
      "WHERE LOWER(SN.name) LIKE '%'|| LOWER(#{searchParam}) ||'%'",
      "ORDER BY LOWER(SN.name), LOWER(RG.Name)"})
  @Results(value = {
    @Result(property = "supervisoryNode.name", column = "supervisoryNodeName"),
    @Result(property = "id", column = "requisitionGroupId"),
    @Result(property = "name", column = "requisitionGroupName"),
    @Result(property = "code", column = "requisitionGroupCode"),
    @Result(property = "memberCount", column = "memberCount")
  })
  List<RequisitionGroup> searchByNodeName(@Param(value = "searchParam") String searchParam, RowBounds rowBounds);

  @Select({"SELECT COUNT(*) FROM requisition_groups RG WHERE LOWER(RG.name) LIKE '%'|| LOWER(#{searchParam}) ||'%'"})
  Integer getTotalRecordsForSearchOnGroupName(String searchParam);

  @Select({"SELECT COUNT(*) FROM requisition_groups RG INNER JOIN supervisory_nodes SN ON SN.id = RG.supervisoryNodeId",
    "WHERE LOWER(SN.name) LIKE '%'|| LOWER(#{searchParam}) ||'%'"})
  Integer getTotalRecordsForSearchOnNodeName(String searchParam);
}
