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
import org.openlmis.upload.annotation.ImportField;

/**
 * Field corresponds to an attribute of a POJO, used in creating new POJOs from a row in csv file.
 */

@Data
@NoArgsConstructor
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
    return this.name.equalsIgnoreCase(name);
  }

}
