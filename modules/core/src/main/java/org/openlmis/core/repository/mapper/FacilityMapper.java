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

    @Select("SELECT facility.name, facility.code, facility_type.facility_type_name, facility_type.nominal_max_month, facility_type.nominal_eop " +
            "FROM facility , facility_type " +
            "WHERE facility.type = facility_type.id " +
            "AND facility.code = #{facilityCode}")
    @Results(value = {
            @Result(property = "facilityName", column = "name"),
            @Result(property = "facilityCode", column = "code"),
            @Result(property = "facilityType", column = "facility_type_name"),
            @Result(property = "maximumStockLevel", column = "nominal_max_month"),
            @Result(property = "emergencyOrderPoint", column = "nominal_eop")
    })
    RequisitionHeader getRequisitionHeaderData(String facilityCode);
}
