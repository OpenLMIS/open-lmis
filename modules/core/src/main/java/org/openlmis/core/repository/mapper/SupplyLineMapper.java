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
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * SupplyLineMapper maps the SupplyLine entity to corresponding representation in database.
 */
@Repository
public interface SupplyLineMapper {

  @Insert({"INSERT INTO supply_lines " +
    "(description, supervisoryNodeId, programId, supplyingFacilityId, exportOrders, createdBy, modifiedBy, modifiedDate)",
    "VALUES (#{description}, #{supervisoryNode.id}, #{program.id}, #{supplyingFacility.id}, #{exportOrders}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  Integer insert(SupplyLine supplyLine);

  @Select("SELECT * FROM supply_lines WHERE supervisoryNodeId = #{supervisoryNode.id} AND programId = #{program.id}")
  @Results(value = {
    @Result(property = "supplyingFacility", javaType = Facility.class, column = "supplyingFacilityId",
      one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  SupplyLine getSupplyLineBy(@Param(value = "supervisoryNode") SupervisoryNode supervisoryNode, @Param(value = "program") Program program);

  @Update({"UPDATE supply_lines ",
    "SET description = #{description}, supervisoryNodeId = #{supervisoryNode.id}, programId = #{program.id}, ",
    "supplyingFacilityId = #{supplyingFacility.id}, exportOrders =#{exportOrders},modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} ",
    "WHERE id = #{id}"})
  void update(SupplyLine supplyLine);

  @Select({"SELECT * FROM supply_lines WHERE supervisoryNodeId = #{supervisoryNode.id} AND programId = #{program.id} ",
    "AND supplyingFacilityId = #{supplyingFacility.id}"})
  @Results(value = {
    @Result(property = "supervisoryNode.id", column = "supervisoryNodeId"),
    @Result(property = "program.id", column = "programId"),
    @Result(property = "supplyingFacility.id", column = "supplyingFacilityId")
  })
  SupplyLine getSupplyLineBySupervisoryNodeProgramAndFacility(SupplyLine supplyLine);

  @Select({"SELECT SL.*, F.name AS facilityName, F.id AS facilityId, F.code as facilityCode",
    " FROM supply_lines SL INNER JOIN facilities F ON SL.supplyingFacilityId = F.id ",
    "WHERE SL.id = #{id}"})
  @Results(value = {
    @Result(property = "supervisoryNode.id", column = "supervisoryNodeId"),
    @Result(property = "program.id", column = "programId"),
    @Result(property = "supplyingFacility.id", column = "facilityId"),
    @Result(property = "supplyingFacility.name", column = "facilityName"),
    @Result(property = "supplyingFacility.code", column = "facilityCode")
  })
  SupplyLine getById(Long id);

  @Select({"SELECT SL.*, FAC.name AS facilityName, SN.name AS supervisoryNodeName, PGM.name AS programName " ,
    "FROM supply_lines SL INNER JOIN facilities FAC ON SL.supplyingFacilityId = FAC.id " ,
    "INNER JOIN supervisory_nodes SN ON SL.supervisoryNodeId = SN.id " ,
    "INNER JOIN programs PGM ON SL.programId = PGM.id ",
    "WHERE LOWER(FAC.name) LIKE '%' || LOWER(#{facilityName} || '%') " ,
    "ORDER BY LOWER(FAC.name), LOWER(SN.name), LOWER(PGM.name)"})
  @Results(value = {
    @Result(property = "supplyingFacility.name", column = "facilityName"),
    @Result(property = "supervisoryNode.name", column = "supervisoryNodeName"),
    @Result(property = "program.name", column = "programName")
  })
  List<SupplyLine> searchByFacilityName(@Param(value = "facilityName") String facilityName, RowBounds rowBounds);

  @Select({"SELECT SL.*, FAC.name AS facilityName, SN.name AS supervisoryNodeName, PGM.name AS programName " ,
    "FROM supply_lines SL INNER JOIN facilities FAC ON SL.supplyingFacilityId = FAC.id " ,
    "INNER JOIN supervisory_nodes SN ON SL.supervisoryNodeId = SN.id " ,
    "INNER JOIN programs PGM ON SL.programId = PGM.id " ,
    "WHERE LOWER(SN.name) LIKE '%' || LOWER(#{supervisoryNodeName} || '%') " ,
    "ORDER BY LOWER(FAC.name), LOWER(SN.name), LOWER(PGM.name)"})
  @Results(value = {
    @Result(property = "supplyingFacility.name", column = "facilityName"),
    @Result(property = "supervisoryNode.name", column = "supervisoryNodeName"),
    @Result(property = "program.name", column = "programName")
  })
  List<SupplyLine> searchBySupervisoryNodeName(@Param(value = "supervisoryNodeName") String supervisoryNodeName, RowBounds rowBounds);

  @Select({"SELECT SL.*, FAC.name AS facilityName, SN.name AS supervisoryNodeName, PGM.name AS programName " ,
    "FROM supply_lines SL INNER JOIN facilities FAC ON SL.supplyingFacilityId = FAC.id " ,
    "INNER JOIN supervisory_nodes SN ON SL.supervisoryNodeId = SN.id " ,
    "INNER JOIN programs PGM ON SL.programId = PGM.id " ,
    "WHERE LOWER(PGM.name) LIKE '%' || LOWER(#{programName} || '%') " ,
    "ORDER BY LOWER(FAC.name), LOWER(SN.name), LOWER(PGM.name)"})
  @Results(value = {
    @Result(property = "supplyingFacility.name", column = "facilityName"),
    @Result(property = "supervisoryNode.name", column = "supervisoryNodeName"),
    @Result(property = "program.name", column = "programName")
  })
  List<SupplyLine> searchByProgramName(String programName, RowBounds rowBounds);

  @Select({"SELECT COUNT(*) FROM supply_lines SL INNER JOIN facilities FAC ON SL.supplyingFacilityId = FAC.id ",
    "WHERE LOWER(FAC.name) LIKE '%' || LOWER(#{facilityName} || '%')"})
  Integer getTotalSearchResultsByFacilityName(String facilityName);

  @Select({"SELECT count(*) FROM supply_lines SL INNER JOIN supervisory_nodes SN ON SL.supervisoryNodeId = SN.id" ,
    "WHERE LOWER(SN.name) LIKE '%' || LOWER(#{supervisoryNodeName} || '%')"})
  Integer getTotalSearchResultsBySupervisoryNodeName(String supervisoryNodeName);

  @Select({"SELECT count(*) FROM supply_lines SL INNER JOIN programs P ON SL.programId = P.id" ,
    "WHERE LOWER(P.name) LIKE '%' || LOWER(#{programName} || '%')"})
  Integer getTotalSearchResultsByProgramName(String programName);
}
