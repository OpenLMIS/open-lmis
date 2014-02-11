package org.openlmis.core.repository.mapper;


import org.apache.ibatis.annotations.*;
import org.openlmis.core.dto.GeographicZoneGeometry;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface GeographicZoneGeoJSONMapper {

  @Select("SELECT * FROM geographic_zone_geojson " +
    "WHERE id=#{id}")
  GeographicZoneGeometry getGeographicZoneGeoJSONbyId(@Param("id") Long id);

  @Select("SELECT * FROM geographic_zone_geojson " +
    "WHERE zoneId=#{zoneId}")
  GeographicZoneGeometry getGeographicZoneGeoJSONbyZoneId(@Param("zoneId") Long zoneId);

  @Insert("INSERT INTO geographic_zone_geojson ( zoneId, geoJsonId, geometry, createdDate, createdBy, modifiedBy, modifiedDate)  VALUES" +
    "( #{zoneId}, #{geoJsonId}, #{geometry}, COALESCE(#{createdDate}, NOW()), #{createdBy}, #{modifiedBy}, " +
    "COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
  @Options(useGeneratedKeys = true)
  Integer insert(GeographicZoneGeometry geometry);

  @Update("UPDATE geographic_zone_geojson SET zoneId = #{zoneId}, geoJsonId = #{geoJsonId}, geometry = #{geometry}," +
    "modifiedBy = #{modifiedBy}, modifiedDate = (COALESCE(#{modifiedDate}, NOW())) WHERE id=#{id}")
  void update(GeographicZoneGeometry geometry);
}
