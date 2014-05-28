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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.core.domain.FacilityType;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * FacilityMapper maps the Facility entity to corresponding representation in database. Apart from basic CRUD operations
 * provides methods like getting all facilities in a requisition group/delivery zone, searching facility by name, code
 * or type (virtual, non-virtual), getting child facilities for a facility etc.
 */

@Repository
public interface FacilityMapper {

  @Insert("INSERT INTO facilities(code, name, description, gln, mainPhone, fax, address1, address2, " +
    "geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById," +
    "coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, online," +
    "satellite, parentFacilityId, hasElectricity, hasElectronicSCC, hasElectronicDAR, active," +
    "goLiveDate, goDownDate, comment, virtualFacility, enabled, createdDate,createdBy, modifiedBy, modifiedDate) " +
    "VALUES(#{code}, #{name}, #{description}, #{gln}, #{mainPhone}, #{fax}, #{address1}, #{address2}," +
    "#{geographicZone.id}," +
    "#{facilityType.id}," +
    "#{catchmentPopulation}, #{latitude}, #{longitude}, #{altitude}," +
    "#{operatedBy.id}," +
    "#{coldStorageGrossCapacity}, #{coldStorageNetCapacity}, #{suppliesOthers}, #{sdp},#{online}," +
    "#{satellite}, #{parentFacilityId}, #{hasElectricity}, #{hasElectronicSCC}, #{hasElectronicDAR}, #{active}," +
    "#{goLiveDate}, #{goDownDate}, #{comment}, #{virtualFacility}, #{enabled},COALESCE(#{createdDate}, NOW()), #{createdBy}, #{modifiedBy}, " +
    "COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
  @Options(useGeneratedKeys = true)
  Integer insert(Facility facility);

  @Select("SELECT id, code, name FROM facilities")
  List<Facility> getAll();

  @Select("SELECT * FROM users U, facilities F " +
    "WHERE U.facilityId = F.id AND U.id = #{userId} AND f.active = TRUE AND f.virtualFacility = FALSE")
  @Results(value = {@Result(property = "id", column = "facilityId")})
  Facility getHomeFacility(Long userId);

  @Select("SELECT * FROM facility_types ORDER BY displayOrder NULLS LAST, LOWER(name)")
  List<FacilityType> getAllTypes();

  @Select("SELECT * FROM facility_types WHERE id = #{id}")
  public FacilityType getFacilityTypeById(Long id);

  @Select("SELECT * FROM facility_operators ORDER BY displayOrder")
  List<FacilityOperator> getAllOperators();

  @Select("SELECT * FROM facility_operators WHERE id = #{id}")
  public FacilityOperator getFacilityOperatorById(Long id);

  @Select("SELECT code FROM facility_operators WHERE id = #{id}")
  @SuppressWarnings("unused")
  public String getFacilityOperatorCodeFor(Long id);

  @Select("SELECT id FROM facility_operators WHERE LOWER(code) = LOWER(#{code})")
  Long getOperatedByIdForCode(String code);

  @Select("SELECT * FROM facilities WHERE id = #{id}")
  @Results(value = {
    @Result(property = "geographicZone", column = "geographicZoneId", javaType = Long.class,
      one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getWithParentById")),
    @Result(property = "facilityType", column = "typeId", javaType = Long.class,
      one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Long.class,
      one = @One(select = "getFacilityOperatorById"))
  })
  Facility getById(Long id);

  @Select("SELECT * FROM facilities WHERE LOWER(code)=LOWER(#{code})")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "geographicZone", column = "geographicZoneId", javaType = Long.class,
      one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getWithParentById")),
    @Result(property = "facilityType", column = "typeId", javaType = Long.class,
      one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Long.class,
      one = @One(select = "getFacilityOperatorById")),
    @Result(property = "supportedPrograms", column = "id", javaType = List.class,
      many = @Many(select = "org.openlmis.core.repository.mapper.ProgramSupportedMapper.getAllByFacilityId"))
  })
  Facility getByCode(String code);

  @Update("UPDATE facilities SET code = #{code}, name = #{name}, description = #{description}, gln = #{gln}," +
    "mainPhone = #{mainPhone}, fax = #{fax}, address1 = #{address1}," +
    "address2 = #{address2}, geographicZoneId = #{geographicZone.id}," +
    "typeId = #{facilityType.id}, catchmentPopulation = #{catchmentPopulation}, latitude = #{latitude}," +
    "longitude = #{longitude}, altitude = #{altitude}," +
    "operatedById = #{operatedBy.id}," +
    "coldStorageGrossCapacity = #{coldStorageGrossCapacity}, coldStorageNetCapacity = #{coldStorageNetCapacity}," +
    "suppliesOthers = #{suppliesOthers}, sdp = #{sdp}, online = #{online}, satellite = #{satellite}, parentFacilityId = #{parentFacilityId}," +
    "hasElectricity = #{hasElectricity}, hasElectronicSCC = #{hasElectronicSCC}, " +
    "hasElectronicDAR = #{hasElectronicDAR}, active = #{active}, virtualFacility = #{virtualFacility}, " +
    "goLiveDate = #{goLiveDate}, goDownDate = #{goDownDate}," +
    "comment = #{comment}, enabled = #{enabled}, modifiedBy = #{modifiedBy}, modifiedDate = (COALESCE(#{modifiedDate}, NOW())) WHERE id=#{id}")
  void update(Facility facility);

