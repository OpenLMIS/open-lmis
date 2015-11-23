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
import org.openlmis.core.domain.SupervisoryNode;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SupervisoryNodeMapper maps the SupervisoryNode entity to corresponding representation in database. Also provides
 * methods like getting supervisory node hierarchy.
 */

@Repository
public interface SupervisoryNodeMapper {

  @Select("SELECT * FROM supervisory_nodes where id = #{id}")
  @Results(value = {
    @Result(property = "parent", column = "parentId", javaType = SupervisoryNode.class,
      one = @One(select = "getById")),
    @Result(property = "facility", column = "facilityId", javaType = Facility.class,
      one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  SupervisoryNode getSupervisoryNode(Long id);

  @Select("SELECT * FROM supervisory_nodes where id = #{id}")
  SupervisoryNode getById(Long id);

  @Insert("INSERT INTO supervisory_nodes " +
    "(code, name, parentId, facilityId, description, createdBy, modifiedBy, modifiedDate)" +
    " VALUES (#{code}, #{name}, #{parent.id}, #{facility.id}, #{description}, #{createdBy}, #{modifiedBy}, COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
  @Options(useGeneratedKeys = true)
  Integer insert(SupervisoryNode supervisoryNode);

  @Select("SELECT id FROM supervisory_nodes WHERE LOWER(code) = LOWER(#{code})")
  Long getIdForCode(String code);

  @Select("WITH  recursive  supervisoryNodesRec AS " +
    "   (" +
    "   SELECT *" +
    "   FROM supervisory_nodes " +
    "   WHERE id in  (SELECT DISTINCT s.id FROM  " +
    "       supervisory_nodes s " +
    "       INNER JOIN role_assignments ra ON s.id = ra.supervisoryNodeId  " +
    "       INNER JOIN role_rights rr ON ra.roleId = rr.roleId  " +
    "       WHERE rr.rightName = ANY (#{commaSeparatedRights}::VARCHAR[])  " +
    "       AND ra.userId = #{userId}  " +
    "       AND ra.programId = #{programId}) " +
    "   UNION " +
    "   SELECT sn.* " +
    "   FROM supervisory_nodes sn " +
    "   JOIN supervisoryNodesRec " +
    "   ON sn.parentId = supervisoryNodesRec.id " +
    "   )" +
    "SELECT * FROM supervisoryNodesRec")
  List<SupervisoryNode> getAllSupervisoryNodesInHierarchyBy(@Param(value = "userId") Long userId, @Param(value = "programId") Long programId,
                                                            @Param(value = "commaSeparatedRights") String commaSeparatedRights);

  @Select({"SELECT SN.* FROM supervisory_nodes SN INNER JOIN requisition_groups RG ON RG.supervisoryNodeId = SN.id",
    "WHERE RG.code = #{rgCode}"})
  @Results(value = {
    @Result(property = "parent.id", column = "parentId"),
    @Result(property = "facility.id", column = "facilityId")
  })
  SupervisoryNode getFor(String rgCode);

  @Select("SELECT * FROM supervisory_nodes WHERE id = (SELECT parentId FROM supervisory_nodes WHERE id = #{id})")
  @Results(value = {
    @Result(property = "parent.id", column = "parentId"),
    @Result(property = "facility.id", column = "facilityId")
  })
  SupervisoryNode getParent(Long id);

  @Select("SELECT * FROM supervisory_nodes order by name")
  List<SupervisoryNode> getAll();

  @Select({"WITH  recursive  supervisoryNodesRec AS ",
    "   (",
    "   SELECT *",
    "   FROM supervisory_nodes ",
    "   WHERE id in  (SELECT DISTINCT s.id FROM  ",
    "       supervisory_nodes s ",
    "       INNER JOIN role_assignments ra ON s.id = ra.supervisoryNodeId  ",
    "       INNER JOIN role_rights rr ON ra.roleId = rr.roleId  ",
    "       WHERE rr.rightName = ANY (#{commaSeparatedRights}::VARCHAR[])  ",
    "       AND ra.userId = #{userId} ) ",
    "   UNION ",
    "   SELECT sn.* ",
    "   FROM supervisory_nodes sn ",
    "   JOIN supervisoryNodesRec ",
    "   ON sn.parentId = supervisoryNodesRec.id ",
    "   )",
    "SELECT * FROM supervisoryNodesRec"})
  List<SupervisoryNode> getAllSupervisoryNodesInHierarchyByUserAndRights(@Param("userId") Long userId,
                                                                         @Param("commaSeparatedRights") String commaSeparatedRights);

  /*
    Returns the specified SupervisoryNode along with all of its ancestor nodes.
   */
  @Select({"WITH  recursive  supervisoryNodesRec AS ",
    "   (",
    "   SELECT *",
    "   FROM supervisory_nodes ",
    "   WHERE id = #{id}",
    "   UNION ",
    "   SELECT sn.* ",
    "   FROM supervisory_nodes sn ",
    "   JOIN supervisoryNodesRec ",
    "   ON sn.id = supervisoryNodesRec.parentId ",
    "   )",
    "SELECT * FROM supervisoryNodesRec"})
  @Results(value = {
          @Result(property = "facility", column = "facilityId", javaType = Facility.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  List<SupervisoryNode> getAllParentSupervisoryNodesInHierarchy(SupervisoryNode supervisoryNode);

  /*
    Returns the specified SupervisoryNode along with all of its descendant nodes.
   */
  @Select({"WITH  recursive  supervisoryNodesRec AS ",
          "   (",
          "   SELECT *",
          "   FROM supervisory_nodes ",
          "   WHERE id = #{id}",
          "   UNION ",
          "   SELECT sn.* ",
          "   FROM supervisory_nodes sn ",
          "   JOIN supervisoryNodesRec ",
          "   ON sn.parentId = supervisoryNodesRec.id ",
          "   )",
          "SELECT * FROM supervisoryNodesRec"})
  @Results(value = {
          @Result(property = "facility", column = "facilityId", javaType = Facility.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  List<SupervisoryNode> getAllChildSupervisoryNodesInHierarchy(SupervisoryNode supervisoryNode);

  @Select("SELECT * FROM supervisory_nodes WHERE LOWER(code) = LOWER(#{code})")
  SupervisoryNode getByCode(SupervisoryNode supervisoryNode);

  @Update("UPDATE supervisory_nodes " +
    "SET code = #{code}, name = #{name}, parentId = #{parent.id}, facilityId = #{facility.id}, " +
    "description = #{description}, modifiedBy = #{modifiedBy}, modifiedDate = COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP) " +
    "WHERE id = #{id}")
  void update(SupervisoryNode supervisoryNode);

  @Select({"SELECT * FROM supervisory_nodes SN INNER JOIN supervisory_nodes SNP ON SN.parentId = SNP.id WHERE LOWER(SNP.name)" +
    " LIKE '%'|| LOWER(#{nameSearchCriteria}) ||'%' order by LOWER(SNP.name), LOWER(SN.name) NULLS LAST"})
  @Results(value = {
    @Result(property = "parent", column = "parentId", javaType = SupervisoryNode.class,
      one = @One(select = "getById")),
    @Result(property = "facility", column = "facilityId", javaType = Facility.class,
      one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  List<SupervisoryNode> getSupervisoryNodesByParent(@Param(value = "nameSearchCriteria") String nameSearchCriteria, RowBounds rowBounds);


  @Select({"SELECT * FROM supervisory_nodes SN LEFT OUTER JOIN supervisory_nodes SNP ON SN.parentId = SNP.id WHERE LOWER(SN.name)" +
    " LIKE '%'|| LOWER(#{nameSearchCriteria}) ||'%' ORDER BY LOWER(SNP.name), LOWER(SN.name) NULLS LAST"})
  @Results(value = {
    @Result(property = "parent", column = "parentId", javaType = SupervisoryNode.class,
      one = @One(select = "getById")),
    @Result(property = "facility", column = "facilityId", javaType = Facility.class,
      one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  List<SupervisoryNode> getSupervisoryNodesBy(@Param(value = "nameSearchCriteria") String nameSearchCriteria, RowBounds rowBounds);

  @Select({"SELECT COUNT(*) FROM supervisory_nodes SN LEFT OUTER JOIN supervisory_nodes SNP ON SN.parentId = SNP.id WHERE LOWER(SN.name)" +
    " LIKE '%'|| LOWER(#{nameSearchCriteria}) ||'%'"})
  Integer getTotalSearchResultCount(String param);

  @Select({"SELECT COUNT(*) FROM supervisory_nodes SN INNER JOIN supervisory_nodes SNP ON SN.parentId = SNP.id WHERE LOWER(SNP.name)" +
    " LIKE '%'|| LOWER(#{nameSearchCriteria}) ||'%'"})
  Integer getTotalParentSearchResultCount(String param);

  @Select("SELECT * FROM supervisory_nodes WHERE LOWER(name) LIKE '%' || LOWER(#{param}) || '%' ORDER BY LOWER(name)")
  List<SupervisoryNode> getFilteredSupervisoryNodesByName(String param);

  @Select({"SELECT * FROM supervisory_nodes WHERE parentId IS NULL AND LOWER(name) LIKE '%' || LOWER(#{param}) || '%' ORDER BY LOWER(name)"})
  List<SupervisoryNode> searchTopLevelSupervisoryNodesByName(String param);

  @Select("select count(id) AS total_unassigned from supervisory_nodes where id not in \n" +
    " (select id from vw_user_supervisorynodes  where userid = #{userId} and programid = #{programId} )")
  Long getTotalUnassignedSupervisoryNodeOfUserBy(
    @Param(value="userId") Long userId,
    @Param(value="programId") Long programId
  );
}
