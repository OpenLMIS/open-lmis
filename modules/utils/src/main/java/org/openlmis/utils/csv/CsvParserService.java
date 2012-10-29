package org.openlmis.utils.csv;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvParserService {
    public List parse(File csvFile, CellProcessor[] csvCellProcessor, Class clazz) throws IOException {
        CsvBeanReader csvListReader = new CsvBeanReader(new FileReader(csvFile), CsvPreference.STANDARD_PREFERENCE);
        String[] header = csvListReader.getHeader(true);
        List<Object> csvData = new ArrayList<Object>();
        Object object;
        while ((object = csvListReader.read(clazz, header, csvCellProcessor))!=null) {
            csvData.add(object);
        }
        csvListReader.close();
        return csvData;
    }
}
