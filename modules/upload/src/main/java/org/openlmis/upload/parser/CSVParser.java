package org.openlmis.upload.parser;

import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CSVParser {

    private CsvBeanReader csvBeanReader;
    private Class<? extends Importable> model;
    private RecordHandler recordHandler;

    @Autowired
    private ImportFieldParser importFieldParser;

    public CSVParser(File csvFile, Class<? extends Importable> model, RecordHandler handler) throws FileNotFoundException {
        this.model = model;
        recordHandler = handler;
        this.csvBeanReader = new CsvBeanReader(new FileReader(csvFile), CsvPreference.STANDARD_PREFERENCE);
    }

    public void process() throws Exception {

        String[] headers = csvBeanReader.getHeader(true);
        trimWhiteSpaceFromHeaders(headers);
        Set<String> headersSet = new LinkedHashSet<String>(Arrays.asList(headers));

        List<CellProcessor> cellProcessors = new ImportFieldParser().parse(model, headersSet);

        CellProcessor[] processors = cellProcessors.toArray(new CellProcessor[0]);
        Importable importedModel;

        while ((importedModel = csvBeanReader.read(model, headers, processors)) !=null){
         recordHandler.execute(importedModel);
        }


    }

    private void trimWhiteSpaceFromHeaders(String[] headers) {
        int i = 0;
        for (String header : headers) {
            headers[i++] = header.trim();
        }
    }
}
