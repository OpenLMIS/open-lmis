package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityMapper {

  @Select("Insert into facility(code, name, description,gln,main_phone,fax,address1,address2, " +
      "geographic_zone_id,type_id,catchment_population,latitude,longitude,altitude,operated_by_id," +
      "cold_storage_gross_capacity,cold_storage_net_capacity,supplies_others,is_sdp,is_online," +
      "is_satellite,satellite_parent_code,has_electricity,has_electronic_scc,has_electronic_dar,active," +
      "go_live_date,go_down_date,comment,data_reportable,modified_by,modified_date) " +
      "values(#{code}, #{name},#{description},#{gln},#{mainPhone},#{fax},#{address1}, #{address2}," +
      "#{geographicZone.id}," +
      "#{facilityType.id}," +
      "#{catchmentPopulation},#{latitude},#{longitude},#{altitude}," +
      "#{operatedBy.id}," +
      "#{coldStorageGrossCapacity},#{coldStorageNetCapacity},#{suppliesOthers},#{sdp},#{online}," +
      "#{satellite},#{satelliteParentCode},#{hasElectricity},#{hasElectronicScc},#{hasElectronicDar},#{active}," +
      "#{goLiveDate},#{goDownDate},#{comment},#{dataReportable},#{modifiedBy},#{modifiedDate}) returning id")
  @Options(useGeneratedKeys = true)
  Long insert(Facility facility);

  @Select("SELECT id, code, name, description,gln,main_phone,fax,address1,address2," +
      "geographic_zone_id,type_id,catchment_population,latitude,longitude,altitude,operated_by_id," +
      "cold_storage_gross_capacity,cold_storage_net_capacity,supplies_others,is_sdp,is_online," +
      "is_satellite,satellite_parent_code,has_electricity,has_electronic_scc,has_electronic_dar,active," +
      "go_live_date,go_down_date,comment,data_reportable,modified_by,modified_date " +
      "FROM FACILITY")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "code", column = "code"),
      @Result(property = "name", column = "name"),
      @Result(property = "description", column = "description"),
      @Result(property = "gln", column = "gln"),
      @Result(property = "mainPhone", column = "main_phone"),
      @Result(property = "fax", column = "fax"),
      @Result(property = "address1", column = "address1"),
      @Result(property = "address2", column = "address2"),
      @Result(property = "geographicZone.id", column = "geographic_zone_id"),
      @Result(property = "facilityType", column = "type_id", javaType = Long.class,
          one = @One(select = "getFacilityTypeById")),
      @Result(property = "catchmentPopulation", column = "catchment_population"),
      @Result(property = "latitude", column = "latitude"),
      @Result(property = "longitude", column = "longitude"),
      @Result(property = "altitude", column = "altitude"),
      @Result(property = "operatedBy", column = "operated_by_id", javaType = Long.class, one = @One(select = "getFacilityOperatorById")),
      @Result(property = "coldStorageGrossCapacity", column = "cold_storage_gross_capacity"),
      @Result(property = "coldStorageNetCapacity", column = "cold_storage_net_capacity"),
      @Result(property = "suppliesOthers", column = "supplies_others"),
      @Result(property = "sdp", column = "is_sdp"),
      @Result(property = "online", column = "is_online"),
      @Result(property = "satellite", column = "is_satellite"),
      @Result(property = "satelliteParentCode", column = "satellite_parent_code"),
      @Result(property = "hasElectricity", column = "has_electricity"),
      @Result(property = "hasElectronicScc", column = "has_electronic_scc"),
      @Result(property = "active", column = "active"),
      @Result(property = "goLiveDate", column = "go_live_date"),
      @Result(property = "goDownDate", column = "go_down_date"),
      @Result(property = "comment", column = "comment"),
      @Result(property = "dataReportable", column = "data_reportable"),
      @Result(property = "modifiedBy", column = "modified_by"),
      @Result(property = "modifiedDate", column = "modified_date")
  })
  List<Facility> getAll();

  @Delete("DELETE FROM FACILITY")
  void deleteAll();

  @Select("SELECT * FROM users U, facility F " +
      "where U.facility_id = F.id and U.user_name = #{userName} and f.active = true")
  @Results(value = {
      @Result(property = "id", column = "facility_id"),
      @Result(property = "name"),
      @Result(property = "code")
  })
  Facility getHomeFacility(String userName);

  @Select("SELECT F.name, F.code,F.operated_by_id as operated_by_id, FT.name as facility_type, FT.nominal_max_month, " +
      "FT.nominal_eop, GZ.name as zone, GL.name as label, GZP.name as parent_zone, GLP.name as parent_label " +
      "FROM facility F, facility_type FT, geographic_zone GZ, geographic_zone GZP, geopolitical_level GL, geopolitical_level GLP " +
      "WHERE F.id = #{facilityId} AND " +
      "F.type_id = FT.id AND " +
      "F.geographic_zone_id = GZ.id AND " +
      "GZ.parent = GZP.id AND " +
      "GZ.level = GL.id AND " +
      "GZP.level = GLP.id")
  @Results(value = {
      @Result(property = "facilityName", column = "name"),
      @Result(property = "facilityCode", column = "code"),
      @Result(property = "facilityOperatedBy", column = "operated_by_id", javaType = java.lang.String.class,
          one = @One(select = "getFacilityOperatorCodeFor")),
      @Result(property = "facilityType", column = "facility_type"),
      @Result(property = "maximumStockLevel", column = "nominal_max_month"),
      @Result(property = "emergencyOrderPoint", column = "nominal_eop"),
      @Result(property = "zone.value", column = "zone"),
      @Result(property = "zone.label", column = "label"),
      @Result(property = "parentZone.value", column = "parent_zone"),
      @Result(property = "parentZone.label", column = "parent_label")
  })
  RequisitionHeader getRequisitionHeaderData(Long facilityId);


  @Select("SELECT * FROM facility_type ORDER BY display_order")
  @Results(value = {
      @Result(property = "code", column = "code"),
      @Result(property = "name", column = "name"),
      @Result(property = "description", column = "description"),
      @Result(property = "levelId", column = "level_id"),
      @Result(property = "nominalMaxMonth", column = "nominal_max_month"),
      @Result(property = "nominalEop", column = "nominal_eop"),
      @Result(property = "displayOrder", column = "display_order"),
      @Result(property = "active", column = "is_active")
  })
  List<FacilityType> getAllTypes();

  @Select("SELECT id, code, name, description, level_id, nominal_max_month, nominal_eop, display_order, is_active " +
      "FROM facility_type where id = #{id}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "code", column = "code"),
      @Result(property = "name", column = "name"),
      @Result(property = "description", column = "description"),
      @Result(property = "levelId", column = "level_id"),
      @Result(property = "nominalMaxMonth", column = "nominal_max_month"),
      @Result(property = "nominalEop", column = "nominal_eop"),
      @Result(property = "displayOrder", column = "display_order"),
      @Result(property = "active", column = "is_active")
  })
  public FacilityType getFacilityTypeById(Long id);

  @Select("SELECT id, code, text, display_order FROM facility_operator ORDER BY display_order")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "code", column = "code"),
      @Result(property = "text", column = "text"),
      @Result(property = "displayOrder", column = "display_order")
  })
  List<FacilityOperator> getAllOperators();

  @Select("SELECT id, code, text, display_order FROM facility_operator WHERE id = #{id}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "code", column = "code"),
      @Result(property = "text", column = "text"),
      @Result(property = "displayOrder", column = "display_order")
  })
  public FacilityOperator getFacilityOperatorById(Long id);

  @Select("SELECT GZ.id as id, GZ.name as value, GL.name as label FROM geographic_zone GZ, geopolitical_level GL where GZ.level = GL.id")
  List<GeographicZone> getAllGeographicZones();

  @Select("SELECT * FROM FACILITY WHERE id = #{id}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "code", column = "code"),
      @Result(property = "name", column = "name"),
      @Result(property = "description", column = "description"),
      @Result(property = "gln", column = "gln"),
      @Result(property = "mainPhone", column = "main_phone"),
      @Result(property = "fax", column = "fax"),
      @Result(property = "address1", column = "address1"),
      @Result(property = "address2", column = "address2"),
      @Result(property = "geographicZone.id", column = "geographic_zone_id"),
      @Result(property = "facilityType", column = "type_id", javaType = Long.class, one = @One(select = "getFacilityTypeById")),
      @Result(property = "catchmentPopulation", column = "catchment_population"),
      @Result(property = "latitude", column = "latitude"),
      @Result(property = "longitude", column = "longitude"),
      @Result(property = "altitude", column = "altitude"),
      @Result(property = "operatedBy", column = "operated_by_id", javaType = Long.class, one = @One(select = "getFacilityOperatorById")),
      @Result(property = "coldStorageGrossCapacity", column = "cold_storage_gross_capacity"),
      @Result(property = "coldStorageNetCapacity", column = "cold_storage_net_capacity"),
      @Result(property = "suppliesOthers", column = "supplies_others"),
      @Result(property = "sdp", column = "is_sdp"),
      @Result(property = "online", column = "is_online"),
      @Result(property = "satellite", column = "is_satellite"),
      @Result(property = "satelliteParentCode", column = "satellite_parent_code"),
      @Result(property = "hasElectricity", column = "has_electricity"),
      @Result(property = "hasElectronicScc", column = "has_electronic_scc"),
      @Result(property = "hasElectronicDar", column = "has_electronic_dar"),
      @Result(property = "active", column = "active"),
      @Result(property = "goLiveDate", column = "go_live_date"),
      @Result(property = "goDownDate", column = "go_down_date"),
      @Result(property = "comment", column = "comment"),
      @Result(property = "dataReportable", column = "data_reportable"),
      @Result(property = "modifiedBy", column = "modified_by"),
      @Result(property = "modifiedDate", column = "modified_date")
  })
  Facility get(Long id);


  @Update("UPDATE facility SET code=#{code},name=#{name},description=#{description},gln=#{gln},main_phone=#{mainPhone},fax=#{fax},address1=#{address1}," +
      "address2=#{address2},geographic_zone_id=#{geographicZone.id}," +
      "type_id=#{facilityType.id},catchment_population=#{catchmentPopulation},latitude=#{latitude}," +
      "longitude=#{longitude},altitude=#{altitude}," +
      "operated_by_id=#{operatedBy.id}," +
      "cold_storage_gross_capacity=#{coldStorageGrossCapacity},cold_storage_net_capacity=#{coldStorageNetCapacity}," +
      "supplies_others=#{suppliesOthers},is_sdp=#{sdp},is_online=#{online},is_satellite=#{satellite},satellite_parent_code=#{satelliteParentCode}," +
      "has_electricity=#{hasElectricity}," +
      "has_electronic_scc=#{hasElectronicScc},has_electronic_dar=#{hasElectronicDar},active=#{active},go_live_date=#{goLiveDate},go_down_date=#{goDownDate}," +
      "comment=#{comment},data_reportable=#{dataReportable},modified_by=#{modifiedBy},modified_date=#{modifiedDate} WHERE id=#{id}")
  void update(Facility facility);

  @Select("SELECT id FROM facility_operator where LOWER(code) = LOWER(#{code})")
  Long getOperatedByIdForCode(String code);

  @Select("SELECT id FROM facility_type where LOWER(code) = LOWER(#{code})")
  Long getFacilityTypeIdForCode(String facilityTypeCode);

  @Select("SELECT code FROM facility_operator where id = #{id}")
  public String getFacilityOperatorCodeFor(Long id);

  @Select("SELECT code FROM facility_type where id = #{id}")
  public String getFacilityTypeCodeFor(Long id);

  @Update("UPDATE facility SET data_reportable=#{dataReportable}, active=#{active}, modified_by=#{modifiedBy}, modified_date= DEFAULT    " +
      "WHERE id =#{id}")
  void updateDataReportableAndActiveFor(Facility facility);

  @Select("SELECT id FROM facility WHERE LOWER(code) = LOWER(#{code})")
  Long getIdForCode(String code);

  @Select("select 0<(select count(id) as count from geographic_zone where id=#{geographicZoneId})")
  Boolean isGeographicZonePresent(Long geographicZoneId);
}
