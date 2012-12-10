package org.openlmis.upload.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.upload.exception.UploadException;

import java.util.ArrayList;
import java.util.List;

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
