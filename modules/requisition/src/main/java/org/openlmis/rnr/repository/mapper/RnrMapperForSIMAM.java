package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RnrMapperForSIMAM {
    @Select("SELECT r.id, '' || r.programid form_program_id," +
        "       (SELECT name FROM facilities f WHERE f.id=r.facilityid) facility_name," +
        "       date(r.clientsubmittedtime) date," +
        "       rl.productcode as product_code," +
        "       rl.beginningbalance beginning_balance," +
        "       rl.quantitydispensed quantity_dispensed," +
        "       rl.quantityreceived quantity_received," +
        "       rl.totallossesandadjustments total_losses_and_adjustments," +
        "       rl.stockinhand stock_in_hand," +
        "       rl.stockinhand inventory," +
        "       rl.quantityRequested quantity_requested," +
        "       rl.quantityapproved quantity_approved," +
        "       rl.totalServiceQuantity total_service_quantity" +
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
        "       rli.patientsOnTreatment total," +
        "       rli.regimencategory category" +
        "    FROM regimen_line_items rli" +
        "    LEFT JOIN requisitions r" +
        "    ON r.id = rli.rnrId" +
        "    WHERE r.id = #{rnr.id}" +
        "    And rli.skipped <>true" +
        "    ORDER BY rli.regimendisplayorder"
    )
    List<Map<String, String>> getRegimenItemsForSIMAMImport(@Param("rnr") Rnr rnr);

    @Select("SELECT p.code FROM programs p" +
        "   LEFT JOIN program_products pp" +
        "   ON pp.programid = p.id" +
        "   LEFT JOIN products pr" +
        "   ON pr.id = pp.productid" +
        "   WHERE pr.code = #{productCode}" +
        "   AND pp.active = TRUE" +
        "   AND (p.id = #{formProgramId} OR p.parentid = #{formProgramId})")
    List<String> getProductProgramCode(@Param("productCode") String productCode, @Param("formProgramId") int formProgramId);
}
