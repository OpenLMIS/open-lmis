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
import org.openlmis.core.domain.Budget;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetMapper {

    @Insert("Insert into budgets(facilityId, periodId, programId, netBudgetAmount, comment, createdDate , createdBy, modifiedBy , modifiedDate )  " +
            "values( #{facility.id}, #{period.id}, #{program.id}, #{netBudgetAmount}, #{comment},COALESCE(#{createdDate}, NOW()), #{createdBy}, #{modifiedBy}, " +
            "COALESCE(#{modifiedDate}, NOW()))")
    @Options(useGeneratedKeys = true)
    Integer insert(Budget budget);


    @Select("select b.* from budgets b " +
            " join programs p on b.programid = p.id " +
            " join processing_periods pps on b.periodid = pps.id " +
            " join facilities f on b.facilityid = f.id " +
            " where f.code = #{facilityCode} and pps.name = #{periodName} and p.code = #{programCode}")
    Budget getBudgetByReferenceCodes(
                                         @Param(value = "programCode") String programCode
                                       , @Param(value = "periodName") String periodName
                                       , @Param(value = "facilityCode") String facilityCode
                                    );


    @Update(" update budgets " +
            "   set " +
            "       facilityId = #{facility.id} , periodId = #{period.id}, programId = #{program.id}, netBudgetAmount = #{netBudgetAmount}, comment = #{comment}, createdDate = #{createdDate} , createdBy = #{createdBy}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} " +
            "   where " +
            "       id = #{id}")
    void update(Budget budget);
}
