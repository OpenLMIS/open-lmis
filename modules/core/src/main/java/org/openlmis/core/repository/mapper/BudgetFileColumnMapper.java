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

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.EDIFileColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BudgetFileColumnMapper maps the BudgetFileColumn(EDIFileColumn) entity to corresponding representation in database.
 */
@Repository
public interface BudgetFileColumnMapper {

  @Update({"UPDATE budget_file_columns SET",
    "position = #{position},",
    "include = #{include},",
    "datePattern = #{datePattern},",
    "modifiedBy = #{modifiedBy},",
    "modifiedDate = DEFAULT ",
    "WHERE name = #{name}"})
  public void update(EDIFileColumn ediFileColumn);

  @Select("SELECT * FROM budget_file_columns ORDER BY id")
  public List<EDIFileColumn> getAll();
}
