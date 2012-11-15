package org.openlmis.upload.parser;

import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.exception.MissingHeaderException;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.*;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import java.lang.reflect.Field;
import java.util.*;

@Component
public class ImportFieldParser {

    private Map<String, StringCellProcessor> typeMappings = new HashMap<String, StringCellProcessor>();

    private String format = "dd/MM/yyyy";

    public ImportFieldParser() {
        typeMappings.put("int", new ParseInt());
        typeMappings.put("long", new ParseLong());
        typeMappings.put("boolean", new ParseBool());
        typeMappings.put("double", new ParseDouble());
        typeMappings.put("Date", new ParseDate(format));
        typeMappings.put("String", new Trim());
    }

    public List<CellProcessor> parse(Class<? extends Importable> clazz, Set<String> headers) throws MissingHeaderException {
        validateHeaders(clazz, lowerCase(headers));
        return getProcessors(clazz, headers);
    }

    private Set<String> lowerCase(Set<String> headers) {
        Set<String> lowerCaseHeaders = new HashSet<String>();
        for (String header : headers) {
            lowerCaseHeaders.add(header.toLowerCase());
        }
        return lowerCaseHeaders;
    }

    private List<CellProcessor> getProcessors(Class<? extends Importable> clazz, Set<String> headers) {
        List<CellProcessor> processors = new ArrayList<CellProcessor>();
        for (String header : headers) {
            Field field = getDeclaredFieldIgnoreCase(clazz, header);
            CellProcessor processor = null;
            if (field != null && field.isAnnotationPresent(ImportField.class)) {
                ImportField importField = field.getAnnotation(ImportField.class);
                processor = chainTypeProcessor(importField, typeMappings.get(importField.type()));
            }
            processors.add(processor);
        }
        return processors;
    }

    private CellProcessor chainTypeProcessor(ImportField importField, CellProcessor mappedProcessor) {
        return importField.mandatory() ? new NotNull(mappedProcessor) : new Optional(mappedProcessor);
    }

    private void validateHeaders(Class<? extends Importable> clazz, Set<String> headers) throws MissingHeaderException {
        Field[] fields = clazz.getDeclaredFields();
        List<String> missingFields = new ArrayList<String>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ImportField.class) && field.getAnnotation(ImportField.class).mandatory()) {
                if (!headers.contains(field.getName().toLowerCase())) {
                    missingFields.add(field.getName());
                }
            }
        }
        if (!missingFields.isEmpty()) {
            throw new MissingHeaderException("Missing Mandatory columns in product upload file: " + missingFields);
        }
    }

    private Field getDeclaredFieldIgnoreCase(Class<? extends Importable> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                return field;
            }
        }
        return null;
    }
}
