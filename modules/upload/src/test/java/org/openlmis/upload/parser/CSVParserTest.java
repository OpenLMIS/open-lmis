package org.openlmis.upload.parser;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.DummyImportable;
import org.openlmis.upload.model.DummyRecordHandler;
import org.supercsv.exception.SuperCsvException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CSVParserTest {

    private CSVParser csvParser;
    private DummyRecordHandler recordHandler;
    private ImportFieldParser importFieldParser;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        importFieldParser = new ImportFieldParser();
        csvParser = new CSVParser(importFieldParser);
        recordHandler = new DummyRecordHandler();
    }

    @Test
    public void shouldTrimSpacesFromParsedRecords() throws Exception {
        String csvInput =
                "mandatoryStringField   , mandatoryIntField\n" +
                        " Random1               , 23\n" +
                        " Random2                , 25\n";

        InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

        csvParser.process(inputStream, DummyImportable.class, recordHandler);

        List<Importable> importedObjects = recordHandler.getImportedObjects();
        assertEquals(23, ((DummyImportable) importedObjects.get(0)).getMandatoryIntField());
        assertEquals("Random1", ((DummyImportable) importedObjects.get(0)).getMandatoryStringField());
        assertEquals(25, ((DummyImportable) importedObjects.get(1)).getMandatoryIntField());
        assertEquals("Random2", ((DummyImportable) importedObjects.get(1)).getMandatoryStringField());
    }


    @Test
    public void shouldReportMissingMandatoryHeader() throws Exception {
        String csvInput =
                "mandatoryStringField,mandatoryIntField\n" +
                        "RandomString1,2533\n" +
                        ",234\n" +
                        "RandomString3,2566\n";

        InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

        expectedEx.expect(SuperCsvException.class);
        expectedEx.expectMessage("Missing Mandatory Data: 'mandatoryStringField' of record# 2");

        csvParser.process(inputStream, DummyImportable.class, recordHandler);
    }

    @Test
    public void shouldReportIncorrectDataType() throws Exception {
        String csvInput =
                "mandatoryStringField,mandatoryIntField\n" +
                        "RandomString1,2533\n" +
                        "RandomString2,abc123\n";

        InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

        expectedEx.expect(SuperCsvException.class);
        expectedEx.expectMessage("Incorrect Data type: 'mandatoryIntField' of record# 2");

        csvParser.process(inputStream, DummyImportable.class, recordHandler);
    }
}