  @Select("SELECT * FROM facility_types WHERE LOWER(code) = LOWER(#{code})")
  FacilityType getFacilityTypeForCode(String facilityTypeCode);

  @Update({"UPDATE facilities SET enabled = #{enabled}, active=#{active}, " +
    "modifiedBy=#{modifiedBy}, modifiedDate = NOW() WHERE id =#{id}"})
  void updateEnabledAndActiveFor(Facility facility);

  @Select("SELECT id FROM facilities WHERE LOWER(code) = LOWER(#{code})")
  Long getIdForCode(String code);

  @Select("SELECT DISTINCT f.* FROM facilities f " +
    "INNER JOIN programs_supported ps ON f.id=ps.facilityId " +
    "INNER JOIN requisition_group_members rgm ON f.id= rgm.facilityId " +
    "INNER JOIN requisition_group_program_schedules rgps ON (rgps.programId = ps.programId AND rgps.requisitionGroupId=rgm.requisitionGroupId)" +
    "WHERE ps.programId = #{programId} " +
    "AND rgm.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[]) " +
    "AND rgps.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[]) " +
    "AND f.active = TRUE " +
    "AND ps.active = TRUE " +
    "AND f.virtualFacility = FALSE ")
  @Results(value = {
    @Result(property = "geographicZone.id", column = "geographicZoneId"),
    @Result(property = "facilityType", column = "typeId", javaType = Long.class,
      one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Long.class,
      one = @One(select = "getFacilityOperatorById"))
  })
  List<Facility> getFacilitiesBy(@Param(value = "programId") Long programId,
                                 @Param(value = "requisitionGroupIds") String requisitionGroupIds);

  @Select({"SELECT id, code, name FROM facilities WHERE virtualFacility = #{virtualFacility} AND",
    "(LOWER(code) LIKE '%' || LOWER(#{searchParam}) || '%'",
    "OR LOWER(name) LIKE '%' || LOWER(#{searchParam}) || '%')",
    "ORDER BY code"})
  List<Facility> searchFacilitiesByCodeOrNameAndVirtualFacilityFlag(@Param("searchParam") String searchParam,
                                                                    @Param("virtualFacility") Boolean includeVirtualFacility);

  @Select({"SELECT id, code, name FROM facilities WHERE",
    "(LOWER(code) LIKE '%' || LOWER(#{searchParam}) || '%'",
    "OR LOWER(name) LIKE '%' || LOWER(#{searchParam}) || '%')",
    "ORDER BY code"})
  List<Facility> searchFacilitiesByCodeOrName(String searchParam);

  @Select({"SELECT DISTINCT F.* FROM facilities F INNER JOIN users U ON U.facilityId = F.id",
    "INNER JOIN role_assignments RA ON RA.userId = U.id INNER JOIN role_rights RR ON RR.roleId = RA.roleId",
    "WHERE U.id = #{userId} AND RR.rightName = ANY(#{commaSeparatedRights}::VARCHAR[]) AND RA.supervisoryNodeId IS NULL"})
  @Results(value = {
    @Result(property = "geographicZone.id", column = "geographicZoneId"),
    @Result(property = "facilityType", column = "typeId", javaType = Long.class, one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Long.class, one = @One(select = "getFacilityOperatorById"))
  })
  Facility getHomeFacilityWithRights(@Param("userId") Long userId,
                                     @Param("commaSeparatedRights") String commaSeparatedRights);

  @Select({"SELECT DISTINCT f.* FROM facilities f",
    "INNER JOIN requisition_group_members rgm ON f.id= rgm.facilityId",
    "WHERE rgm.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[])"})
  @Results(value = {
    @Result(property = "geographicZone.id", column = "geographicZoneId"),
    @Result(property = "facilityType", column = "typeId", javaType = Long.class,
      one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Long.class,
      one = @One(select = "getFacilityOperatorById"))
  })
  List<Facility> getAllInRequisitionGroups(@Param("requisitionGroupIds") String requisitionGroupIds);

