package org.openlmis.report.builder;

import java.util.Map;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:34 AM
 */
public class SummaryQueryBuilder {

    public static String getQuery(Map params){

        String period =    ((String[])params.get("period"))[0];
        String program =   ((String[])params.get("program"))[0];
        String query = "select " +
                    " li.productcode as code" +
                    ", li.product" +
                    ", li.productcategory as category" +
                    ", li.dispensingunit as unit" +
                    ", sum(li.beginningBalance) as openingBalance" +
                    ", sum(li.quantityReceived) as quantityReceived" +
                    ", sum(li.quantitydispensed) as actualDispensedQuantity" +
                    ", sum(li.quantitydispensed) as adjustedDispensedQuantity" +
                    ", sum(li.quantitydispensed) as adjustedDistributedQuantity" +
                    ", sum(li.stockInHand) as balanceOnHand " +
                    ", sum(0) as stockOutRate " +
                    ", sum(1.0) / (select count(*) from facilities) as productReportingRate " +
                " from requisition_line_items li join requisitions r on r.id =li.rnrid" +
                " where r.periodid = " + period + " and r.programid = " + program +
                " group by li.productcode, li.productcategory, li.product, li.dispensingunit" +
                " order by productcategory asc, product asc;";
            return query;
    }
}
