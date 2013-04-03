/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
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

    @Select("SELECT id FROM requisition_groups where supervisoryNodeId = ANY(#{supervisoryNodeIdsAsString}::INTEGER[])")
    List<RequisitionGroup> getRequisitionGroupBySupervisoryNodes(String supervisoryNodeIdsAsString);

  @Select("SELECT * " +
      "FROM requisition_groups rg " +
      "INNER JOIN requisition_group_program_schedules rgps ON rg.id = rgps.requisitionGroupId " +
      "INNER JOIN requisition_group_members rgm ON rgps.requisitionGroupId = rgm.requisitionGroupId " +
      "WHERE rgps.programId = #{program.id} " +
      "AND RGM.facilityId = #{facility.id}")
  RequisitionGroup getRequisitionGroupForProgramAndFacility(@Param(value = "program") Program program, @Param(value = "facility") Facility facility);

  @Select("SELECT * FROM requisition_groups where LOWER(code) = LOWER(#{code})")
  @Results(value = {
    @Result(property = "supervisoryNode.id", column = "supervisoryNodeId")
  })
  RequisitionGroup getByCode(String code);

  @Update("UPDATE requisition_groups " +
    "SET name = #{name}, description =  #{description}, supervisoryNodeId = #{supervisoryNode.id}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} " +
    "WHERE id = #{id}")
  void update(RequisitionGroup requisitionGroup);
}
