/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
