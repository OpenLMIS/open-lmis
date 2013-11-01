/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository
public interface GeographicZoneMapperExtension extends GeographicZoneMapper {

    @Select(value = "SELECT * FROM geographic_zones where LOWER(name) like '%'|| LOWER(#{geographicZoneSearchParam}) ||'%' OR LOWER(code) like '%'|| " +
            "LOWER(#{geographicZoneSearchParam}) ||'%' ")
    List<GeographicZone> getGeographicZoneWithSearchedName(String geographicZoneSearchParam);

    @Select({"SELECT GZ.id AS id, GZ.code AS code, GZ.name AS name, GZ.catchmentPopulation, GZ.longitude, GZ.latitude, GL.code AS levelCode, GL.name AS level, GZP.code AS parentCode, GZP.name AS parentZone, GLP.code AS parentLevelCode, GLP.name AS parentLevel, GZP.ID AS parentId, GL.ID AS levelId ",
            "FROM geographic_zones GZ LEFT JOIN geographic_zones GZP ON GZ.parentId = GZP.id ",
            "INNER JOIN geographic_levels GL ON GZ.levelId = GL.id ",
            "LEFT JOIN geographic_levels GLP ON GZP.levelId = GLP.id ",
            "WHERE GZ.id = #{geographicZoneId} "})
    @Results(value = {
            @Result(property = "level.code", column = "levelCode"),
            @Result(property = "level.name", column = "level"),
            @Result(property = "level.id", column = "levelId"),
            @Result(property = "parent.id", column="parentId"),
            @Result(property = "parent.name", column = "parentZone"),
            @Result(property = "parent.code", column = "parentCode"),
            @Result(property = "parent.level.code", column = "parentLevelCode"),
            @Result(property = "parent.level.name", column = "parentLevel")
    })
    GeographicZone getGeographicZoneById_Ext(Integer geographicZoneId);


    @Select({"SELECT GZ.id AS id, GZ.code AS code, GZ.name AS name, GZ.catchmentPopulation, GZ.longitude, GZ.latitude, GL.code AS levelCode, GL.name AS level, GZP.code AS parentCode, GZP.name AS parentZone, GLP.code AS parentLevelCode, GLP.name AS parentLevel, GZP.ID AS parentId, GL.ID AS levelId " +
            "FROM geographic_zones GZ LEFT JOIN geographic_zones GZP ON GZ.parentId = GZP.id " +
            "INNER JOIN geographic_levels GL ON GZ.levelId = GL.id " +
            "LEFT JOIN geographic_levels GLP ON GZP.levelId = GLP.id " +
            "ORDER BY GL.name, GZP.name, GZ.name "})
    @Results({
            @Result(property = "level.code", column = "levelCode"),
            @Result(property = "level.name", column = "level"),
            @Result(property = "level.id", column = "levelId"),
            @Result(property = "parent.id", column="parentId"),
            @Result(property = "parent.name", column = "parentZone"),
            @Result(property = "parent.code", column = "parentCode"),
            @Result(property = "parent.level.code", column = "parentLevelCode"),
            @Result(property = "parent.level.name", column = "parentLevel")
    })
    List<GeographicZone> getAllGeographicZones_Ext();


    @Insert("INSERT INTO geographic_zones (code, name, levelId, parentId, createdBy, modifiedBy, modifiedDate) " +
            "VALUES (#{code}, #{name}, #{level.id}, #{parent.id}, #{createdBy}, #{modifiedBy}, #{modifiedDate})")
    Integer insert_Ext(GeographicZone geographicZone);
}
