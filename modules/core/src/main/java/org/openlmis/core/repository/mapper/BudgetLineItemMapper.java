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

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.BudgetLineItem;
import org.springframework.stereotype.Repository;

/**
 * BudgetLineItemMapper maps the BudgetLineItem entity to corresponding representation in database.
 */
@Repository
public interface BudgetLineItemMapper {

  @Insert({
    "INSERT INTO budget_line_items (facilityId, programId, budgetFileId, periodId, periodDate, allocatedBudget, notes) ",
    "VALUES (#{facilityId}, #{programId}, #{budgetFileId}, #{periodId}, #{periodDate}, #{allocatedBudget}, #{notes})"
  })
  @Options(useGeneratedKeys = true)
  void insert(BudgetLineItem budgetLineItem);

  @Update({
    "UPDATE budget_line_items SET budgetFileId = #{budgetFileId}, periodDate = #{periodDate}, allocatedBudget = #{allocatedBudget}, notes = #{notes} ",
    "WHERE id = #{id}"
  })
  void update(BudgetLineItem budgetLineItem);

  @Select({
    "SELECT * FROM budget_line_items WHERE facilityId = #{facilityId} AND programId = #{programId} AND periodId = #{periodId}"
  })
  BudgetLineItem get(@Param("facilityId") Long facilityId, @Param("programId") Long programId, @Param("periodId") Long periodId);
}
