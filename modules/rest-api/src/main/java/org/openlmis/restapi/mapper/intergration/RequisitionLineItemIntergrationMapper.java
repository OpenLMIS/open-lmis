package org.openlmis.restapi.mapper.intergration;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.restapi.domain.integration.RequisitionLineItemIntergration;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionLineItemIntergrationMapper {

    @Select({"SELECT facilities.code AS facilityCode,\n" +
            "  rli.rnrid AS requisitionId,\n" +
            "  programs.code AS programCode,\n" +
            "  rli.productcode AS productCode,\n" +
            "  rli.beginningbalance AS beginningBalance,\n" +
            "  rli.quantityreceived AS quantityReceived,\n" +
            "  rli.quantitydispensed AS quantityDispensed,\n" +
            "  rli.totallossesandadjustments AS totalLossesAndAdjustments,\n" +
            "  rli.stockinhand AS inventory,\n" +
            "  rli.quantityrequested AS quantityRequested,\n" +
            "  rli.quantityapproved AS quantityApproved,\n" +
            "  rli.product AS productFullName\n" +
            "FROM requisitions\n" +
            "JOIN requisition_line_items rli ON requisitions.id = rli.rnrid\n" +
            "JOIN products ON rli.productcode = products.code\n" +
            "JOIN program_products ON products.id = program_products.productid\n" +
            "JOIN programs ON program_products.programid = programs.id\n" +
            "JOIN facilities ON requisitions.facilityid = facilities.id\n" +
            "where requisitions.id = #{requisitionId} AND rli.skipped = FALSE"})
    List<RequisitionLineItemIntergration> getRequisitionLineItems(@Param(value = "requisitionId") Integer requisitionId);
}
