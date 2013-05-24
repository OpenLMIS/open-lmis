package org.openlmis.report.builder;

import java.util.Map;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:34 AM
 */
public class SummaryQueryBuilder {

    public static String getQuery(Map params){

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
                writePredicates(params)+

                " group by li.productcode, li.productcategory, li.product, li.dispensingunit" +
                " order by " + QueryHelpers.getSortOrder(params, "productcategory asc, product asc");
            return query;
    }
    private static String writePredicates(Map params){
        String predicate = "";
        String period =    ((String[])params.get("period"))[0];
        String program =   ((String[])params.get("program"))[0];

        if (period != null &&  !period.equals("undefined") && !period.isEmpty()){
            predicate = predicate.isEmpty() ? "where r.periodid = "+ period : " and r.periodid = "+ period;
        }
        if (program != null &&  !program.equals("undefined") && !program.isEmpty()) {
            predicate = predicate.isEmpty() ? "where r.programid = "+ program : " and r.programid = "+ program;
        }

        return predicate;
    }


}
