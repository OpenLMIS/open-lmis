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
import org.openlmis.core.domain.FacilityType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityTypeMapper {

  @Select("SELECT * FROM facility_types WHERE LOWER(code) = LOWER(#{lower})")
  FacilityType getByCode(String code);

  @Select("SELECT * FROM facility_types WHERE id = #{id}")
  public FacilityType getById(Long id);

  @Insert({"INSERT INTO facility_types (code"
    , ", name"
    , ", description"
    , ", levelId"
    , ", nominalMaxMonth"
    , ", nominalEop"
    , ", displayOrder"
    , ", active"
    , ", createdBy"
    , ", createdDate"
    , ", modifiedBy"
    , ", modifiedDate"
    , ") VALUES (#{code}"
    , ", #{name}"
    , ", #{description}"
    , ", #{levelId}"
    , ", #{nominalMaxMonth}"
    , ", #{nominalEop}"
    , ", #{displayOrder}"
    , ", #{active}"
    , ", #{createdBy}"
    , ", NOW()"
    , ", #{modifiedBy}"
    , ", NOW()"
    , ")"})
  void insert(FacilityType facilityType);

  @Update({"UPDATE facility_types SET code = #{code}"
    , ", name = #{name}"
    , ", description = #{description}"
    , ", levelId = #{levelId}"
    , ", nominalMaxMonth = #{nominalMaxMonth}"
    , ", nominalEop = #{nominalEop}"
    , ", displayOrder = #{displayOrder}"
    , ", active = #{active}"
    , ", modifiedBy = #{modifiedBy}"
    , ", modifiedDate = NOW()"
    , "WHERE id = #{id}"})
  void update(FacilityType facilityType);

  @Select("SELECT * FROM facility_types ORDER BY displayOrder NULLS LAST, LOWER(name)")
  List<FacilityType> getAll();
}
