package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RnrEmailMapper {
    @Select("SELECT rl.id, " +
        "       (select name from facilities f where f.id=r.facilityid) facility_name," +
        "       r.clientsubmittedtime client_submitted_time," +
        "       rl.productcode product_code," +
        "       rl.beginningbalance beginning_balance," +
        "       rl.quantitydispensed quantity_dispensed," +
        "       rl.quantityreceived quantity_received," +
        "       " +
        "       rl.totallossesandadjustments total_losses_and_adjustments," +
        "       rl.stockinhand stock_in_hand," +
        "       rl.quantityrequested quantity_requested ," +
        "       rl.quantityapproved quantity_approved " +
        "      " +
        "  FROM requisition_line_items rl" +
        "  left join requisitions r" +
        "   on rl.rnrid = r.id" +
        "   where rl.skipped<>true" +
        "   and r.id = #{r.id}")
    List<Map<String,String>> getEmailAttachmentItems(@Param("r") Rnr rnr);
}
