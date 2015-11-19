package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RnrMapperForSIMAM {
    @Select("SELECT r.id, " +
        "       (SELECT code FROM programs p WHERE p.id = r.programid) program_code," +
        "       (SELECT name FROM facilities f WHERE f.id=r.facilityid) facility_name," +
        "       date(r.clientsubmittedtime) date," +
        "       'a' || rl.productcode as product_code," +
        "       rl.beginningbalance beginning_balance," +
        "       rl.quantitydispensed quantity_dispensed," +
        "       rl.quantityreceived quantity_received," +
        "       rl.totallossesandadjustments total_losses_and_adjustments," +
        "       rl.stockinhand stock_in_hand," +
        "       rl.stockinhand inventory," +
        "       rl.quantityapproved quantity_approved" +
        "  FROM requisition_line_items rl" +
        "  LEFT JOIN requisitions r" +
        "   ON rl.rnrid = r.id" +
        "   WHERE rl.skipped<>true" +
        "   AND r.id = #{r.id}")
    List<Map<String,String>> getRnrItemsForSIMAMImport(@Param("r") Rnr rnr);

    @Select("SELECT r.id requisition_id," +
        "       (SELECT code FROM programs p WHERE p.id = r.programid) program_code," +
        "       date(r.clientSubmittedTime) date," +
        "       rli.name regimen_name," +
        "       rli.patientsOnTreatment total" +
        "    FROM regimen_line_items rli" +
        "    LEFT JOIN requisitions r" +
        "    ON r.id = rli.rnrId" +
        "    WHERE r.id = #{rnr.id}"
    )
    List<Map<String, String>> getRegimenItemsForSIMAMImport(@Param("rnr") Rnr rnr);
}
