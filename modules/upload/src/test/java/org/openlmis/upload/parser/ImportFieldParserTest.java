package org.openlmis.upload.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.upload.model.DummyImportable;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ImportFieldParser.class})
public class ImportFieldParserTest {

    ImportFieldParser parser = new ImportFieldParser();

    @Test
    public void shouldReturnCorrectProcessorForHeaders() throws Exception {

        Set<String> headers = new LinkedHashSet<String>();
        headers.add("mandatoryStringField");
        headers.add("mandatoryIntField");
        headers.add("optionalStringField");

        List<CellProcessor> cellProcessors = parser.parse(DummyImportable.class, headers);

        assertEquals(3, cellProcessors.size());
        assertTrue(cellProcessors.get(0) instanceof NotNull);
        assertTrue(cellProcessors.get(1) instanceof NotNull);
        assertTrue(cellProcessors.get(2) instanceof Optional);
    }

    @Test
    public void testReturnProcessorForMismatchCaseAsWell() throws Exception {
        Set<String> headers = new LinkedHashSet<String>();
        headers.add("MANDAtoryStringField");
        headers.add("mandatoryIntFIELD");

        List<CellProcessor> cellProcessors = parser.parse(DummyImportable.class, headers);

        assertEquals(2, cellProcessors.size());
        assertTrue(cellProcessors.get(0) instanceof NotNull);
        assertTrue(cellProcessors.get(1) instanceof NotNull);
    }

    @Test
    public void shouldIgnoreNonAnnotatedHeaders() throws Exception {
        Set<String> headers = new LinkedHashSet<String>();
        headers.add("mandatoryStringField");
        headers.add("mandatoryIntField");
        headers.add("nonAnnotatedField");
        headers.add("random");

        List<CellProcessor> cellProcessors = parser.parse(DummyImportable.class, headers);

        assertEquals(4, cellProcessors.size());
        assertTrue(cellProcessors.get(0) instanceof NotNull);
        assertTrue(cellProcessors.get(1) instanceof NotNull);
        assertNull(cellProcessors.get(2));
        assertNull(cellProcessors.get(3));
    }

    @Test
    public void shouldBeAbleToPickupMandatoryFieldTypes() throws Exception {
        Set<String> headers = new LinkedHashSet<String>();
        headers.add("mandatoryStringField");
        headers.add("mandatoryIntField");

        ParseInt parseIntMock = mock(ParseInt.class);
        NotNull notNullMock = mock(NotNull.class);
        Trim trimMock = mock(Trim.class);

        whenNew(ParseInt.class).withNoArguments().thenReturn(parseIntMock);
        whenNew(NotNull.class).withNoArguments().thenReturn(notNullMock);
        whenNew(NotNull.class).withArguments(parseIntMock).thenReturn(notNullMock);
        whenNew(Trim.class).withArguments(parseIntMock).thenReturn(trimMock);

        parser = new ImportFieldParser();
        List<CellProcessor> cellProcessors = parser.parse(DummyImportable.class, headers);

        assertEquals(2, cellProcessors.size());
        verifyNew(NotNull.class).withNoArguments();
        verifyNew(NotNull.class).withArguments(trimMock);
    }

    @Test
    public void shouldBeAbleToPickupOptionalFieldTypes() throws Exception {
        Set<String> headers = new LinkedHashSet<String>();
        headers.add("mandatoryStringField");
        headers.add("mandatoryIntField");
        headers.add("optionalStringField");
        headers.add("optionalIntField");

        Optional optionalMock = mock(Optional.class);
        ParseInt parseIntMock = mock(ParseInt.class);
        Trim trimMock = mock(Trim.class);

        whenNew(ParseInt.class).withNoArguments().thenReturn(parseIntMock);
        whenNew(Optional.class).withNoArguments().thenReturn(optionalMock);
        whenNew(Optional.class).withArguments(parseIntMock).thenReturn(optionalMock);
        whenNew(Trim.class).withArguments(parseIntMock).thenReturn(trimMock);
        parser = new ImportFieldParser();
        List<CellProcessor> cellProcessors = parser.parse(DummyImportable.class, headers);

        assertEquals(4, cellProcessors.size());
        verifyNew(Optional.class).withNoArguments();
        verifyNew(Optional.class).withArguments(trimMock);

    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionIfAnyMandatoryFieldIsMissing() throws Exception {

        Set<String> headers = new LinkedHashSet<String>();
        headers.add("optionalStringField");

        parser.parse(DummyImportable.class, headers);
    }

}
