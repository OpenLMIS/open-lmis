package org.openlmis.upload.parser;

import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.ModelClass;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Component
@NoArgsConstructor
public class CSVParser {

    @Transactional
    public int process(InputStream inputStream, ModelClass modelClass, RecordHandler recordHandler, String modifiedBy)
            throws SuperCsvException, IOException {
        CsvPreference csvPreference = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE)
                .surroundingSpacesNeedQuotes(true).build();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        CsvDozerBeanReader  csvBeanReader = new CsvDozerBeanReader(bufferedReader, csvPreference);

        String[] headers = parseHeaders(csvBeanReader);
        List<String> headersSet = Arrays.asList(headers);

        modelClass.validateHeaders(headersSet);
        List<CellProcessor> cellProcessors = CsvCellProcessors.getProcessors(modelClass, headersSet);

        CellProcessor[] processors = cellProcessors.toArray(new CellProcessor[cellProcessors.size()]);
        parse(modelClass, recordHandler, csvBeanReader, headers, processors, modifiedBy);
        return csvBeanReader.getRowNumber() - 1;
    }

    private String[] parseHeaders(CsvDozerBeanReader csvBeanReader) throws IOException {
        String[] headers = csvBeanReader.getHeader(true);
        for (int i = 0; i < headers.length; i++) {
            if(headers[i] == null) {
                throw new UploadException("Header for column " + (i+1) + " is missing.");
            }
            headers[i] = headers[i].trim();
        }
        return headers;
    }

    private void parse(ModelClass modelClass, RecordHandler recordHandler,
                       CsvDozerBeanReader csvBeanReader, String[] userFriendlyHeaders, CellProcessor[] processors, String modifiedBy) throws SuperCsvException, IOException {
        String[] fieldMappings  = modelClass.getFieldMappings(userFriendlyHeaders);
        Importable importedModel;
        try {
            csvBeanReader.configureBeanMapping(modelClass.getClazz(), fieldMappings);
            while ((importedModel = csvBeanReader.read(modelClass.getClazz(), processors)) != null) {
                recordHandler.execute(importedModel, csvBeanReader.getRowNumber(), modifiedBy);
            }
        } catch (SuperCsvConstraintViolationException constraintException) {
            createException("Missing Mandatory data in field :", userFriendlyHeaders, constraintException);
        } catch (SuperCsvCellProcessorException processorException) {
            createException("Incorrect Data type in field :", userFriendlyHeaders, processorException);
        }
    }


    private void createException(String error, String[] headers, SuperCsvCellProcessorException exception) throws SuperCsvException {
        CsvContext csvContext = exception.getCsvContext();
        String header = headers[csvContext.getColumnNumber() - 1];
        throw new UploadException(String.format("%s '%s' of Record No. %d", error, header, csvContext.getRowNumber() - 1));
    }

}
