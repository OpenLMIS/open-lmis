import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CSVParserService {
    public List<String[]> parseCSV(File file) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(file));
        return reader.readAll();
    }
}
