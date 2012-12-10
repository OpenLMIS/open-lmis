package org.openlmis.upload.parser;

import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.model.ModelClass;
import org.supercsv.cellprocessor.*;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.lang.reflect.Field;
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
        typeMappings.put("Date", new ParseDate(format));
        typeMappings.put("String", new Trim());
    }

    protected static List<CellProcessor> getProcessors(ModelClass modelClass, List<String> headers) {
        List<CellProcessor> processors = new ArrayList<CellProcessor>();
        for (String header : headers) {
            Field field =  modelClass.findImportFieldWithName(header);
            CellProcessor processor = null;
            if (field != null && field.isAnnotationPresent(ImportField.class)) {
                ImportField importField = field.getAnnotation(ImportField.class);
                processor = chainTypeProcessor(importField, typeMappings.get(importField.type()));
            }
            processors.add(processor);
        }
        return processors;
    }


    private static CellProcessor chainTypeProcessor(ImportField importField, CellProcessor mappedProcessor) {
        return importField.mandatory() ? new NotNull(mappedProcessor) : new Optional(mappedProcessor);
    }



}
