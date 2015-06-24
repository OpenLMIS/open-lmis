package org.openlmis.report.mapper.lookup.vaccine;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.report.vaccine.ReplacementPlanSummary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplacementPlanSummaryReportMapper {
    @Select(
            " select region,District,facilityCode,facilityName,equipment_name equipment,energystatus sourceOfEnergy,serialNumber,total,sum(purchaseprice) purchasePrice " +
                    " from get_fn_replacement_plan_summary()  " +
                    "where counted>0 and serialNumber not in('0') and  programId=#{program} and levelId = #{regionId} and yeartoreplace = #{plannedYear}   " +
                    "group by  " +
                    "region,District,facilityCode,facilityName,equipment_name,energystatus ,serialNumber,total   " +
                    "order by region "
            )
    public List<ReplacementPlanSummary>getEquipmentInNeedOfReplacement(@Param("program") Long program,
                                                                       @Param("regionId") Long regionId,
                                                                       @Param("plannedYear") Long plannedYear
    );


}
