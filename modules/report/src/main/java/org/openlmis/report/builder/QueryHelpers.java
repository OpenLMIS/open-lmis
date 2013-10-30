/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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
