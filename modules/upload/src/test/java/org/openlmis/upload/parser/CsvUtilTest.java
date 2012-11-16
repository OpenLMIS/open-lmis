package org.openlmis.upload.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.upload.exception.MissingHeaderException;
import org.openlmis.upload.model.DummyImportable;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CsvUtil.class})
public class CsvUtilTest {

    @Test
    public void shouldReturnCorrectProcessorForHeaders() throws MissingHeaderException {

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("mandatoryStringField");
        headers.add("mandatoryIntField");
        headers.add("optionalStringField");
        headers.add("optionalDateField");
        List<CellProcessor> cellProcessors = CsvUtil.getProcessors(DummyImportable.class, headers);

        assertEquals(4, cellProcessors.size());
        assertTrue(cellProcessors.get(0) instanceof NotNull);
        assertTrue(cellProcessors.get(1) instanceof NotNull);
        assertTrue(cellProcessors.get(2) instanceof Optional);
        assertTrue(cellProcessors.get(3) instanceof Optional);
    }

    @Test
    public void shouldValidateHeadersWithCaseMismatch() throws MissingHeaderException {
        List<String> headers = new ArrayList<String>();
        headers.add("MANDAtoryStringField");
        headers.add("mandatoryIntFIELD");

        CsvUtil.validateHeaders(DummyImportable.class, headers);
    }

    @Test
    public void testReturnProcessorForMismatchCase() throws MissingHeaderException {
        List<String> headers = new ArrayList<String>();
        headers.add("MANDAtoryStringField");
        headers.add("mandatoryIntFIELD");

        List<CellProcessor> cellProcessors = CsvUtil.getProcessors(DummyImportable.class, headers);

        assertEquals(2, cellProcessors.size());
        assertTrue(cellProcessors.get(0) instanceof NotNull);
        assertTrue(cellProcessors.get(1) instanceof NotNull);
    }

    @Test
    public void shouldIgnoreNonAnnotatedHeaders() throws MissingHeaderException {
        List<String> headers = new ArrayList<String>();
        headers.add("mandatoryStringField");
        headers.add("mandatoryIntField");
        headers.add("nonAnnotatedField");
        headers.add("random");

        List<CellProcessor> cellProcessors = CsvUtil.getProcessors(DummyImportable.class, headers);

        assertEquals(4, cellProcessors.size());
        assertTrue(cellProcessors.get(0) instanceof NotNull);
        assertTrue(cellProcessors.get(1) instanceof NotNull);
        assertNull(cellProcessors.get(2));
        assertNull(cellProcessors.get(3));
    }

    @Test
    public void shouldReturnProcessorsForMandatoryFields() throws Exception {
        List<String> headers = new ArrayList<String>();
        headers.add("mandatoryStringField");
        headers.add("mandatoryIntField");

        NotNull notNullForString = mock(NotNull.class);
        NotNull notNullForInt = mock(NotNull.class);
        whenNew(NotNull.class).withArguments(CsvUtil.typeMappings.get("String")).thenReturn(notNullForString);
        whenNew(NotNull.class).withArguments(CsvUtil.typeMappings.get("int")).thenReturn(notNullForInt);

        List<CellProcessor> cellProcessors = CsvUtil.getProcessors(DummyImportable.class, headers);

        assertEquals(2, cellProcessors.size());
        assertThat((NotNull) cellProcessors.get(0), is(notNullForString));
        assertThat((NotNull) cellProcessors.get(1), is(notNullForInt));
    }

    @Test
    public void shouldBeAbleToPickupOptionalFieldTypes() throws Exception {
        List<String> headers = new ArrayList<String>();
        headers.add("optionalStringField");
        headers.add("optionalIntField");
        headers.add("optionalDateField");

        Optional optionalForString = mock(Optional.class);
        Optional optionalForInt = mock(Optional.class);
        Optional optionalForDate = mock(Optional.class);
        whenNew(Optional.class).withArguments(CsvUtil.typeMappings.get("String")).thenReturn(optionalForString);
        whenNew(Optional.class).withArguments(CsvUtil.typeMappings.get("int")).thenReturn(optionalForInt);
        whenNew(Optional.class).withArguments(CsvUtil.typeMappings.get("Date")).thenReturn(optionalForDate);

        List<CellProcessor> cellProcessors = CsvUtil.getProcessors(DummyImportable.class, headers);

        assertEquals(3, cellProcessors.size());
        assertThat((Optional) cellProcessors.get(0), is(optionalForString));
        assertThat((Optional) cellProcessors.get(1), is(optionalForInt));
        assertThat((Optional) cellProcessors.get(2), is(optionalForDate));
    }

    @Test
    public void shouldThrowExceptionIfHeaderDoesNotHaveCorrespondingFieldInModel(){
        List<String> headers = new ArrayList<String>() {{
            add("optionalStringFieldsff");
            add("mandatoryStringField");
            add("mandatoryIntField");
        }};

        try {
            CsvUtil.validateHeaders(DummyImportable.class, headers);
        } catch (MissingHeaderException e) {
            assertEquals("Invalid Headers in upload file: [optionalstringfieldsff]", e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionForMissingMandatoryHeaders() {
        List<String> headers = new ArrayList<String>() {{
            add("optionalStringField");
        }};

        try {
            CsvUtil.validateHeaders(DummyImportable.class, headers);
        } catch (MissingHeaderException e) {
            assertEquals("Missing Mandatory columns in upload file: [mandatoryStringField, mandatoryIntField]", e.getMessage());
        }
    }

}
