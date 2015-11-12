/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.upload.parser;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.Importable;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.upload.model.DummyImportable;
import org.openlmis.upload.model.DummyRecordHandler;
import org.openlmis.upload.model.ModelClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.openlmis.upload.matchers.ExceptionMatcher.uploadExceptionMatcher;

@Category(UnitTests.class)
public class CSVParserTest {

  public static final Long MODIFIED_BY = 1L;
  private CSVParser csvParser;
  private DummyRecordHandler recordHandler;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  ModelClass dummyImportableClass;
  Date currentTimestamp;
  AuditFields auditFields;

  @Before
  public void setUp() throws Exception {
    dummyImportableClass = new ModelClass(DummyImportable.class);
    csvParser = new CSVParser();
    recordHandler = new DummyRecordHandler();
    currentTimestamp = new Date();
    auditFields = new AuditFields(MODIFIED_BY, currentTimestamp);
  }

  @Test
  public void shouldTrimSpacesFromParsedRecords() throws Exception {
    String csvInput =
      "mandatory string field   , mandatoryIntField\n" +
        " Random1               , 23\n" +
        " Random2                , 25\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));


    csvParser.process(inputStream, dummyImportableClass, recordHandler, auditFields);

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

    expectedEx.expect(uploadExceptionMatcher("missing.mandatory", "Mandatory String Field", "of Record No. ", "2"));

    csvParser.process(inputStream, dummyImportableClass, recordHandler, auditFields);
  }


  @Test
  public void shouldUseHeadersFromCSVToReportIncorrectDataTypeError() throws Exception {
    String csvInput =
      "mandatory string field, mandatoryIntField, OPTIONAL INT FIELD\n" +
        "RandomString1, 2533, \n" +
        "RandomString2, 123, random\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

    expectedEx.expect(uploadExceptionMatcher("incorrect.data.type", "OPTIONAL INT FIELD", "of Record No. ", "2"));

    csvParser.process(inputStream, dummyImportableClass, recordHandler, auditFields);
  }

  @Test
  public void shouldReportMissingHeaders() throws IOException {
    String csvInput =
      "mandatory string field,\n" +
        "RandomString1,2533\n" +
        "RandomString2,abc123\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

    expectedEx.expect(uploadExceptionMatcher("error.upload.header.missing", "2"));

    csvParser.process(inputStream, dummyImportableClass, recordHandler, auditFields);
  }

  @Test
  public void shouldReportFewerOrMoreColumnData() throws IOException {
    String csvInput =
      "Mandatory String Field,mandatoryIntField,optionalStringField,OPTIONAL INT FIELD\n" +
        "a,1,,,\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

    expectedEx.expect(UploadException.class);
    expectedEx.expectMessage("incorrect.file.format");

    csvParser.process(inputStream, dummyImportableClass, recordHandler, auditFields);
  }

  @Test
  public void shouldThrowErrorIfDateFormatIncorrect() throws IOException {
    String csvInput = "mandatory string field   , mandatoryIntField, OPTIONAL DATE FIELD\n" +
      " Random1               , 23, 99/99/99\n" +
      " Random2                , 25, 19/12/2012\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));

    expectedEx.expect(uploadExceptionMatcher("incorrect.date.format", "OPTIONAL DATE FIELD", "of Record No. ", "1"));

    csvParser.process(inputStream, dummyImportableClass, recordHandler, auditFields);
  }

  @Test
  public void shouldUseUserSpecifiedFieldMapping() throws IOException {
    String csvInput = "mandatory string field   , mandatoryIntField, OPTIONAL NESTED FIELD, OPTIONAL DATE FIELD\n" +
      " Random1               , 23, code1, 19/1/1990\n" +
      " Random2                , 25, code2,\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));


    csvParser.process(inputStream, dummyImportableClass, recordHandler, auditFields);
    DummyImportable dummyImportable = (DummyImportable) recordHandler.getImportedObjects().get(0);
    assertThat(dummyImportable.getDummyNestedField().getCode(), is("code1"));
  }

  @Test
  public void shouldSetMultipleNestedValues() throws IOException {
    String csvInput = "mandatory string field   , mandatoryIntField, entity 1 code, entity 2 code\n" +
      " Random1               , 23, code1-1, code1-2\n" +
      " Random2                , 25, code2-1, code2-2\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));


    csvParser.process(inputStream, dummyImportableClass, recordHandler, auditFields);
    DummyImportable dummyImportable = (DummyImportable) recordHandler.getImportedObjects().get(0);
    assertThat(dummyImportable.getMultipleNestedFields().getEntityCode1(), is("code1-1"));
    assertThat(dummyImportable.getMultipleNestedFields().getEntityCode2(), is("code1-2"));
  }

  @Test
  public void shouldPostProcessRecordsAfterSuccessfulUpload() throws UnsupportedEncodingException {
    String csvInput = "mandatory string field   , mandatoryIntField, entity 1 code, entity 2 code\n" +
      " Random1               , 23, code1-1, code1-2\n" +
      " Random2                , 25, code2-1, code2-2\n";

    InputStream inputStream = new ByteArrayInputStream(csvInput.getBytes("UTF-8"));


    DummyRecordHandler spyRecordHandler = spy(recordHandler);
    csvParser.process(inputStream, dummyImportableClass, spyRecordHandler, auditFields);
    DummyImportable dummyImportable = (DummyImportable) spyRecordHandler.getImportedObjects().get(0);
    assertThat(dummyImportable.getMultipleNestedFields().getEntityCode1(), is("code1-1"));
    assertThat(dummyImportable.getMultipleNestedFields().getEntityCode2(), is("code1-2"));
    verify(spyRecordHandler).postProcess(auditFields);
  }

}
