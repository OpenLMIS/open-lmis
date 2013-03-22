/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.upload.parser;

import org.openlmis.upload.model.Field;
import org.openlmis.upload.model.ModelClass;
import org.supercsv.cellprocessor.*;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvCellProcessors {

    private static final String format = "dd/MM/yyyy";
    public static Map<String, CellProcessor> typeMappings = new HashMap<String, CellProcessor>();
    static {
        typeMappings.put("int", new ParseInt());
        typeMappings.put("long", new ParseLong());
        typeMappings.put("boolean", new ParseBool());
        typeMappings.put("double", new ParseDouble());
        typeMappings.put("Date", new StrRegEx("^\\d{1,2}/\\d{1,2}/\\d{4}$", new ParseDate(format))); //second parameter for leniency
        typeMappings.put("String", new Trim());
        typeMappings.put("BigDecimal", new ParseBigDecimal());
    }

    protected static List<CellProcessor> getProcessors(ModelClass modelClass, List<String> headers) {
        List<CellProcessor> processors = new ArrayList<>();
        for (String header : headers) {
            org.openlmis.upload.model.Field field =  modelClass.findImportFieldWithName(header);
            CellProcessor processor = null;
            if (field != null) {
                processor = chainTypeProcessor(field);
            }
            processors.add(processor);
        }
        return processors;
    }


    private static CellProcessor chainTypeProcessor(Field field) {
      CellProcessor mappedProcessor = typeMappings.get(field.getType());
      return field.isMandatory() ? new NotNull(mappedProcessor) : new Optional(mappedProcessor);
    }
}
