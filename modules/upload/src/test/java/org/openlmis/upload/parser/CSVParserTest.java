package org.openlmis.upload.parser;


import org.junit.Before;
import org.junit.Test;
import org.openlmis.upload.model.DummyImportable;
import org.openlmis.upload.model.DummyRecordHandler;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

public class CSVParserTest {

    private CSVParser csvParser;
    private DummyRecordHandler recordHandler;
    private ImportFieldParser importFieldParser;


    @Before
    public void setUp() throws Exception {

        importFieldParser = mock(ImportFieldParser.class);
        csvParser = new CSVParser(importFieldParser);
        recordHandler = new DummyRecordHandler();

    }

    @Test
    public void shouldParseFileWithTrimmedHeaders() throws Exception {
        File csvFile = new File(this.getClass().getResource("/dummyImportableWithSpacesInHeaders.csv").getFile());

        String[] headers = {"mandatoryStringField", "mandatoryIntField"};
        Set<String> headersSet = new LinkedHashSet<String>(Arrays.asList(headers));

        ArrayList<CellProcessor> processors = new ArrayList<CellProcessor>() {{
            add(new Trim(new NotNull()));
            add(new Trim(new ParseInt()));
        }};

        when(importFieldParser.parse(DummyImportable.class, headersSet)).thenReturn(processors);

        csvParser.process(csvFile, DummyImportable.class, recordHandler);

        verify(importFieldParser).parse(DummyImportable.class, headersSet);
    }
}
