package org.openlmis.upload.parser;


import org.junit.Before;
import org.junit.Test;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.DummyImportable;
import org.openlmis.upload.model.DummyRecordHandler;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CSVParserTest {

    private CSVParser csvParser;
    private DummyRecordHandler recordHandler;
    private ImportFieldParser importFieldParser;
    private File csvFile = new File(this.getClass().getResource("/dummyImportableWithSpacesInHeaders.csv").getFile());
    private FileInputStream inputStream;

    @Before
    public void setUp() throws Exception {

        importFieldParser = mock(ImportFieldParser.class);
        csvParser = new CSVParser(importFieldParser);
        recordHandler = new DummyRecordHandler();
        inputStream = new FileInputStream(csvFile);
    }

    @Test
    public void shouldParseFileWithTrimmedHeaders() throws Exception {
        String[] headers = {"mandatoryStringField", "mandatoryIntField"};
        Set<String> headersSet = new LinkedHashSet<String>(Arrays.asList(headers));

        ArrayList<CellProcessor> processors = new ArrayList<CellProcessor>() {{
            add(new Trim(new NotNull()));
            add(new Trim(new ParseInt()));
        }};

        when(importFieldParser.parse(DummyImportable.class, headersSet)).thenReturn(processors);

        csvParser.process(inputStream, DummyImportable.class, recordHandler);

        verify(importFieldParser).parse(DummyImportable.class, headersSet);
    }

    @Test
    public void shouldInvokeHandlerForEachRecord() throws Exception {
        String[] headers = {"mandatoryStringField", "mandatoryIntField"};
        Set<String> headersSet = new LinkedHashSet<String>(Arrays.asList(headers));

        ArrayList<CellProcessor> processors = new ArrayList<CellProcessor>() {{
            add(new Trim(new NotNull()));
            add(new Trim(new ParseInt()));
        }};

        when(importFieldParser.parse(DummyImportable.class, headersSet)).thenReturn(processors);

        csvParser.process(inputStream, DummyImportable.class, recordHandler);

        List<Importable> importedObjects = recordHandler.getImportedObjects();
        assertEquals(2, importedObjects.size());
    }
}
