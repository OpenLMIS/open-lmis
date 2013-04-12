package org.openlmis.report.builder;

import java.util.Map;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:34 AM
 */
public class SummaryQueryBuilder {

    public static String getQuery(Map params){
        return "select " +
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
                    ", sum(1) as productReportingRate " +
                " from requisition_line_items li " +
                " group by li.productcode, li.productcategory, li.product, li.dispensingunit;";
    }
}
