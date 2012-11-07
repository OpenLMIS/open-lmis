package org.openlmis.upload.parser;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.DummyImportable;
import org.openlmis.upload.model.DummyRecordHandler;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CSVParserTest {

    private CSVParser csvParser;
    private DummyRecordHandler recordHandler;
    private ImportFieldParser importFieldParser;
    private File csvFile = new File(this.getClass().getResource("/dummyImportableWithSpacesInHeaders.csv").getFile());
    private FileInputStream inputStream;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        importFieldParser = mock(ImportFieldParser.class);
        csvParser = new CSVParser(importFieldParser);
        recordHandler = new DummyRecordHandler();
        inputStream = new FileInputStream(csvFile);
    }

    @Test
    public void shouldParseFileWithTrimmedHeaders() throws UploadException {
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
    public void shouldInvokeHandlerForEachRecord() throws UploadException {
        String[] headers = {"mandatoryStringField", "mandatoryIntField"};
        Set<String> headersSet = new LinkedHashSet<String>(Arrays.asList(headers));

        ArrayList<CellProcessor> processors = new ArrayList<CellProcessor>() {{
            add(new NotNull());
            add(new ParseInt());
        }};

        when(importFieldParser.parse(DummyImportable.class, headersSet)).thenReturn(processors);

        csvParser.process(inputStream, DummyImportable.class, recordHandler);

        List<Importable> importedObjects = recordHandler.getImportedObjects();
        assertEquals(2, importedObjects.size());
    }

    @Test
    public void shouldReportMissingMandatoryHeader() throws UploadException, FileNotFoundException {
        File csvFile = new File(this.getClass().getResource("/dummyImportableWithMandatoryFieldMissing.csv").getFile());
        FileInputStream inputStream = new FileInputStream(csvFile);

        expectedEx.expect(UploadException.class);
        expectedEx.expectMessage("Missing Mandatory Data: 'mandatoryStringField' of record number 3");

        CSVParser csvParser = new CSVParser(new ImportFieldParser());
        csvParser.process(inputStream, DummyImportable.class, recordHandler);
    }
}
