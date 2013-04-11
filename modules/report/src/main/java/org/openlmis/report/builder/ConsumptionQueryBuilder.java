package org.openlmis.report.builder;

import java.util.Map;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:34 AM
 */
public class ConsumptionQueryBuilder {

    public static String getQuery(Map params){
        return "select " +
                    "sum(quantitydispensed) consumption " +
                    ", product,productcategory category " +
                    ", ft.name facilityType " +
                    ", f.name facility " +
                "from " +
                    "requisition_line_items li " +
                    "join requisitions r on r.id = li.rnrid " +
                    "join facilities f on r.facilityid = f.id " +
                    "join facility_types ft on ft.id = f.typeid " +
                "group by " +
                    "li.product, li.productcategory, f.name, ft.name " +
                "order by " +
                    "li.productCategory, li.product";
    }
}