  @Select(
    {"SELECT DISTINCT F.geographicZoneId, F.name, F.code, F.id, F.catchmentPopulation FROM facilities F INNER JOIN delivery_zone_members DZM ON F.id = DZM.facilityId",
      "INNER JOIN programs_supported PS ON PS.facilityId = F.id",
      "INNER JOIN delivery_zones DZ ON DZ.id = DZM.deliveryZoneId",
      "INNER JOIN delivery_zone_program_schedules DZPS ON DZPS.deliveryZoneId = DZM.deliveryZoneId",
      "WHERE DZPS.programId = #{programId} AND F.active = true",
      "AND PS.programId = #{programId}  AND DZM.deliveryZoneId = #{deliveryZoneId} order by F.name"})
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "geographicZone", column = "geographicZoneId", javaType = Long.class,
      one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getWithParentById"))
  })
  List<Facility> getAllInDeliveryZoneFor(@Param("deliveryZoneId") Long deliveryZoneId,
                                         @Param("programId") Long programId);

  @Select({"SELECT F.id AS id, F.code AS code",
    "FROM facilities F INNER JOIN programs_supported PS ON F.id = PS.facilityId",
    "WHERE PS.modifiedDate = #{modifiedDate}"})
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "supportedPrograms", column = "id", javaType = List.class,
      many = @Many(select = "org.openlmis.core.repository.mapper.ProgramSupportedMapper.getAllByFacilityId"))})
  List<Facility> getAllByProgramSupportedModifiedDate(Date modifiedDate);

  @Select({"SELECT * FROM facilities WHERE id IN (SELECT supplyingFacilityId FROM supply_lines) AND enabled = TRUE"})
  List<Facility> getEnabledWarehouses();

  @Select({"SELECT * FROM facilities WHERE parentFacilityId = #{id}"})
  @Results(value = {
    @Result(property = "geographicZone.id", column = "geographicZoneId"),
    @Result(property = "facilityType", column = "typeId", javaType = Long.class,
      one = @One(select = "getFacilityTypeById"))
  })
  List<Facility> getChildFacilities(Facility facility);

  @Update({"UPDATE facilities SET typeId = Parent.typeId, geographicZoneId = Parent.geographicZoneId",
    "FROM (SELECT typeId, geographicZoneId FROM facilities WHERE id = #{id}) AS Parent",
    "WHERE parentFacilityId = #{id}"})
  void updateVirtualFacilities(Facility parentFacility);

  @Select({"SELECT F.id AS id, F.code AS code FROM facilities F INNER JOIN requisition_group_members RGM ON",
    "F.id = RGM.facilityId WHERE RGM.modifiedDate = #{modifiedDate}"})
  List<Facility> getAllByRequisitionGroupMemberModifiedDate(Date modifiedDate);

  @Select({"SELECT id, code FROM facilities WHERE modifiedDate = #{modifiedDate} AND",
    "id IN(SELECT DISTINCT(parentFacilityId) FROM facilities)"})
  List<Facility> getAllParentsByModifiedDate(Date modifiedDate);

  @SelectProvider(type = SelectFacilities.class, method = "getEnabledFacilities")
  @Results(value = {
    @Result(property = "facilityType.id", column = "facilityTypeId"),
    @Result(property = "facilityType.name", column = "facilityTypeName"),
  })
  List<Facility> getEnabledFacilities(@Param(value = "searchParam") String searchParam,
                                      @Param(value = "facilityTypeId") Long facilityTypeId,
                                      @Param(value = "geoZoneId") Long geoZoneId);

  @SelectProvider(type = SelectFacilities.class, method = "getEnabledFacilitiesCount")
  Integer getEnabledFacilitiesCount(@Param(value = "searchParam") String searchParam,
                                    @Param(value = "facilityTypeId") Long facilityTypeId,
                                    @Param(value = "geoZoneId") Long geoZoneId);

  public class SelectFacilities {
    @SuppressWarnings(value = "unused")
    public static String getEnabledFacilitiesCount(Map<String, Object> params) {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT COUNT(*) FROM facilities F WHERE ");
      return createQuery(sql, params).toString();
    }

    @SuppressWarnings(value = "unused")
    public static String getEnabledFacilities(Map<String, Object> params) {
      StringBuilder sql = new StringBuilder();
      sql.append(
        "SELECT F.*, FT.id AS facilityTypeId, FT.name AS facilityTypeName FROM facilities F INNER JOIN facility_types FT ON F.typeId = FT.id WHERE ");
      sql = createQuery(sql, params);
      sql.append(" ORDER BY LOWER(F.code)");
      return sql.toString();
    }

    private static StringBuilder createQuery(StringBuilder sql, Map<String, Object> params) {
      String facilityCodeName = (String) params.get("searchParam");
      Long facilityTypeId = (Long) params.get("facilityTypeId");
      Long geographicZoneId = (Long) params.get("geoZoneId");

      if (facilityTypeId != null) {
        sql.append("F.typeId = " + facilityTypeId + " AND ");
      }
      if (geographicZoneId != null) {
        sql.append("F.geographicZoneId =" + geographicZoneId + " AND ");
      }
      sql.append(
        "(LOWER(F.code) LIKE LOWER('%" + facilityCodeName + "%') OR LOWER(F.name) LIKE LOWER('%" + facilityCodeName + "%'))");
      sql.append(" AND F.enabled = true");

      return sql;
    }
  }
}