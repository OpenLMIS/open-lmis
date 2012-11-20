package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionHeader;

import java.util.List;

public interface FacilityMapper {

    @Insert("Insert into facility(code, name, description,gln,main_phone,fax,address1,address2, " +
            "geographic_zone_id,type,catchment_population,latitude,longitude,altitude,operated_by," +
            "cold_storage_gross_capacity,cold_storage_net_capacity,supplies_others,is_sdp,is_online," +
            "is_satellite,satellite_parent_code,has_electricity,has_electronic_scc,has_electronic_dar,is_active," +
            "go_live_date,go_down_date,comment,do_not_display,modified_by,modified_date) " +
            "values(#{code}, #{name},#{description},#{GLN},#{mainPhone},#{fax},#{address1}, #{address2}," +
            "#{geographicZone},#{type},#{catchmentPopulation},#{latitude},#{longitude},#{altitude},#{operatedBy}," +
            "#{coldStorageGrossCapacity},#{coldStorageNetCapacity},#{suppliesOthers},#{sdp},#{online}," +
            "#{satellite},#{satelliteParentCode},#{hasElectricity},#{hasElectronicScc},#{hasElectronicDar},#{active}," +
            "#{goLiveDate},#{goDownDate},#{comment},#{doNotDisplay},#{modifiedBy},#{modifiedDate})")
    int insert(Facility facility);

    @Select("SELECT * FROM FACILITY")
    @Results(value = {
            @Result(property = "code", column = "code"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "gln", column = "gln"),
            @Result(property = "mainPhone", column = "main_phone"),
            @Result(property = "fax", column = "fax"),
            @Result(property = "address1", column = "address1"),
            @Result(property = "address2", column = "address2"),
            @Result(property = "geographicZone", column = "geographic_zone_id"),
            @Result(property = "type", column = "type"),
            @Result(property = "catchmentPopulation", column = "catchment_population"),
            @Result(property = "latitude", column = "latitude"),
            @Result(property = "longitude", column = "longitude"),
            @Result(property = "altitude", column = "altitude"),
            @Result(property = "operatedBy", column = "operated_by"),
            @Result(property = "coldStorageGrossCapacity", column = "cold_storage_gross_capacity"),
            @Result(property = "coldStorageNetCapacity", column = "cold_storage_net_capacity"),
            @Result(property = "suppliesOthers", column = "supplies_others"),
            @Result(property = "sdp", column = "is_sdp"),
            @Result(property = "online", column = "is_online"),
            @Result(property = "satellite", column = "is_satellite"),
            @Result(property = "satelliteParentCode", column = "satellite_parent_code"),
            @Result(property = "hasElectricity", column = "has_electricity"),
            @Result(property = "hasElectronicScc", column = "has_electronic_scc"),
            @Result(property = "active", column = "is_active"),
            @Result(property = "goLiveDate", column = "go_live_date"),
            @Result(property = "goDownDate", column = "go_down_date"),
            @Result(property = "comment", column = "comment"),
            @Result(property = "doNotDisplay", column = "do_not_display"),
            @Result(property = "modifiedBy", column = "modified_by"),
            @Result(property = "modifiedDate", column = "modified_date")
    })
    List<Facility> getAll();

    @Delete("DELETE FROM FACILITY")
    void deleteAll();

    @Select("SELECT F.name, F.code,F.operated_by as operated_by, FT.name as facility_type, FT.nominal_max_month, " +
            "FT.nominal_eop, GZ.name as zone, GL.name as label, GZP.name as parent_zone, GLP.name as parent_label " +
            "FROM facility F, facility_type FT, geographic_zone GZ, geographic_zone GZP, geopolitical_level GL, geopolitical_level GLP " +
            "WHERE F.code = #{facilityCode} AND " +
            "F.type = FT.id AND " +
            "F.geographic_zone_id = GZ.id AND " +
            "GZ.parent = GZP.id AND " +
            "GZ.level = GL.id AND " +
            "GZP.level = GLP.id")
    @Results(value = {
            @Result(property = "facilityName", column = "name"),
            @Result(property = "facilityCode", column = "code"),
            @Result(property = "facilityOperatedBy", column = "operated_by"),
            @Result(property = "facilityType", column = "facility_type"),
            @Result(property = "maximumStockLevel", column = "nominal_max_month"),
            @Result(property = "emergencyOrderPoint", column = "nominal_eop"),
            @Result(property = "zone.value", column = "zone"),
            @Result(property = "zone.label", column = "label"),
            @Result(property = "parentZone.value", column = "parent_zone"),
            @Result(property = "parentZone.label", column = "parent_label")
    })
    RequisitionHeader getRequisitionHeaderData(String facilityCode);
}
