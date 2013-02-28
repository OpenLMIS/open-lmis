package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.springframework.stereotype.Repository;

@Repository
public interface GeographicZoneMapper {

  @Insert("INSERT INTO geographic_zones (code, name, level, parent, modifiedBy, modifiedDate) " +
    "VALUES (#{code}, #{name}, #{level.id}, #{parent.id}, #{modifiedBy}, DEFAULT)")
  @Options(useGeneratedKeys = true)
  Integer insert(GeographicZone geographicZone);

  @Select("SELECT * FROM geographic_levels WHERE LOWER(code) = LOWER(#{code})")
  GeographicLevel getGeographicLevelByCode(String code);

  @Select("SELECT * FROM geographic_zones WHERE LOWER(code) = LOWER(#{code})")
    @Results (
      @Result(column = "level", property = "level.id")
    )
  GeographicZone getGeographicZoneByCode(String code);
}
