/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.GeographicLevel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * GeographicLevelMapper maps the GeographicLevel entity to corresponding representation in database. Provides methods
 * like finding lowest Geographic Level.
 */
@Repository
public interface GeographicLevelMapper {

  @Select("SELECT MAX(levelNumber) FROM geographic_levels")
  Integer getLowestGeographicLevel();

  @Select("SELECT * FROM geographic_levels ORDER BY levelNumber")
  List<GeographicLevel> getAll();

  @Select("SELECT * FROM geographic_levels WHERE LOWER(code) = LOWER(#{code})")
  GeographicLevel getByCode(String code);

  @Select("SELECT * FROM geographic_levels WHERE LOWER(code) = LOWER(#{code})")
  GeographicLevel getGeographicLevelByCode(String code);

  @Insert({"INSERT INTO geographic_levels (code"
    , ", name"
    , ", levelNumber"
    , ", createdBy"
    , ", createdDate"
    , ", modifiedBy"
    , ", modifiedDate)"
    , "VALUES (#{code}"
    , ", #{name}"
    , ", #{levelNumber}"
    , ", #{createdBy}"
    , ", NOW()"
    , ", #{modifiedBy}"
    , ", NOW())"})
  void insert(GeographicLevel geographicLevel);

  @Update({"UPDATE geographic_levels SET code = #{code}"
    , ", name = #{name}"
    , ", levelNumber = #{levelNumber}"
    , ", modifiedBy = #{modifiedBy}"
    , ", modifiedDate = #{modifiedDate}"
    , "WHERE LOWER(code) = LOWER(#{code})"})
  void update(GeographicLevel geographicLevel);
}
