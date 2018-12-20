package org.openlmis.restapi.mapper.intergration;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.restapi.domain.integration.RequisitionIntergration;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RequisitionIntergrationMapper {

    @Select({"SELECT requisitions.id AS requisitionId,\n" +
            "  facilities.code AS facilityCode,\n" +
            "  requisitions.id AS requisitioNnumber,\n" +
            "  extract(YEAR FROM requisitions.modifieddate) AS requisitionYear,\n" +
            "  requisitions.modifieddate AS requisitionDate,\n" +
            "  facilities.name AS facilityName,\n" +
            "  requisitions.emergency AS isEmergency,\n" +
            "    CASE WHEN requisitions.emergency is TRUE THEN 'EMERGENCIA'\n" +
            "         WHEN (extract(DAY FROM requisitions.clientsubmittedtime) - extract(DAY FROM processing_periods.enddate) < 5) THEN 'NORMAL'\n" +
            "    ELSE 'ATRASADA'\n" +
            "    END AS requistionStatus",
            "FROM requisitions\n" +
            "JOIN facilities ON requisitions.facilityid = facilities.id\n" +
            "JOIN processing_periods on requisitions.periodid = processing_periods.id " +
            "WHERE requisitions.modifieddate >= #{fromStartDate} limit #{everyPageCount} offset #{startPosition}"})
    @Results(value = {
            @Result(property = "requisitionId", column = "requisitionId"),
            @Result(property = "products", javaType = List.class, column = "requisitionId",
            many = @Many(select = "org.openlmis.restapi.mapper.intergration.RequisitionLineItemIntergrationMapper.getRequisitionLineItems")),
            @Result(property = "regimens", javaType = List.class, column = "requisitionId",
                    many = @Many(select = "org.openlmis.restapi.mapper.intergration.RestRegimenLineItemIntergrationMapper.getRegimenLineItems"))
    })
    List<RequisitionIntergration> getRequisitions(
            @Param(value = "fromStartDate") Date fromStartDate,
            @Param(value = "everyPageCount") int everyPageCount,
            @Param(value = "startPosition") int startPosition
    );
}
