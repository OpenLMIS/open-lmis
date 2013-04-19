/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.upload.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.upload.exception.UploadException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
      add("optionalStringFieldsff");
      add("mandatory string field");
      add("mandatoryIntField");
    }};

    expectedEx.expect(UploadException.class);
    expectedEx.expectMessage("Invalid Headers in upload file: [optionalstringfieldsff]");

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

    expectedEx.expect(UploadException.class);
    expectedEx.expectMessage("Missing Mandatory columns in upload file: [Mandatory String Field, mandatoryIntField]");

    ModelClass modelClass = new ModelClass(DummyImportable.class);
    modelClass.validateHeaders(headers);
  }
}
