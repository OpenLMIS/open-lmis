package org.openlmis.upload.parser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.DummyImportable;
import org.openlmis.upload.model.ModelClass;
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
@PrepareForTest({CsvCellProcessors.class})
public class CsvCellProcessorsTest {


    private ModelClass dummyImportableClass;

    @Before
    public void setUp() throws Exception {
        dummyImportableClass = new ModelClass(DummyImportable.class);
    }

    @Test
    public void shouldReturnCorrectProcessorForHeaders() throws UploadException {

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("mandatory string field");
        headers.add("mandatoryIntField");
        headers.add("optionalStringField");
        headers.add("optional date field");
        List<CellProcessor> cellProcessors = CsvCellProcessors.getProcessors(dummyImportableClass, headers);

        assertEquals(4, cellProcessors.size());
        assertTrue(cellProcessors.get(0) instanceof NotNull);
        assertTrue(cellProcessors.get(1) instanceof NotNull);
        assertTrue(cellProcessors.get(2) instanceof Optional);
        assertTrue(cellProcessors.get(3) instanceof Optional);
    }



    @Test
    public void testReturnProcessorForMismatchCase() throws UploadException {
        List<String> headers = new ArrayList<String>();
        headers.add("MANDAtory String Field");
        headers.add("mandatoryIntFIELD");

        List<CellProcessor> cellProcessors = CsvCellProcessors.getProcessors(dummyImportableClass, headers);

        assertEquals(2, cellProcessors.size());
        assertTrue(cellProcessors.get(0) instanceof NotNull);
        assertTrue(cellProcessors.get(1) instanceof NotNull);
    }

    @Test
    public void shouldIgnoreNonAnnotatedHeaders() throws UploadException {
        List<String> headers = new ArrayList<String>();
        headers.add("mandatory string field");
        headers.add("mandatoryIntField");
        headers.add("nonAnnotatedField");
        headers.add("random");

        List<CellProcessor> cellProcessors = CsvCellProcessors.getProcessors(dummyImportableClass, headers);

        assertEquals(4, cellProcessors.size());
        assertTrue(cellProcessors.get(0) instanceof NotNull);
        assertTrue(cellProcessors.get(1) instanceof NotNull);
        assertNull(cellProcessors.get(2));
        assertNull(cellProcessors.get(3));
    }

    @Test
    public void shouldReturnProcessorsForMandatoryFields() throws Exception {
        List<String> headers = new ArrayList<String>();
        headers.add("mandatory string field");
        headers.add("mandatoryIntField");

        NotNull notNullForString = mock(NotNull.class);
        NotNull notNullForInt = mock(NotNull.class);
        whenNew(NotNull.class).withArguments(CsvCellProcessors.typeMappings.get("String")).thenReturn(notNullForString);
        whenNew(NotNull.class).withArguments(CsvCellProcessors.typeMappings.get("int")).thenReturn(notNullForInt);

        List<CellProcessor> cellProcessors = CsvCellProcessors.getProcessors(dummyImportableClass, headers);

        assertEquals(2, cellProcessors.size());
        assertThat((NotNull) cellProcessors.get(0), is(notNullForString));
        assertThat((NotNull) cellProcessors.get(1), is(notNullForInt));
    }

    @Test
    public void shouldBeAbleToPickupOptionalFieldTypes() throws Exception {
        List<String> headers = new ArrayList<String>();
        headers.add("optionalStringField");
        headers.add("OPTIONAL INT FIELD");
        headers.add("optional date field");

        Optional optionalForString = mock(Optional.class);
        Optional optionalForInt = mock(Optional.class);
        Optional optionalForDate = mock(Optional.class);
        whenNew(Optional.class).withArguments(CsvCellProcessors.typeMappings.get("String")).thenReturn(optionalForString);
        whenNew(Optional.class).withArguments(CsvCellProcessors.typeMappings.get("int")).thenReturn(optionalForInt);
        whenNew(Optional.class).withArguments(CsvCellProcessors.typeMappings.get("Date")).thenReturn(optionalForDate);

        List<CellProcessor> cellProcessors = CsvCellProcessors.getProcessors(dummyImportableClass, headers);

        assertEquals(3, cellProcessors.size());
        assertThat((Optional) cellProcessors.get(0), is(optionalForString));
        assertThat((Optional) cellProcessors.get(1), is(optionalForInt));
        assertThat((Optional) cellProcessors.get(2), is(optionalForDate));
    }

}
