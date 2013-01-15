package org.openlmis.upload.model;

import lombok.Data;
import org.openlmis.upload.annotation.ImportField;

@Data
public class Field {
  java.lang.reflect.Field field;
  private boolean mandatory;
  private String name;
  private String nested;
  private String type;

  public Field(java.lang.reflect.Field field, ImportField annotation) {
    this.field = field;
    this.mandatory = annotation.mandatory();
    this.name = annotation.name().isEmpty() ? field.getName() : annotation.name();
    this.nested = annotation.nested();
    this.type = annotation.type();
  }

  public boolean hasName(String name) {
    return name.equalsIgnoreCase(this.name);
  }

}
