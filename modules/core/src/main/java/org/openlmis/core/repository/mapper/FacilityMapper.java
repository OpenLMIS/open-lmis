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
import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.PriceSchedule;
import org.openlmis.core.dto.FacilityContact;
import org.openlmis.core.dto.FacilityGeoTreeDto;
import org.openlmis.core.dto.FacilityImages;
import org.openlmis.core.dto.FacilitySupervisor;
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
    "goLiveDate, goDownDate, comment, virtualFacility, enabled, priceScheduleId, createdDate,createdBy, modifiedBy, modifiedDate) " +
    "VALUES(#{code}, #{name}, #{description}, #{gln}, #{mainPhone}, #{fax}, #{address1}, #{address2}," +
    "#{geographicZone.id}," +
    "#{facilityType.id}," +
    "#{catchmentPopulation}, #{latitude}, #{longitude}, #{altitude}," +
    "#{operatedBy.id}," +
    "#{coldStorageGrossCapacity}, #{coldStorageNetCapacity}, #{suppliesOthers}, #{sdp},#{online}," +
    "#{satellite}, #{parentFacilityId}, #{hasElectricity}, #{hasElectronicSCC}, #{hasElectronicDAR}, #{active}," +
    "#{goLiveDate}, #{goDownDate}, #{comment}, #{virtualFacility}, #{enabled}, #{priceSchedule.id} , COALESCE(#{createdDate}, NOW()), #{createdBy}, #{modifiedBy}, " +
    "COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
  @Options(useGeneratedKeys = true)
  Integer insert(Facility facility);

  @Select("SELECT id, code, name FROM facilities")
  List<Facility> getAll();

  @Select("SELECT * FROM facilities")
  @Results(value = {@Result(property = "geographicZone", column = "geographicZoneId", javaType = Integer.class,
          one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getWithParentById"))})
  List<Facility> getAllReportFacilities();

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
      one = @One(select = "getFacilityOperatorById")),
    @Result(property = "priceSchedule", column = "priceScheduleId", javaType = PriceSchedule.class,
      one = @One(select = "org.openlmis.core.repository.mapper.PriceScheduleMapper.getById")),
  })
  Facility getById(Long id);

  @Select("SELECT * FROM facilities WHERE id = #{id}")
  Facility getLWById(Long id);

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
    "goLiveDate = #{goLiveDate}, goDownDate = #{goDownDate}, priceScheduleId = #{priceSchedule.id}, " +
    "comment = #{comment}, enabled = #{enabled}, modifiedBy = #{modifiedBy}, modifiedDate = (COALESCE(#{modifiedDate}, NOW())) WHERE id=#{id}")
  void update(Facility facility);

  @Select("SELECT * FROM facility_types WHERE LOWER(code) = LOWER(#{code})")
  FacilityType getFacilityTypeForCode(String facilityTypeCode);

  @Update({"UPDATE facilities SET enabled = #{enabled}, active=#{active}, " +
    "modifiedBy=#{modifiedBy}, modifiedDate = NOW() WHERE id =#{id}"})
  void updateEnabledAndActiveFor(Facility facility);

  @Select("SELECT id FROM facilities WHERE LOWER(code) = LOWER(#{code})")
  Long getIdForCode(String code);

  @Select("SELECT DISTINCT f.code, f.name, f.description, f.id FROM facilities f " +
    "INNER JOIN programs_supported ps ON f.id=ps.facilityId " +
    "INNER JOIN requisition_group_members rgm ON f.id= rgm.facilityId " +
    "INNER JOIN requisition_group_program_schedules rgps ON (rgps.programId = ps.programId AND rgps.requisitionGroupId=rgm.requisitionGroupId)" +
    "WHERE ps.programId = #{programId} " +
    "AND rgm.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[]) " +
    "AND rgps.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[]) " +
    "AND f.active = TRUE " +
    "AND ps.active = TRUE " +
    "AND f.virtualFacility = FALSE ")
  List<Facility> getFacilitiesBy(@Param(value = "programId") Long programId,
                                 @Param(value = "requisitionGroupIds") String requisitionGroupIds);

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

    @Select("SELECT facilities.*, facility_types.name  as facilityType "+
            "FROM facilities, facility_types "+
            "WHERE   facilities.typeid = facility_types.id and facilities.typeid = #{facilityTypeId}"+
            "ORDER BY facilities.code, facilities.name")
    @Results(value = {
            @Result(property = "geographicZone", column = "geographicZoneId", javaType = Integer.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getWithParentById")),
            @Result(property = "facilityType", column = "typeId", javaType = Integer.class, one = @One(select = "getFacilityTypeById")),
            @Result(property = "operatedBy", column = "operatedById", javaType = Integer.class, one = @One(select = "getFacilityOperatorById"))
    })
    List<Facility> getFacilitiesListForAFacilityType(Long facilityTypeId);

    @Select("SELECT facilities.*, facility_types.name  as facilityType "+
            "FROM facilities, facility_types "+
            "WHERE   facilities.typeId = facility_types.id and facilities.suppliesOthers = 't' "+
            "ORDER BY facilities.code, facilities.name")
    @Results(value = {
            @Result(property = "geographicZone", column = "geographicZoneId", javaType = Integer.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getGeographicZoneById_Ext")),
            @Result(property = "facilityType", column = "typeId", javaType = Integer.class, one = @One(select = "getFacilityTypeById")),
            @Result(property = "operatedBy", column = "operatedById", javaType = Integer.class, one = @One(select = "getFacilityOperatorById"))
    })
    List<Facility> getSupplyingFacilitiesCompleteList();


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

  @SelectProvider(type = SelectFacilities.class, method = "getFacilitiesCountBy")
  Integer getFacilitiesCountBy(@Param(value = "searchParam") String searchParam,
                               @Param(value = "facilityTypeId") Long facilityTypeId,
                               @Param(value = "geoZoneId") Long geoZoneId,
                               @Param(value = "virtualFacility") Boolean virtualFacility,
                               @Param(value = "enabled") Boolean enabled);

  @Select({"SELECT COUNT(*) FROM facilities",
    "WHERE (LOWER(code) LIKE '%' || LOWER(#{searchParam}) || '%')",
    "OR (LOWER(name) LIKE '%' || LOWER(#{searchParam}) || '%')"})
  Integer getTotalSearchResultCount(String searchParam);

  @Select({"SELECT COUNT(*) FROM facilities F",
    "INNER JOIN geographic_zones GZ on GZ.id = F.geographicZoneId",
    "WHERE (LOWER(GZ.name) LIKE '%' || LOWER(#{searchParam}) || '%')"})
  Integer getTotalSearchResultCountByGeographicZone(String searchParam);

  @SelectProvider(type = SelectFacilities.class, method = "getFacilitiesBySearchParam")
  @Results(value = {
    @Result(property = "geographicZone.name", column = "geoZoneName"),
    @Result(property = "facilityType.name", column = "facilityTypeName"),
  })
  List<Facility> search(@Param(value = "searchParam") String searchParam,
                          @Param(value = "column") String column,
                          RowBounds rowBounds);

  @SelectProvider(type = SelectFacilities.class, method = "searchFacilitiesBy")
  @Results(value = {
    @Result(property = "facilityType.id", column = "facilityTypeId"),
    @Result(property = "facilityType.name", column = "facilityTypeName"),
    @Result(property = "geographicZone.name", column = "geoZoneName"),
  })
  List<Facility> searchFacilitiesBy(@Param(value = "searchParam") String searchParam,
                                    @Param(value = "facilityTypeId") Long facilityTypeId,
                                    @Param(value = "geoZoneId") Long geoZoneId,
                                    @Param(value = "virtualFacility") Boolean virtualFacility,
                                    @Param(value = "enabled") Boolean enabled);



    public class SelectFacilities {
    @SuppressWarnings(value = "unused")
    public static String getFacilitiesCountBy(Map<String, Object> params) {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT COUNT(*) FROM facilities F WHERE ");
      return createQuery(sql, params).toString();
    }

    @SuppressWarnings(value = "unused")
    public static String getFacilitiesBySearchParam(Map<String, Object> params){
      StringBuilder sql = new StringBuilder();
      String column = (String) params.get("column");
      sql.append("SELECT F.id, F.code, F.name, GZ.name as geoZoneName, FT.name as facilityTypeName, F.active, F.enabled FROM facilities F ");
      sql.append("INNER JOIN geographic_zones GZ on GZ.id = F.geographicZoneId ");
      sql.append("INNER JOIN facility_types FT on FT.id = F.typeId WHERE ");

      if(column.equalsIgnoreCase("facility")){
        sql.append("(LOWER(F.code) LIKE '%' || LOWER(#{searchParam}) || '%') OR (LOWER(F.name) LIKE '%' || LOWER(#{searchParam}) || '%') ");
        sql.append("ORDER BY LOWER(F.name), LOWER(F.code)");
      }
      else if(column.equalsIgnoreCase("geographicZone")){
        sql.append("(LOWER(GZ.name) LIKE '%' || LOWER(#{searchParam}) || '%') ");
        sql.append("ORDER BY LOWER(GZ.name), LOWER(F.name), LOWER(F.code)");
      }
      return sql.toString();
    }

    @SuppressWarnings(value = "unused")
    public static String searchFacilitiesBy(Map<String, Object> params) {
      StringBuilder sql = new StringBuilder();
      sql.append(
        "SELECT F.*, GZ.name as geoZoneName, FT.id AS facilityTypeId, FT.name AS facilityTypeName FROM facilities F INNER JOIN facility_types FT ON F.typeId = FT.id " +
          "INNER JOIN geographic_zones GZ ON GZ.id = F.geographicZoneId WHERE ");
      sql = createQuery(sql, params);
      sql.append(" ORDER BY LOWER(F.code)");
      return sql.toString();
    }

    private static StringBuilder createQuery(StringBuilder sql, Map<String, Object> params) {
      String facilityCodeName = (String) params.get("searchParam");
      Long facilityTypeId = (Long) params.get("facilityTypeId");
      Long geographicZoneId = (Long) params.get("geoZoneId");
      Boolean virtualFacility = (Boolean) params.get("virtualFacility");
      Boolean enabled = (Boolean) params.get("enabled");

      if (facilityTypeId != null) {
        sql.append("F.typeId = " + facilityTypeId + " AND ");
      }
      if (geographicZoneId != null) {
        sql.append("F.geographicZoneId =" + geographicZoneId + " AND ");
      }
      sql.append(
        "(LOWER(F.code) LIKE LOWER('%" + facilityCodeName + "%') OR LOWER(F.name) LIKE LOWER('%" + facilityCodeName + "%'))");
      if(virtualFacility != null){
        sql.append(" AND F.virtualFacility = " + virtualFacility);
      }
      if(enabled != null){
        sql.append(" AND F.enabled = " + enabled);
      }
      return sql;
    }
  }

  @Select({"SELECT id as userId, username as name, cellphone as contact ",
      "FROM users ",
      "WHERE ",
      " active = true and facilityId = #{facilityId}"})
  List<FacilityContact> getSmsContacts(Long facilityId);

  @Select({"SELECT id as userId, username as name, email as contact ",
      "FROM users ",
      "WHERE ",
      " active = true and facilityId = #{facilityId}"})
  List<FacilityContact> getEmailContacts(Long facilityId);

  @Select("SELECT * from odk_submission_data where facilityId = #{facilityId}")
  List<FacilityImages> getFacilityImages(Long facilityId);

    @Select("SELECT DISTINCT userid as userId, username as name, email as contact   \n" +
            "            FROM role_assignments  \n" +
            "            JOIN supervisory_nodes on supervisory_nodes.id = role_assignments.supervisorynodeid  \n" +
            "            JOIN users on users.id = role_assignments.userid AND users.active = true  \n" +
            "            WHERE supervisory_nodes.facilityid = #{facilityId}\n" +
            "            ORDER BY username")
    List<FacilitySupervisor> getFacilitySupervisors(Long facilityId);


  @Select("SELECT * FROM facilities where geographiczoneId = #{geographicZoneId}")
  List<Facility> getForGeographicZone(Long geographicZoneId);

  @Select("SELECT facilities.*, facility_types.name  as facilityType "+
    "FROM facilities, facility_types "+
    "WHERE   facilities.typeid = facility_types.id "+
    "ORDER BY facility_types.name, facilities.name")
  @Results(value = {
    @Result(property = "geographicZone", column = "geographicZoneId", javaType = Integer.class,
      one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getById")),
    @Result(property = "facilityType", column = "typeId", javaType = Integer.class, one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Integer.class, one = @One(select = "getFacilityOperatorById"))
  })
  List<Facility> getAllFacilitiesDetail();

  @Select("SELECT  f.*, ft.name as facilityType " +
    "FROM  facilities f" +
    "INNER JOIN facility_types ft ON f.typeid = ft.id " +
    "INNER JOIN facility_operators fo ON f.operatedbyid = fo.id "+
    "ORDER BY ft.name, f.name;")
  @Results(value = {
    @Result(property = "geographicZone", column = "geographicZoneId", javaType = Integer.class,
      one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getGeographicZoneById")),
    @Result(property = "facilityType", column = "typeId", javaType = Integer.class, one = @One(select = "getFacilityTypeById")),
    @Result(property = "operatedBy", column = "operatedById", javaType = Integer.class, one = @One(select = "getFacilityOperatorById"))
  })
  List<Facility> getMailingLabels();


  @Select("SELECT DISTINCT f.*\n" +
          "          FROM facilities f\n" +
          "          INNER JOIN requisition_group_members rgm ON f.id= rgm.facilityId\n" +
          "          INNER JOIN facility_types FT on FT.id = f.typeId \n" +
          "          WHERE ft.id = CASE WHEN COALESCE(#{facilityTypeId}, 0) = 0 THEN ft.id ELSE #{facilityTypeId} END\n" +
          "           AND rgm.requisitionGroupId = CASE WHEN COALESCE(#{requisitionGroupId}, 0) = 0 THEN rgm.requisitionGroupId ELSE #{requisitionGroupId} END\n" +
          "          AND f.active = TRUE\n" +
          "          AND f.virtualFacility = FALSE ")
  @Results(value = {
          @Result(property = "geographicZone.id", column = "geographicZoneId"),
          @Result(property = "facilityType", column = "typeId", javaType = Long.class,
                  one = @One(select = "getFacilityTypeById")),
          @Result(property = "operatedBy", column = "operatedById", javaType = Long.class,
                  one = @One(select = "getFacilityOperatorById"))
  })
  List<Facility> getFacilitiesByTypeAndRequisitionGroupId(@Param(value = "facilityTypeId") Long facilityTypeId,
                                 @Param(value = "requisitionGroupId") Long requisitionGroupId);


    //-===============================

    @Select("select distinct region.id, region.name, 0 as facility, vw_user_districts.user_id as userId \n" +
            "from geographic_zones region \n" +
            "inner join geographic_levels ON region.levelid = geographic_levels.id AND geographic_levels.code = 'reg'\n" +
            "inner join geographic_zones district ON district.parentid = region.id\n" +
            "inner join vw_user_districts ON vw_user_districts.district_id = district.id where vw_user_districts.user_id = #{userId} order by region.name")
    @Results(value = {
            @Result(property = "children", column = "{id=id, userId=userId}", javaType = List.class,  many = @Many(select = "getGeoTreeDistrictsByRegion"))
        })
    List<FacilityGeoTreeDto> getGeoRegionFacilityTree(@Param(value = "userId") Long userId);

    @Select("select distinct district.id, district.name, 0 as facility, vw_user_districts.user_id as userId \n" +
            "from geographic_zones district \n" +
            "inner join vw_user_districts ON vw_user_districts.district_id = district.id AND district.parentid = #{id}\n" +
            "where vw_user_districts.user_id = #{userId} order by district.name")
    @Results(value = {
            @Result(property = "children",  column = "{id=id, userId=userId}", javaType = List.class,  many = @Many(select = "getGeoTreeFacilities"))
    })
    List<FacilityGeoTreeDto> getGeoTreeDistrictsByRegion(Map params);

    @Select("select facilities.id, facilities.name, 1 as facility from facilities inner join \n" +
            "vw_user_facilities ON facilities.id = vw_user_facilities.facility_id AND vw_user_facilities.user_id = #{userId} " +
            "AND vw_user_facilities.district_id = #{id}")
    List<FacilityGeoTreeDto> getGeoTreeFacilities(Map params);


    @Select("select distinct district.id, district.name, 0 as facility, vw_user_districts.user_id as userId \n" +
            "from geographic_zones district \n" +
            "inner join vw_user_districts ON vw_user_districts.district_id = district.id\n" +
            "where vw_user_districts.user_id = #{userId} order by district.name")
    @Results(value = {
            @Result(property = "children",  column = "{id=id, userId=userId}", javaType = List.class,  many = @Many(select = "getGeoTreeFacilities"))
    })
    List<FacilityGeoTreeDto> getGeoTreeDistricts(@Param(value = "userId") Long userId);


    @Select("select facilities.id, facilities.name, 1 as facility from facilities inner join \n" +
            "vw_user_facilities ON facilities.id = vw_user_facilities.facility_id AND vw_user_facilities.user_id = #{userId}")
    List<FacilityGeoTreeDto> getGeoTreeFlatFacilities(@Param(value = "userId") Long userId);
}