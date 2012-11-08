package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;

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
    void insert(Facility facility);

    @Delete("DELETE FROM FACILITY")
    void deleteAll();

    @Insert("Insert into programs_supported(facility_code,program_id,active) values(#{facilityCode},#{programId},#{isActive})")
    int map(@Param("facilityCode") String facilityCode, @Param("programId") int programId, @Param("isActive") boolean isActive);

    @Delete("DELETE From programs_supported")
    void deleteProgramMappings();
}
