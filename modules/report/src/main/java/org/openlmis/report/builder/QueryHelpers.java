/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class QueryHelpers {

    public static String getSortOrder(Map params, String defaultColumn){
        String sortOrder = "";

        for (Object entryObject : params.keySet())
        {
            String entry = entryObject.toString();
            if(entry.startsWith("sort-") &&  (entry.endsWith("asc") || entry.endsWith("desc"))){
                if(sortOrder == ""){
                    sortOrder = entry.substring(5) + " " + ((String[])params.get(entry))[0];
                }else{
                    sortOrder += ", " + entry.substring(5) + " " + ((String[])params.get(entry))[0];
                }
            }
        }
        return ((sortOrder == "")? defaultColumn : sortOrder);
    }


    public static String getSortOrder(Map filterCriteria, Class reportModel, String defaultSortOrder){

        Map columnMapping = new HashMap(reportModel.getFields().length);

        for(Field field : reportModel.getDeclaredFields()){
            if(field.getAnnotation(Column.class) != null){
                Column column = field.getAnnotation(Column.class);
                columnMapping.put(field.getName(),column.name());

            }else {//assumes report model column name matches database column name
                columnMapping.put(field.getName(),field.getName());
            }
        }
        StringBuilder sortOrder = new StringBuilder("");

        for(Object entryObject : filterCriteria.keySet()){

            String entry = entryObject.toString();
            if(entry.startsWith("sort-") && columnMapping.get(entry.substring(5)) != null)
                sortOrder = sortOrder.toString().equals("") ?
                            sortOrder.append(columnMapping.get(entry.substring(5)).toString()).append("  ").append(((String[])filterCriteria.get(entry))[0]) :
                             sortOrder.append(",").append(columnMapping.get(entry.substring(5)).toString()).append("  ").append(((String[])filterCriteria.get(entry))[0]) ;
        }

        return sortOrder.toString().equals("") ? defaultSortOrder : sortOrder.toString();

    }
}
