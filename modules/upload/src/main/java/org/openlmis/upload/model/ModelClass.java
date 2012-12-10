package org.openlmis.upload.model;

import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.exception.UploadException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelClass {
  @Getter
  private Class<? extends Importable> clazz;

  private List<Field> importFields;

  public ModelClass(Class<? extends Importable> clazz) {
    this.clazz = clazz;
    importFields = fieldsWithImportFieldAnnotation();
  }

  public void validateHeaders(List<String> headers) {
    List<String> lowerCaseHeaders = lowerCase(headers);
    validateInvalidHeaders(lowerCaseHeaders);
    validateMandatoryFields(lowerCaseHeaders);

  }

  public String[] getFieldMappings(String[] headers) {
    List<String> fieldMappings = new ArrayList<>();
    for (String header : headers) {
      Field importField = findImportFieldWithName(header);
      if (importField != null) {
        String mapped = importField.getAnnotation(ImportField.class).mapped();
        if (mapped.isEmpty()) {
          fieldMappings.add(importField.getName());
        } else {
          fieldMappings.add(mapped);
        }
      }
    }
    return fieldMappings.toArray(new String[fieldMappings.size()]);
  }

  public Field findImportFieldWithName(final String name) {
    Object result = CollectionUtils.find(importFields, new Predicate() {
      @Override
      public boolean evaluate(Object object) {
        Field field = (Field) object;
        if (fieldHasName(field, name)) {
          return true;
        }
        return false;
      }
    });
    return (Field) result;
  }

  private List<Field> fieldsWithImportFieldAnnotation() {
    List<Field> fieldsList = Arrays.asList(clazz.getDeclaredFields());
    List result = (List) CollectionUtils.select(fieldsList, new Predicate() {
      @Override
      public boolean evaluate(Object object) {
        Field field = (Field) object;
        if (field.isAnnotationPresent(ImportField.class)) {
          return true;
        }
        return false;
      }
    });
    return (List<Field>) result;
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
    List<String> missingFields = new ArrayList<String>();
    for (Field field : importFields) {
      if (field.getAnnotation(ImportField.class).mandatory()) {
        String annotatedName = field.getAnnotation(ImportField.class).name();
        if (annotatedName.equals("")) annotatedName = field.getName();
        if (!headers.contains(annotatedName.toLowerCase())) {
          missingFields.add(annotatedName);
        }
      }
    }
    return missingFields;
  }

  private List<String> lowerCase(List<String> headers) {
    List<String> lowerCaseHeaders = new ArrayList<String>();
    for (String header : headers) {
      lowerCaseHeaders.add(header.toLowerCase());
    }
    return lowerCaseHeaders;
  }


  private List<String> getAllImportedFieldNames() {
    List<String> outputCollection = new ArrayList<String>();
    for (Field field : importFields) {
      String fieldName = field.getAnnotation(ImportField.class).name();
      if (fieldName.equals("")) fieldName = field.getName();
      outputCollection.add(fieldName);
    }
    return outputCollection;
  }

  private boolean fieldHasName(Field field, String name) {
    return name.equalsIgnoreCase(field.getAnnotation(ImportField.class).name()) || name.equalsIgnoreCase(field.getName());
  }
}
