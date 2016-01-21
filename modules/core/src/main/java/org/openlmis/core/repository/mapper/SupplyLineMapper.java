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
import java.util.Map;


/**
 * SupplyLineMapper maps the SupplyLine entity to corresponding representation in database.
 */
@Repository
public interface SupplyLineMapper {

  @Insert({"INSERT INTO supply_lines " +
    "(description, supervisoryNodeId, programId, supplyingFacilityId, exportOrders, parentId, createdBy, modifiedBy, modifiedDate)",
    "VALUES (#{description}, #{supervisoryNode.id}, #{program.id}, #{supplyingFacility.id}, #{exportOrders}, #{parentId}, #{createdBy}, #{modifiedBy}, COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))"})
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
    "supplyingFacilityId = #{supplyingFacility.id}, exportOrders = #{exportOrders}, parentId = #{parentId}, modifiedBy = #{modifiedBy},",
    "modifiedDate = COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP) ",
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

  @Select({"SELECT SL.*, F.name AS facilityName, F.id AS facilityId, F.code as facilityCode,",
    "P.name AS programName, SN.name AS supervisoryNodeName",
    "FROM supply_lines SL INNER JOIN facilities F ON SL.supplyingFacilityId = F.id ",
    "INNER JOIN programs P ON SL.programId = P.id",
    "INNER JOIN supervisory_nodes SN ON SL.supervisoryNodeId = SN.id",
    "WHERE SL.id = #{id}"})
  @Results(value = {
    @Result(property = "supervisoryNode.id", column = "supervisoryNodeId"),
    @Result(property = "supervisoryNode.name", column = "supervisoryNodeName"),
    @Result(property = "program.id", column = "programId"),
    @Result(property = "program.name", column = "programName"),
    @Result(property = "supplyingFacility.id", column = "facilityId"),
    @Result(property = "supplyingFacility.name", column = "facilityName"),
    @Result(property = "supplyingFacility.code", column = "facilityCode")
  })
  SupplyLine getById(Long id);

  @SelectProvider(type = SupplyLineSearchProvider.class, method = "searchSupplyLines")
  @Results(value = {
    @Result(property = "supplyingFacility.name", column = "facilityName"),
    @Result(property = "supervisoryNode.name", column = "supervisoryNodeName"),
    @Result(property = "program.name", column = "programName")
  })
  List<SupplyLine> search(@Param(value = "searchParam") String searchParam,
                          @Param(value = "column") String column,
                          RowBounds rowBounds);

  @SelectProvider(type = SupplyLineSearchProvider.class, method = "getSearchedSupplyLinesCount")
  Integer getSearchedSupplyLinesCount(@Param(value = "searchParam") String searchParam,
                                      @Param(value = "column") String column);

  public class SupplyLineSearchProvider {
    @SuppressWarnings(value = "unused")
    public static String getSearchedSupplyLinesCount(Map<String, Object> params) {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT COUNT(*) ");
      return createQuery(sql, params).toString();
    }

    @SuppressWarnings(value = "unused")
    public static String searchSupplyLines(Map<String, Object> params) {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT SL.*, FAC.name AS facilityName, SN.name AS supervisoryNodeName, PGM.name AS programName ");
      sql = createQuery(sql, params);
      sql.append("ORDER BY LOWER(FAC.name), LOWER(SN.name), LOWER(PGM.name)");
      return sql.toString();
    }

    private static StringBuilder createQuery(StringBuilder sql, Map<String, Object> params) {
      String column = (String) params.get("column");
      sql.append(
        "FROM supply_lines SL INNER JOIN facilities FAC ON SL.supplyingFacilityId = FAC.id " +
          "INNER JOIN supervisory_nodes SN ON SL.supervisoryNodeId = SN.id " +
          "INNER JOIN programs PGM ON SL.programId = PGM.id ");

      if (column.equals("facility")) {
        sql.append("WHERE LOWER(FAC.name) LIKE ('%' || LOWER(#{searchParam}) || '%') ");
      }
      if (column.equals("supervisoryNode")) {
        sql.append("WHERE LOWER(SN.name) LIKE ('%' || LOWER(#{searchParam}) || '%') ");
      }
      if (column.equals("program")) {
        sql.append("WHERE LOWER(PGM.name) LIKE ('%' || LOWER(#{searchParam}) || '%') ");
      }
      return sql;
    }
  }

  @Select( "select distinct f.id, f.name from supply_lines sl join facilities f on f.id = sl.supplyingFacilityId where sl.supplyingFacilityId in (select facilityId from fulfillment_role_assignments where userId = #{userId} )")
  List<Facility> getSupplyingFacilities(@Param("userId") Long userId);

  @Select("SELECT * FROM supply_lines WHERE supplyingFacilityId = #{facilityId} AND programId = #{programId} limit 1")
  @Results(value = {
      @Result(property = "supervisoryNode.id", column = "supervisoryNodeId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "supplyingFacility.id", column = "supplyingFacilityId")
  })
  SupplyLine getByFacilityByProgram(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

}
