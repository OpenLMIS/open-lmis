package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Budget;
import org.springframework.stereotype.Repository;

/**
 * User: Wolde
 * Date: 7/27/13
 * Time: 3:47 PM
 */
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
