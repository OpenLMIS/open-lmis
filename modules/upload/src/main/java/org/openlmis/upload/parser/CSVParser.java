package org.openlmis.upload.parser;

import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.exception.UploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@NoArgsConstructor
public class CSVParser {

    private ImportFieldParser importFieldParser;

    @Autowired
    public CSVParser(ImportFieldParser importFieldParser) {
        this.importFieldParser = importFieldParser;
    }

    @Transactional
    public void process(InputStream inputStream, Class<? extends Importable> modelClass, RecordHandler recordHandler)
            throws UploadException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        CsvBeanReader csvBeanReader = new CsvBeanReader(bufferedReader, CsvPreference.STANDARD_PREFERENCE);

        String[] headers = parseHeaders(csvBeanReader);
        Set<String> headersSet = new LinkedHashSet<String>(Arrays.asList(headers));

        List<CellProcessor> cellProcessors = importFieldParser.parse(modelClass, headersSet);

        CellProcessor[] processors = cellProcessors.toArray(new CellProcessor[1]);

        next(modelClass, recordHandler, csvBeanReader, headers, processors);
    }

    private String[] parseHeaders(CsvBeanReader csvBeanReader) throws UploadException {
        String[] headers;
        try {
            headers = csvBeanReader.getHeader(true);
        } catch (IOException e) {
            throw new UploadException(e.getMessage());
        }
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].trim();
        }
        return headers;
    }

    private void next(Class<? extends Importable> modelClass, RecordHandler recordHandler,
                      CsvBeanReader csvBeanReader, String[] headers, CellProcessor[] processors) throws UploadException {
        Importable importedModel;
        try {
            while ((importedModel = csvBeanReader.read(modelClass, headers, processors)) != null) {
                recordHandler.execute(importedModel);
            }
        } catch (SuperCsvConstraintViolationException constraintException) {
            createException(headers, constraintException, "Missing Mandatory Data: ");
        } catch (SuperCsvCellProcessorException processorException) {
            createException(headers, processorException, "Incorrect Data type: ");
        } catch (IOException ioException) {
            throw new UploadException(ioException.getMessage());
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new UploadException(String.format("Duplicate Product Code at record# %d", csvBeanReader.getRowNumber() - 1));
        } catch (Exception e) {
            throw new UploadException(e.getMessage());
        }
    }

    private void createException(String[] headers, SuperCsvCellProcessorException exception, String error) throws UploadException {
        CsvContext csvContext = exception.getCsvContext();
        String header = headers[csvContext.getColumnNumber() - 1];
        throw new UploadException(String.format("%s '%s' of record number %d", error, header, csvContext.getRowNumber() - 1));
    }

}
