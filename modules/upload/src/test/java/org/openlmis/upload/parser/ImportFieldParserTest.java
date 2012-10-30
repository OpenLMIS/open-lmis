package org.openlmis.upload.parser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.upload.model.DummyImportable;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ImportFieldParserTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldReturnCorrectProcessorForHeaders() throws Exception {
        ImportFieldParser parser = new ImportFieldParser();

        Set<String> headers = new LinkedHashSet<String>();
        headers.add("mandatoryField");
        headers.add("optionalField");


        List<CellProcessor> cellProcessors = parser.parse(DummyImportable.class, headers);

        assertEquals(2, cellProcessors.size());
        assertTrue(cellProcessors.get(0) instanceof NotNull);
        assertTrue(cellProcessors.get(1) instanceof Optional);
    }

    @Test
    public void shouldIgnoreNonAnnotatedHeaders() throws Exception {

        ImportFieldParser parser = new ImportFieldParser();

        Set<String> headers = new LinkedHashSet<String>();
        headers.add("mandatoryField");
        headers.add("nonAnnotatedField");
        headers.add("random");

        List<CellProcessor> cellProcessors = parser.parse(DummyImportable.class, headers);
        assertEquals(3, cellProcessors.size());
        assertTrue(cellProcessors.get(0) instanceof NotNull);
        assertNull(cellProcessors.get(1));
        assertNull(cellProcessors.get(2));
    }


    @Test(expected = Exception.class)
    public void shouldThrowExceptionIfAnyMandatoryFieldIsMissing() throws Exception {

        ImportFieldParser parser = new ImportFieldParser();

        Set<String> headers = new LinkedHashSet<String>();
        headers.add("optionalField");

        parser.parse(DummyImportable.class, headers);
    }
    
}
