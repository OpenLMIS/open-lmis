package org.openlmis.report.builder;

import java.util.Map;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 5/8/13
 * Time: 9:29 AM
 */
public class QueryHelpers {

    public static String getSortOrder(Map params, String defaultColumn){
        String sortOrder = "";

        for (Object entryObject : params.keySet())
        {
            String entry = entryObject.toString();
            if(entry.startsWith("sort-")){
                if(sortOrder == ""){
                    sortOrder = entry.substring(5) + " " + ((String[])params.get(entry))[0];
                }else{
                    sortOrder += ", " + entry.substring(5) + " " + ((String[])params.get(entry))[0];
                }
            }
        }
        return ((sortOrder == "")? defaultColumn : sortOrder);
    }
}
