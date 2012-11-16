package org.openlmis.upload.parser;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.annotation.ImportField;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;

@Component
@NoArgsConstructor
public class CSVParser {

    @Transactional
    public int process(InputStream inputStream, Class<? extends Importable> modelClass, RecordHandler recordHandler)
            throws SuperCsvException, IOException {

        CsvPreference csvPreference = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE)
                .surroundingSpacesNeedQuotes(true).build();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        CsvBeanReader csvBeanReader = new CsvBeanReader(bufferedReader, csvPreference);

        String[] headers = parseHeaders(csvBeanReader);
        Set<String> headersSet = new LinkedHashSet<String>(Arrays.asList(headers));

        CsvUtil.validateHeaders(modelClass, headersSet);
        List<CellProcessor> cellProcessors = CsvUtil.getProcessors(modelClass, headersSet);

        CellProcessor[] processors = cellProcessors.toArray(new CellProcessor[cellProcessors.size()]);
        String[] nameMappings = getNameMappings(modelClass, headers);
        parse(modelClass, recordHandler, csvBeanReader, nameMappings, processors);
        return csvBeanReader.getRowNumber() - 1 ;
    }

    private String[] getNameMappings(Class<? extends Importable> clazz, final String[] headers) {
        List<String> nameMappings = new ArrayList<String>();

        for(String header : headers){
            Field fieldWithAnnotatedName = findFieldWithAnnotatedName(header, clazz);
            if(fieldWithAnnotatedName != null){
                nameMappings.add(fieldWithAnnotatedName.getName());
            }else {
                nameMappings.add(header);
            }
        }
        return nameMappings.toArray(new String[nameMappings.size()]);
    }

    private Field findFieldWithAnnotatedName(final String annotatedName, Class<? extends Importable> clazz) {
        List<Field> fieldsList = Arrays.asList(clazz.getDeclaredFields());
        Object result = CollectionUtils.find(fieldsList, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                Field field = (Field) object;
                if (field.isAnnotationPresent(ImportField.class) &&
                        annotatedName.equalsIgnoreCase(field.getAnnotation(ImportField.class).name())) {
                    return true;
                }
                return false;
            }
        });
        return (Field)result;
    }

    private String[] parseHeaders(CsvBeanReader csvBeanReader) throws IOException {
        String[] headers = csvBeanReader.getHeader(true);
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].trim();
        }
        return headers;
    }

    private void parse(Class<? extends Importable> modelClass, RecordHandler recordHandler,
                       CsvBeanReader csvBeanReader, String[] headers, CellProcessor[] processors) throws SuperCsvException, IOException {
        Importable importedModel;
        try {
            while ((importedModel = csvBeanReader.read(modelClass, headers, processors)) != null) {
                recordHandler.execute(importedModel, csvBeanReader.getRowNumber());
            }
        } catch (SuperCsvConstraintViolationException constraintException) {
            createException("Missing Mandatory data in field :", headers, constraintException);
        } catch (SuperCsvCellProcessorException processorException) {
            createException("Incorrect Data type in field :", headers, processorException);
        }
    }

    private void createException(String error, String[] headers, SuperCsvCellProcessorException exception) throws SuperCsvException {
        CsvContext csvContext = exception.getCsvContext();
        String header = headers[csvContext.getColumnNumber() - 1];
        throw new SuperCsvException(String.format("%s '%s' of Record No. %d", error, header, csvContext.getRowNumber() - 1), csvContext);
    }

}
