package org.openlmis.upload.parser;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.DummyImportable;
import org.openlmis.upload.model.DummyRecordHandler;
import org.openlmis.upload.model.ModelClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CSVParserTest {

  private CSVParser csvParser;
  private DummyRecordHandler recordHandler;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  ModelClass dummyImportableClass;

  @Before
  public void setUp() throws Exception {
    dummyImportableClass = new ModelClass(DummyImportable.class);
    csvParser = new CSVParser();
    recordHandler = new DummyRecordHandler();
  }

  @Test
  public void shouldTrimSpacesFromParsedRecords() throws Exception {
    String csvInput =
      "mandatory string field   , mandatoryIntField\n" +
        " Random1               , 23\n" +
        " Random2                , 25\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));


    csvParser.process(inputStream, dummyImportableClass, recordHandler, "user");

    List<Importable> importedObjects = recordHandler.getImportedObjects();
    assertEquals(23, ((DummyImportable) importedObjects.get(0)).getMandatoryIntField());
    assertEquals("Random1", ((DummyImportable) importedObjects.get(0)).getMandatoryStringField());
    assertEquals(25, ((DummyImportable) importedObjects.get(1)).getMandatoryIntField());
    assertEquals("Random2", ((DummyImportable) importedObjects.get(1)).getMandatoryStringField());
  }


  @Test
  public void shouldUseHeadersFromCSVToReportMissingMandatoryData() throws Exception {
    String csvInput =
      "Mandatory String Field,mandatoryIntField\n" +
        "RandomString1,2533\n" +
        ",234\n" +
        "RandomString3,2566\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

    expectedEx.expect(UploadException.class);
    expectedEx.expectMessage("Missing Mandatory data in field : 'Mandatory String Field' of Record No. 2");

    csvParser.process(inputStream, dummyImportableClass, recordHandler, "user");
  }

  @Test
  public void shouldUseHeadersFromCSVToReportIncorrectDataTypeError() throws Exception {
    String csvInput =
      "mandatory string field, mandatoryIntField, OPTIONAL INT FIELD\n" +
        "RandomString1, 2533, \n" +
        "RandomString2, 123, random\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

    expectedEx.expect(UploadException.class);
    expectedEx.expectMessage("Incorrect Data type in field : 'OPTIONAL INT FIELD' of Record No. 2");

    csvParser.process(inputStream, dummyImportableClass, recordHandler, "user");
  }

  @Test
  public void shouldReportMissingHeaders() throws IOException {
    String csvInput =
      "mandatory string field,\n" +
        "RandomString1,2533\n" +
        "RandomString2,abc123\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

    expectedEx.expect(UploadException.class);
    expectedEx.expectMessage("Header for column 2 is missing.");

    csvParser.process(inputStream, dummyImportableClass, recordHandler, "user");
  }

  @Test
  public void shouldReportFewerOrMoreColumnData() throws IOException {
    String csvInput =
      "Mandatory String Field,mandatoryIntField,optionalStringField,OPTIONAL INT FIELD\n" +
        "a,1,,,\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

    expectedEx.expect(UploadException.class);
    expectedEx.expectMessage("Incorrect file format, Column name missing");

    csvParser.process(inputStream, dummyImportableClass, recordHandler, "user");
  }

  @Test
  public void shouldThrowErrorIfDateFormatIncorrect() throws IOException {
    String csvInput = "mandatory string field   , mandatoryIntField, OPTIONAL DATE FIELD\n" +
      " Random1               , 23, 99/99/99\n" +
      " Random2                , 25, 19/12/2012\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

    expectedEx.expect(UploadException.class);
    expectedEx.expectMessage("Incorrect date format in field : 'OPTIONAL DATE FIELD' of Record No. 1");

    csvParser.process(inputStream, dummyImportableClass, recordHandler, "user");
  }

  @Test
  public void shouldUseUserSpecifiedFieldMapping() throws IOException {
    String csvInput = "mandatory string field   , mandatoryIntField, OPTIONAL NESTED FIELD, OPTIONAL DATE FIELD\n" +
      " Random1               , 23, code1, 19/1/1990\n" +
      " Random2                , 25, code2,\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));


    csvParser.process(inputStream, dummyImportableClass, recordHandler, "user");
    DummyImportable dummyImportable = (DummyImportable) recordHandler.getImportedObjects().get(0);
    assertThat(dummyImportable.getDummyNestedField().getCode(), is("code1"));
  }

  @Test
  public void shouldSetMultipleNestedValues() throws IOException {
    String csvInput = "mandatory string field   , mandatoryIntField, entity 1 code, entity 2 code\n" +
      " Random1               , 23, code1-1, code1-2\n" +
      " Random2                , 25, code2-1, code2-2\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));


    csvParser.process(inputStream, dummyImportableClass, recordHandler, "user");
    DummyImportable dummyImportable = (DummyImportable) recordHandler.getImportedObjects().get(0);
    assertThat(dummyImportable.getMultipleNestedFields().getEntityCode1(), is("code1-1"));
    assertThat(dummyImportable.getMultipleNestedFields().getEntityCode2(), is("code1-2"));
  }
}
