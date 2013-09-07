/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
            "FROM geographic_zones GZ INNER JOIN geographic_zones GZP ON GZ.parentId = GZP.id ",
            "INNER JOIN geographic_levels GL ON GZ.levelId = GL.id ",
            "INNER JOIN geographic_levels GLP ON GZP.levelId = GLP.id ",
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
            "FROM geographic_zones GZ INNER JOIN geographic_zones GZP ON GZ.parentId = GZP.id " +
            "INNER JOIN geographic_levels GL ON GZ.levelId = GL.id " +
            "INNER JOIN geographic_levels GLP ON GZP.levelId = GLP.id " +
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
