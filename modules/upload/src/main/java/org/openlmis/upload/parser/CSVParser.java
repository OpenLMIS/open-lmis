package org.openlmis.upload.parser;

import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.springframework.beans.factory.annotation.Autowired;
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
    public int process(InputStream inputStream, Class<? extends Importable> modelClass, RecordHandler recordHandler)
            throws SuperCsvException, IOException {

        CsvPreference csvPreference = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE)
                .surroundingSpacesNeedQuotes(true).build();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        CsvBeanReader csvBeanReader = new CsvBeanReader(bufferedReader, csvPreference);

        String[] headers = parseHeaders(csvBeanReader);
        Set<String> headersSet = new LinkedHashSet<String>(Arrays.asList(headers));

        List<CellProcessor> cellProcessors = importFieldParser.parse(modelClass, headersSet);

        CellProcessor[] processors = cellProcessors.toArray(new CellProcessor[1]);

        next(modelClass, recordHandler, csvBeanReader, headers, processors);
        return csvBeanReader.getRowNumber() - 1 ;
    }

    private String[] parseHeaders(CsvBeanReader csvBeanReader) throws IOException {
        String[] headers = csvBeanReader.getHeader(true);

        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].trim();
        }
        return headers;
    }

    private void next(Class<? extends Importable> modelClass, RecordHandler recordHandler,
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
