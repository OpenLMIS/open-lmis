package org.openlmis.report.builder;


import org.openlmis.report.model.params.OrderRequisitionParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class OrderRequisitionBuilder {

    public static String getQuery(Map params) {

        OrderRequisitionParam filter = (OrderRequisitionParam) params.get("filterCriteria");

        Long orderId = filter.getId();

        BEGIN();
        SELECT(" productName,sum(maximumStock) maximumStock,sum(reorderlevel) reorderlevel,sum(bufferstock) bufferstock, sum(stockonhand) stockonhand,\n" +
                "sum(quantityrequested) quantityrequested,f.name facilityname,p.name periodname ");
        FROM(" vaccine_order_requisitions r");
        JOIN("vaccine_order_requisition_line_items li on r.id = li.orderId ");
        JOIN("facilities f on r.facilityId = f.id ");
        JOIN("processing_periods p on r.periodId = p.id ");
        WHERE("orderId = " + orderId + " and li.productCategory is not null");
        GROUP_BY(" f.name,p.name,productname ");
        String sql = SQL();
        return sql;

    }

}
