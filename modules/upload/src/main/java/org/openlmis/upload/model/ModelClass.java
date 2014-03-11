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

import lombok.Data;
import lombok.NoArgsConstructor;
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

/**
 * This class represents a Java model to which the csv row is mapped.
 * This class encapsulates validation logic.
 */

@Data
@NoArgsConstructor
public class ModelClass {

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
        String missingHeaderPosition = i + 1 + "";
        throw new UploadException("error.upload.header.missing", missingHeaderPosition);
      }
    }
  }

  private void validateMandatoryFields(List<String> headers) {
    List<String> missingFields = findMissingFields(headers);

    if (!missingFields.isEmpty()) {
      throw new UploadException("error.upload.missing.mandatory.columns", missingFields.toString());
    }
  }

  private void validateInvalidHeaders(List<String> headers) {
    List<String> fieldNames = getAllImportedFieldNames();
    List invalidHeaders = ListUtils.subtract(headers, lowerCase(fieldNames));
    if (!invalidHeaders.isEmpty()) {
      throw new UploadException("error.upload.invalid.header", invalidHeaders.toString());
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
