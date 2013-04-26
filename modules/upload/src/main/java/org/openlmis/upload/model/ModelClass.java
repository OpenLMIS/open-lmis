/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.upload.model;

import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.annotation.ImportFields;
import org.openlmis.upload.exception.UploadException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelClass {
  @Getter
  private Class<? extends Importable> clazz;

  private List<Field> importFields;
  private boolean acceptExtraHeaders = false;

  public ModelClass(Class<? extends Importable> clazz) {
    this.clazz = clazz;
    importFields = fieldsWithImportFieldAnnotation();
  }

  public ModelClass(Class<? extends Importable> clazz, boolean acceptExtraHeaders) {
    this(clazz);
    this.acceptExtraHeaders = acceptExtraHeaders;
  }

  public void validateHeaders(List<String> headers) {
    validateNullHeaders(headers);
    List<String> lowerCaseHeaders = lowerCase(headers);
    if (!acceptExtraHeaders) validateInvalidHeaders(lowerCaseHeaders);
    validateMandatoryFields(lowerCaseHeaders);
  }

  public String[] getFieldNameMappings(String[] headers) {
    List<String> fieldMappings = new ArrayList<>();
    for (String header : headers) {
      Field importField = findImportFieldWithName(header);
      if (importField != null) {
        String nestedProperty = importField.getNested();
        if (nestedProperty.isEmpty()) {
          fieldMappings.add(importField.getField().getName());
        } else {
          fieldMappings.add(importField.getField().getName() + "." + nestedProperty);
        }
      } else {
        fieldMappings.add(null);
      }

    }
    return fieldMappings.toArray(new String[fieldMappings.size()]);
  }

  public Field findImportFieldWithName(final String name) {
    Object result = CollectionUtils.find(importFields, new Predicate() {
      @Override
      public boolean evaluate(Object object) {
        Field field = (Field) object;
        return field.hasName(name);
      }
    });
    return (Field) result;
  }

  private List<Field> fieldsWithImportFieldAnnotation() {
    List<java.lang.reflect.Field> fieldsList = Arrays.asList(clazz.getDeclaredFields());
    List<Field> result = new ArrayList<>();
    for (java.lang.reflect.Field field : fieldsList) {
      if (field.isAnnotationPresent(ImportField.class)) {
        result.add(new Field(field, field.getAnnotation(ImportField.class)));
      }

      if (field.isAnnotationPresent(ImportFields.class)) {
        final ImportFields importFields = field.getAnnotation(ImportFields.class);
        for (ImportField importField : importFields.importFields()) {
          result.add(new Field(field, importField));
        }
      }
    }

    return result;
  }

  private void validateNullHeaders(List<String> headers) throws UploadException {
    for (int i = 0; i < headers.size(); i++) {
      if (headers.get(i) == null) {
        throw new UploadException("Header for column " + (i + 1) + " is missing.");
      }
    }
  }

  private void validateMandatoryFields(List<String> headers) {
    List<String> missingFields = findMissingFields(headers);

    if (!missingFields.isEmpty()) {
      throw new UploadException("Missing Mandatory columns in upload file: " + missingFields);
    }
  }

  private void validateInvalidHeaders(List<String> headers) {
    List<String> fieldNames = getAllImportedFieldNames();
    List invalidHeaders = ListUtils.subtract(headers, lowerCase(fieldNames));
    if (!invalidHeaders.isEmpty()) {
      throw new UploadException("Invalid Headers in upload file: " + invalidHeaders);
    }
  }

  private List<String> findMissingFields(List<String> headers) {
    List<String> missingFields = new ArrayList<>();
    for (Field field : importFields) {
      if (field.isMandatory()) {
        String fieldName = field.getName();
        if (!headers.contains(fieldName.toLowerCase())) {
          missingFields.add(fieldName);
        }
      }
    }
    return missingFields;
  }

  private List<String> lowerCase(List<String> headers) {
    List<String> lowerCaseHeaders = new ArrayList<>();
    for (String header : headers) {
      lowerCaseHeaders.add(header.toLowerCase());
    }
    return lowerCaseHeaders;
  }

  private List<String> getAllImportedFieldNames() {
    List<String> outputCollection = new ArrayList<>();
    for (Field field : importFields) {
      outputCollection.add(field.getName());
    }
    return outputCollection;
  }
}
