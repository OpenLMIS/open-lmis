package org.openlmis.report.builder;

import java.util.Map;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:34 AM
 */
public class ConsumptionQueryBuilder {

    public static String getQuery(Map params){
        return "select * from facilities";
    }
}
