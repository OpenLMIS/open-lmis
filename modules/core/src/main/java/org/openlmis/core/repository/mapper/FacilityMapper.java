/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.core.domain.FacilityType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityMapper {

  @Insert("Insert into facilities(code, name, description, gln, mainPhone, fax, address1, address2, " +
    "geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById," +
    "coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, online," +
    "satellite, satelliteParentId, hasElectricity, hasElectronicScc, hasElectronicDar, active," +
    "goLiveDate, goDownDate, comment, dataReportable, createdDate,createdBy, modifiedBy, modifiedDate) " +
    "values(#{code}, #{name}, #{description}, #{gln}, #{mainPhone}, #{fax}, #{address1}, #{address2}," +
    "#{geographicZone.id}," +
    "#{facilityType.id}," +
    "#{catchmentPopulation}, #{latitude}, #{longitude}, #{altitude}," +
    "#{operatedBy.id}," +
    "#{coldStorageGrossCapacity}, #{coldStorageNetCapacity}, #{suppliesOthers}, #{sdp},#{online}," +
    "#{satellite}, #{satelliteParentId}, #{hasElectricity}, #{hasElectronicScc}, #{hasElectronicDar}, #{active}," +
    "#{goLiveDate}, #{goDownDate}, #{comment}, #{dataReportable},COALESCE(#{createdDate}, NOW()), #{createdBy}, #{modifiedBy}, " +
    "COALESCE(#{modifiedDate}, NOW()))")
  @Options(useGeneratedKeys = true)
  Integer insert(Facility facility);

  @Select("SELECT id, code, name FROM facilities")
  List<Facility> getAll();

  @Select("SELECT * FROM users U, facilities F " +
    "WHERE U.facilityId = F.id AND U.id = #{userId} AND f.active = true")
  @Results(value = {@Result(property = "id", column = "facilityId")})
  Facility getHomeFacility(Long userId);

  @Select("SELECT * FROM facility_types ORDER BY displayOrder")
  List<FacilityType> getAllTypes();

  @Select("SELECT * FROM facility_types where id = #{id}")
  public FacilityType getFacilityTypeById(Long id);

  @Select("SELECT * FROM facility_operators ORDER BY displayOrder")
  List<FacilityOperator> getAllOperators();

  @Select("SELECT * FROM facility_operators WHERE id = #{id}")
  public FacilityOperator getFacilityOperatorById(Long id);

  @Select("SELECT code FROM facility_operators where id = #{id}")
  @SuppressWarnings("unused")
  public String getFacilityOperatorCodeFor(Long id);

  @Select("SELECT id FROM facility_operators where LOWER(code) = LOWER(#{code})")
  Long getOperatedByIdForCode(String code);


  @Select("SELECT * FROM facilities WHERE id = #{id}")
  @Results(value = {
    @Result(property = "geographicZone", column = "geographicZoneId", javaType = Long.class,
      one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getGeographicZoneById")),
    @Result(property = "facilityType", column = "typeId", javaType = Long.class, one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Long.class, one = @One(select = "getFacilityOperatorById"))
  })
  Facility getById(Long id);

  @Update("UPDATE facilities SET code = #{code}, name = #{name}, description = #{description}, gln = #{gln}," +
    "mainPhone = #{mainPhone}, fax = #{fax}, address1 = #{address1}," +
    "address2 = #{address2}, geographicZoneId = #{geographicZone.id}," +
    "typeId = #{facilityType.id}, catchmentPopulation = #{catchmentPopulation}, latitude = #{latitude}," +
    "longitude = #{longitude}, altitude = #{altitude}," +
    "operatedById = #{operatedBy.id}," +
    "coldStorageGrossCapacity = #{coldStorageGrossCapacity}, coldStorageNetCapacity = #{coldStorageNetCapacity}," +
    "suppliesOthers = #{suppliesOthers}, sdp = #{sdp}, online = #{online}, satellite = #{satellite}, satelliteParentId = #{satelliteParentId}," +
    "hasElectricity = #{hasElectricity}, hasElectronicScc = #{hasElectronicScc}, hasElectronicDar = #{hasElectronicDar}, active = #{active}," +
    "goLiveDate = #{goLiveDate}, goDownDate = #{goDownDate}," +
    "comment = #{comment}, dataReportable = #{dataReportable}, modifiedBy = #{modifiedBy}, modifiedDate = (COALESCE(#{modifiedDate}, NOW())) WHERE id=#{id}")
  void update(Facility facility);

  @Select("SELECT * FROM facility_types where LOWER(code) = LOWER(#{code})")
  FacilityType getFacilityTypeForCode(String facilityTypeCode);

  @Update({"UPDATE facilities SET dataReportable = #{dataReportable}, active=#{active}, " +
    "modifiedBy=#{modifiedBy}, modifiedDate = NOW() WHERE id =#{id}"})
  void updateDataReportableAndActiveFor(Facility facility);

  @Select("SELECT id FROM facilities WHERE LOWER(code) = LOWER(#{code})")
  Long getIdForCode(String code);

  @Select("SELECT DISTINCT f.* FROM facilities f " +
    "INNER JOIN programs_supported ps ON f.id=ps.facilityId " +
    "INNER JOIN requisition_group_members rgm ON f.id= rgm.facilityId " +
    "WHERE ps.programId = #{programId} " +
    "AND rgm.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[]) " +
    "AND f.active = true " +
    "AND ps.active = true ")
  @Results(value = {
    @Result(property = "geographicZone.id", column = "geographicZoneId"),
    @Result(property = "facilityType", column = "typeId", javaType = Long.class, one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Long.class, one = @One(select = "getFacilityOperatorById"))
  })
  List<Facility> getFacilitiesBy(@Param(value = "programId") Long programId, @Param(value = "requisitionGroupIds") String requisitionGroupIds);

  @Select("SELECT id, code, name FROM facilities WHERE " +
    "LOWER(code) LIKE '%' || LOWER(#{searchParam}) || '%' " +
    "OR LOWER(name) LIKE '%' || LOWER(#{searchParam}) || '%'")
  List<Facility> searchFacilitiesByCodeOrName(String searchParam);


  @Select({"SELECT DISTINCT F.* FROM facilities F INNER JOIN users U ON U.facilityId = F.id",
    "INNER JOIN role_assignments RA ON RA.userId = U.id INNER JOIN role_rights RR ON RR.roleId = RA.roleId",
    "WHERE U.id = #{userId} AND RR.rightName = ANY(#{commaSeparatedRights}::VARCHAR[]) AND RA.supervisoryNodeId IS NULL"})
  @Results(value = {
    @Result(property = "geographicZone.id", column = "geographicZoneId"),
    @Result(property = "facilityType", column = "typeId", javaType = Long.class, one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Long.class, one = @One(select = "getFacilityOperatorById"))
  })
  Facility getHomeFacilityWithRights(@Param("userId") Long userId, @Param("commaSeparatedRights") String commaSeparatedRights);

  @Select({"SELECT DISTINCT f.* FROM facilities f",
    "INNER JOIN requisition_group_members rgm ON f.id= rgm.facilityId",
    "WHERE rgm.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[])",
    "AND f.active = true"})
  @Results(value = {
    @Result(property = "geographicZone.id", column = "geographicZoneId"),
    @Result(property = "facilityType", column = "typeId", javaType = Long.class, one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Long.class, one = @One(select = "getFacilityOperatorById"))
  })
  List<Facility> getAllInRequisitionGroups(@Param("requisitionGroupIds") String requisitionGroupIds);

  @Select("SELECT * from facilities WHERE code=#{code}")
  Facility getByCode(String code);

  @Select({"SELECT F.geographicZoneId, F.name, F.code, F.id, F.catchmentPopulation FROM facilities F INNER JOIN delivery_zone_members DZM ON F.id = DZM.facilityId",
    "INNER JOIN programs_supported PS ON PS.facilityId = F.id",
    "INNER JOIN delivery_zones DZ ON DZ.id = DZM.deliveryZoneId",
    "INNER JOIN delivery_zone_program_schedules DZPS ON DZPS.deliveryZoneId = DZM.deliveryZoneId",
    "WHERE DZPS.programId = #{programId} AND F.active = true",
    "AND PS.programId = #{programId}  AND dzm.deliveryZoneId = #{deliveryZoneId} order by F.name"})
  @Results(value = {
    @Result(property = "geographicZone", column = "geographicZoneId", javaType = Long.class,
      one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getGeographicZoneById"))
  })
  List<Facility> getAllInDeliveryZoneFor(@Param("deliveryZoneId") Long deliveryZoneId, @Param("programId") Long programId);
}
