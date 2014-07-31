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
import org.openlmis.core.domain.FacilityOperator;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityOperatorMapper {

  @Insert({"INSERT INTO facility_operators (code"
    , ", text"
    , ", displayOrder"
    , ", createdBy"
    , ", createdDate"
    , ", modifiedBy"
    , ", modifiedDate"
    , ") VALUES (#{code}"
    , ", #{text}"
    , ", #{displayOrder}"
    , ", #{createdBy}"
    , ", NOW()"
    , ", #{modifiedBy}"
    , ", NOW() )"})
  void insert(FacilityOperator facilityOperator);

  @Update({"UPDATE facility_operators SET code = #{code}"
    , ", text = #{text}"
    , ", displayOrder = #{displayOrder}"
    , ", modifiedBy = #{modifiedBy}"
    , ", modifiedDate = NOW()"
    , "WHERE id = #{id}"})
  void update(FacilityOperator facilityOperator);

  @Select("SELECT * FROM facility_operators WHERE LOWER(code) = LOWER(#{code})")
  FacilityOperator getByCode(String code);

  @Select("SELECT * FROM facility_operators ORDER BY displayOrder")
  List<FacilityOperator> getAll();

  @Select("SELECT * FROM facility_operators WHERE id = #{id}")
  FacilityOperator getById(Long id);
}