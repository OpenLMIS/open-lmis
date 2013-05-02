package org.openlmis.report.builder;

import java.util.Map;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:34 AM
 */
public class AverageConsumptionQueryBuilder {

    public static String getQuery(Map params){
        return "select p.code || ' ' || p.manufacturer as productDescription, pp.dosespermonth average " +
                "from products p " +
                "inner join program_products pp on p.id = pp.productId ";
    }
}
