package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityMapper {

  @Select("Insert into facilities(code, name, description, gln, mainPhone, fax, address1, address2, " +
      "geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById," +
      "coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, online," +
      "satellite, satelliteParentCode, hasElectricity, hasElectronicScc, hasElectronicDar, active," +
      "goLiveDate, goDownDate, comment, dataReportable, modifiedBy, modifiedDate) " +
      "values(#{code}, #{name}, #{description}, #{gln}, #{mainPhone}, #{fax}, #{address1}, #{address2}," +
      "#{geographicZone.id}," +
      "#{facilityType.id}," +
      "#{catchmentPopulation}, #{latitude}, #{longitude}, #{altitude}," +
      "#{operatedBy.id}," +
      "#{coldStorageGrossCapacity}, #{coldStorageNetCapacity}, #{suppliesOthers}, #{sdp},#{online}," +
      "#{satellite}, #{satelliteParentCode}, #{hasElectricity}, #{hasElectronicScc}, #{hasElectronicDar}, #{active}," +
      "#{goLiveDate}, #{goDownDate}, #{comment}, #{dataReportable}, #{modifiedBy}, #{modifiedDate}) returning id")
  @Options(useGeneratedKeys = true)
  Integer insert(Facility facility);

  @Select("SELECT * FROM facilities")
  @Results(value = {
      @Result(property = "geographicZone.id", column = "geographicZoneId"),
      @Result(property = "facilityType", column = "typeId", javaType = Integer.class, one = @One(select = "getFacilityTypeById")),
      @Result(property = "operatedBy", column = "operatedById", javaType = Integer.class, one = @One(select = "getFacilityOperatorById")),
  })
  List<Facility> getAll();

  @Select("SELECT * FROM users U, facilities F " +
      "WHERE U.facilityId = F.id AND U.userName = #{userName} AND f.active = true")
  @Results(value = {@Result(property = "id", column = "facilityId")})
  Facility getHomeFacility(String userName);

  @Select("SELECT F.name as facilityName,  F.code as facilityCode, F.operatedById, " +
      "FT.name as facilityType, FT.nominal_max_month, " +
      "FT.nominal_eop, GZ.name as zone, GL.name as label, GZP.name as parent_zone, GLP.name as parent_label " +
      "FROM facilities F, facility_type FT, geographic_zones GZ, geographic_zones GZP, geopolitical_levels GL, geopolitical_levels GLP " +
      "WHERE F.id = #{facilityId} AND " +
      "F.typeId = FT.id AND " +
      "F.geographicZoneId = GZ.id AND " +
      "GZ.parent = GZP.id AND " +
      "GZ.level = GL.id AND " +
      "GZP.level = GLP.id")
  @Results(value = {
      @Result(property = "facilityOperatedBy", column = "operatedById", javaType = String.class,
          one = @One(select = "getFacilityOperatorCodeFor")),
      @Result(property = "maximumStockLevel", column = "nominal_max_month"),
      @Result(property = "emergencyOrderPoint", column = "nominal_eop"),
      @Result(property = "zone.value", column = "zone"),
      @Result(property = "zone.label", column = "label"),
      @Result(property = "parentZone.value", column = "parent_zone"),
      @Result(property = "parentZone.label", column = "parent_label")
  })
  RequisitionHeader getRequisitionHeaderData(Integer facilityId);


  @Select("SELECT * FROM facility_type ORDER BY display_order")
  @Results(value = {
      @Result(property = "levelId", column = "level_id"),
      @Result(property = "nominalMaxMonth", column = "nominal_max_month"),
      @Result(property = "nominalEop", column = "nominal_eop"),
      @Result(property = "displayOrder", column = "display_order"),
      @Result(property = "active", column = "is_active")
  })
  List<FacilityType> getAllTypes();

  @Select("SELECT * FROM facility_type where id = #{id}")
  @Results(value = {
      @Result(property = "levelId", column = "level_id"),
      @Result(property = "nominalMaxMonth", column = "nominal_max_month"),
      @Result(property = "nominalEop", column = "nominal_eop"),
      @Result(property = "displayOrder", column = "display_order"),
      @Result(property = "active", column = "is_active")
  })
  public FacilityType getFacilityTypeById(Integer id);

  @Select("SELECT * FROM facility_operators ORDER BY displayOrder")
  List<FacilityOperator> getAllOperators();

  @Select("SELECT * FROM facility_operators WHERE id = #{id}")
  public FacilityOperator getFacilityOperatorById(Integer id);

  @Select("SELECT code FROM facility_operators where id = #{id}")
  @SuppressWarnings("unused")
  public String getFacilityOperatorCodeFor(Integer id);

  @Select("SELECT id FROM facility_operators where LOWER(code) = LOWER(#{code})")
  Integer getOperatedByIdForCode(String code);


  @Select("SELECT GZ.id as id, GZ.name as value, GL.name as label FROM geographic_zones GZ, geopolitical_levels GL where GZ.level = GL.id")
  List<GeographicZone> getAllGeographicZones();

  @Select("SELECT * FROM facilities WHERE id = #{id}")
  @Results(value = {
      @Result(property = "geographicZone.id", column = "geographicZoneId"),
      @Result(property = "facilityType", column = "typeId", javaType = Integer.class, one = @One(select = "getFacilityTypeById")),
      @Result(property = "operatedBy", column = "operatedById", javaType = Integer.class, one = @One(select = "getFacilityOperatorById")),
      @Result(property = "suppliesOthers", column = "supplies_others")
  })
  Facility get(Integer id);

  @Update("UPDATE facilities SET code = #{code}, name = #{name}, description = #{description}, gln = #{gln}," +
      "mainPhone = #{mainPhone}, fax = #{fax}, address1 = #{address1}," +
      "address2 = #{address2}, geographicZoneId = #{geographicZone.id}," +
      "typeId = #{facilityType.id}, catchmentPopulation = #{catchmentPopulation}, latitude = #{latitude}," +
      "longitude = #{longitude}, altitude = #{altitude}," +
      "operatedById = #{operatedBy.id}," +
      "coldStorageGrossCapacity = #{coldStorageGrossCapacity}, coldStorageNetCapacity = #{coldStorageNetCapacity}," +
      "suppliesOthers = #{suppliesOthers}, sdp = #{sdp}, online = #{online}, satellite = #{satellite}, satelliteParentCode = #{satelliteParentCode}," +
      "hasElectricity = #{hasElectricity}, hasElectronicScc = #{hasElectronicScc}, hasElectronicDar = #{hasElectronicDar}, active = #{active}," +
      "goLiveDate = #{goLiveDate}, goDownDate = #{goDownDate}," +
      "comment = #{comment}, dataReportable = #{dataReportable}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} WHERE id=#{id}")
  void update(Facility facility);

  @Select("SELECT id FROM facility_type where LOWER(code) = LOWER(#{code})")
  Integer getFacilityTypeIdForCode(String facilityTypeCode);

  @Update("UPDATE facilities SET dataReportable = #{dataReportable}, active=#{active}, modifiedBy=#{modifiedBy}, modifiedDate= DEFAULT " +
      "WHERE id =#{id}")
  void updateDataReportableAndActiveFor(Facility facility);

  @Select("SELECT id FROM facilities WHERE LOWER(code) = LOWER(#{code})")
  Integer getIdForCode(String code);

  @Select("select 0<(select count(id) as count from geographic_zones where id=#{geographicZoneId})")
  Boolean isGeographicZonePresent(Integer geographicZoneId);
}
