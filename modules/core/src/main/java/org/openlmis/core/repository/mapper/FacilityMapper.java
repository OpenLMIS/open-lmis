package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionHeader;

import java.util.List;

public interface FacilityMapper {

    @Select("SELECT * FROM FACILITY")
    @Results(value = {
            @Result(property = "code", column = "code"),
            @Result(property = "name", column = "name"),
            @Result(property = "type", column = "type"),
            @Result(property = "geographicZone", column = "geographic_zone_id")
    })
    List<Facility> getAll();

    @Insert("Insert into facility(code, name, type, geographic_zone_id) values(#{code}, #{name}, #{type}, #{geographicZone})")
    int insert(Facility facility);

    @Delete("DELETE FROM FACILITY")
    void deleteAll();

    @Insert("Insert into programs_supported(facility_code,program_id,active) values(#{facilityCode},#{programId},#{isActive})")
    int map(@Param("facilityCode") String facilityCode, @Param("programId") int programId, @Param("isActive") boolean isActive);

    @Delete("DELETE From programs_supported")
    void deleteProgramMappings();

    @Select("SELECT F.name, F.code, FT.facility_type_name, FT.nominal_max_month, " +
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
            @Result(property = "facilityType", column = "facility_type_name"),
            @Result(property = "maximumStockLevel", column = "nominal_max_month"),
            @Result(property = "emergencyOrderPoint", column = "nominal_eop"),
            @Result(property = "zone.value", column = "zone"),
            @Result(property = "zone.label", column = "label"),
            @Result(property = "parentZone.value", column = "parent_zone"),
            @Result(property = "parentZone.label", column = "parent_label")
    })
    RequisitionHeader getRequisitionHeaderData(String facilityCode);
}
