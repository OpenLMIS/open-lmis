/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
