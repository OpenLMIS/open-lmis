package org.openlmis.upload.parser;

import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ImportFieldParser {

    public List<CellProcessor> parse(Class<? extends Importable> clazz, Set<String> headers) throws Exception {
        validateMandatory(clazz, headers);
        return getProcessors(clazz, headers);
    }

    private List<CellProcessor> getProcessors(Class<? extends Importable> clazz, Set<String> headers) throws NoSuchFieldException {
        List<CellProcessor> processors = new ArrayList<CellProcessor>();
        for (String header : headers) {
            Field field = getDeclaredFieldIgnoreCase(clazz, header);
            if (field != null && field.isAnnotationPresent(ImportField.class)) {
                ImportField importField = field.getAnnotation(ImportField.class);
                if (importField.mandatory()) {
                    processors.add(new NotNull());
                } else {
                    processors.add(new Optional());
                }
            } else {
                processors.add(null);
            }
        }

        return processors;
    }

    private void validateMandatory(Class<? extends Importable> clazz, Set<String> headers) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ImportField.class) && field.getAnnotation(ImportField.class).mandatory()) {
                if (!headers.contains(field.getName())) {
                    throw new Exception("Mandatory Field " + field.getName() + " not present");
                }
            }
        }
    }

    private Field getDeclaredFieldIgnoreCase(Class<? extends Importable> clazz, String fieldName) throws NoSuchFieldException {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }
}
