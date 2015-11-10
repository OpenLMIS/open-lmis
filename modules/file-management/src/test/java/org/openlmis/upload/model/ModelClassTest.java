/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.upload.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.exception.UploadException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.upload.matchers.ExceptionMatcher.uploadExceptionMatcher;

@Category(UnitTests.class)
public class ModelClassTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldNotThrowExceptionWhileValidatingHeadersWithMismatchCase() throws UploadException {
    List<String> headers = new ArrayList<>();
    headers.add("MANDAtory String Field");
    headers.add("mandatoryIntFIELD");
    ModelClass modelClass = new ModelClass(DummyImportable.class);
    modelClass.validateHeaders(headers);
  }

  @Test
  public void shouldThrowExceptionIfHeaderDoesNotHaveCorrespondingFieldInModel() {
    List<String> headers = new ArrayList<String>() {{
      add("not existing field");
      add("mandatory string field");
      add("mandatoryIntField");
    }};

    expectedEx.expect(uploadExceptionMatcher("error.upload.invalid.header", "[not existing field]"));

    ModelClass modelClass = new ModelClass(DummyImportable.class);
    modelClass.validateHeaders(headers);
  }

  @Test
  public void shouldThrowExceptionIfHeaderIsNull() {
    List<String> headers = new ArrayList<String>() {{
      add("mandatory string field");
      add(null);
      add("mandatoryIntField");
    }};

    expectedEx.expect(uploadExceptionMatcher("error.upload.header.missing", "2"));

    ModelClass modelClass = new ModelClass(DummyImportable.class);
    modelClass.validateHeaders(headers);
  }


  @Test
  public void shouldFindFieldNameGivenTheHeader() {
    List<String> headers = new ArrayList<String>() {{
      add("mandatory string field");
      add("mandatoryIntField");
    }};

    ModelClass modelClass = new ModelClass(DummyImportable.class);
    final String[] mappings = modelClass.getFieldNameMappings(headers.toArray(new String[headers.size()]));
    assertThat(mappings[0], is("mandatoryStringField"));
    assertThat(mappings[1], is("mandatoryIntField"));

  }

  @Test
  public void shouldThrowExceptionForMissingMandatoryHeaders() {
    List<String> headers = new ArrayList<String>() {{
      add("optionalStringField");
    }};

    expectedEx.expect(uploadExceptionMatcher("error.upload.missing.mandatory.columns", "[Mandatory String Field, mandatoryIntField]"));

    ModelClass modelClass = new ModelClass(DummyImportable.class);
    modelClass.validateHeaders(headers);
  }
}
