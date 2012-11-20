package org.openlmis.upload.parser;

import org.apache.commons.collections.ListUtils;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.exception.UploadException;
import org.supercsv.cellprocessor.*;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.lang.reflect.Field;
import java.util.*;

public class CsvUtil {

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

    protected static List<CellProcessor> getProcessors(Class<? extends Importable> clazz, List<String> headers) {
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

    protected static void validateHeaders(Class<? extends Importable> clazz, List<String> headers) throws UploadException {
        Field[] fields = clazz.getDeclaredFields();
        List<String> lowerCaseHeaders = lowerCase(headers);
        validateInvalidHeaders(lowerCaseHeaders, fields);
        validateMandatoryFields(lowerCaseHeaders, fields);
    }

    private static CellProcessor chainTypeProcessor(ImportField importField, CellProcessor mappedProcessor) {
        return importField.mandatory() ? new NotNull(mappedProcessor) : new Optional(mappedProcessor);
    }

    private static List<String> lowerCase(List<String> headers) {
        List<String> lowerCaseHeaders = new ArrayList<String>();
        for (String header : headers) {
            lowerCaseHeaders.add(header.toLowerCase());
        }
        return lowerCaseHeaders;
    }

    private static void validateMandatoryFields(List<String> headers, Field[] fields) {
        List<String> missingFields = findMissingFields(headers, fields);

        if (!missingFields.isEmpty()) {
            throw new UploadException("Missing Mandatory columns in upload file: " + missingFields);
        }
    }

    private static void validateInvalidHeaders(List<String> headers, Field[] fields) {
        List<String> fieldNames = getAllFieldNames(fields);
        List invalidHeaders = ListUtils.subtract(headers, lowerCase(fieldNames));
        if (!invalidHeaders.isEmpty()) {
            throw new UploadException("Invalid Headers in upload file: " + invalidHeaders);
        }
    }

    private static List<String> getAllFieldNames(Field[] fields) {
        List<String> outputCollection = new ArrayList<String>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ImportField.class)) {
                String fieldName = field.getAnnotation(ImportField.class).name();
                if (fieldName.equals("")) fieldName = field.getName();
                outputCollection.add(fieldName);
            }
        }
        return outputCollection;
    }

    private static List<String> findMissingFields(List<String> headers, Field[] fields) {
        List<String> missingFields = new ArrayList<String>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ImportField.class) && field.getAnnotation(ImportField.class).mandatory()) {
                String annotatedName = field.getAnnotation(ImportField.class).name();
                if (annotatedName.equals("")) annotatedName = field.getName();
                if (!headers.contains(annotatedName.toLowerCase())) {
                    missingFields.add(annotatedName);
                }
            }
        }
        return missingFields;
    }

    private static Field getDeclaredFieldIgnoreCase(Class<? extends Importable> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {

            if (field.getName().equalsIgnoreCase(fieldName)) {
                return field;
            }

            if (field.isAnnotationPresent(ImportField.class)) {
                String annotatedName = field.getAnnotation(ImportField.class).name();
                if (annotatedName.equalsIgnoreCase(fieldName)) {
                    return field;
                }
            }
        }
        return null;
    }
}
