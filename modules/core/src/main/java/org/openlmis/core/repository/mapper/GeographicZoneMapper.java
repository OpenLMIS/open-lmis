package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeographicZoneMapper {

  @Insert("INSERT INTO geographic_zones (code, name, levelId, parent, modifiedBy, modifiedDate) " +
    "VALUES (#{code}, #{name}, #{level.id}, #{parent.id}, #{modifiedBy}, DEFAULT)")
  @Options(useGeneratedKeys = true)
  Integer insert(GeographicZone geographicZone);

  @Select("SELECT * FROM geographic_levels WHERE LOWER(code) = LOWER(#{code})")
  GeographicLevel getGeographicLevelByCode(String code);

  @Select({"SELECT GZ.id, GZ.code, GZ.name, GZ.modifiedDate, GL.id as levelId, GL.code as levelCode, GL.name as levelName,",
    "GL.levelNumber as levelNumber FROM",
    "geographic_zones GZ, geographic_levels GL WHERE LOWER(GZ.code) = LOWER(#{code}) AND GZ.levelId = GL.id"})
  @Results({
    @Result(column = "levelId", property = "level.id"),
    @Result(column = "levelCode", property = "level.code"),
    @Result(column = "levelName", property = "level.name"),
    @Result(column = "levelNumber", property = "level.levelNumber")
  })
  GeographicZone getGeographicZoneByCode(String code);

  @Select({"SELECT GZ.id, GZ.code, GZ.name, GL.id as levelId, GL.code as levelCode, GL.name as levelName, GL.levelNumber as levelNumber ",
    "FROM geographic_zones GZ, geographic_levels GL ",
    "where GZ.levelId = GL.id AND LOWER(GZ.code) <> 'root' AND ",
    "GL.levelNumber = (SELECT MAX(levelNumber) FROM geographic_levels)"})
  @Results({
    @Result(column = "levelId", property = "level.id"),
    @Result(column = "levelCode", property = "level.code"),
    @Result(column = "levelName", property = "level.name"),
    @Result(column = "levelNumber", property = "level.levelNumber")
  })
  List<GeographicZone> getAllGeographicZones();


  @Select({"SELECT GZ.id AS id, GZ.code AS code, GZ.name AS name, GL.code AS levelCode, GL.name AS level, GZP.code AS parentCode, GZP.name AS parentZone, GLP.code AS parentLevelCode, GLP.name AS parentLevel",
    "FROM geographic_zones GZ INNER JOIN geographic_zones GZP ON GZ.parent = GZP.id",
    "INNER JOIN geographic_levels GL ON GZ.levelId = GL.id",
    "INNER JOIN geographic_levels GLP ON GZP.levelId = GLP.id",
    "WHERE GZ.id = #{geographicZoneId}"})
  @Results(value = {
    @Result(property = "level.code", column = "levelCode"),
    @Result(property = "level.name", column = "level"),
    @Result(property = "parent.name", column = "parentZone"),
    @Result(property = "parent.code", column = "parentCode"),
    @Result(property = "parent.level.code", column = "parentLevelCode"),
    @Result(property = "parent.level.name", column = "parentLevel")
  })
  GeographicZone getGeographicZoneById(Integer geographicZoneId);

  @Update({"UPDATE geographic_zones set code = #{code}, name = #{name}, levelId = #{level.id}, parent = #{parent.id}, modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT",
    "WHERE id = #{id}"})
  void update(GeographicZone geographicZone);
}
