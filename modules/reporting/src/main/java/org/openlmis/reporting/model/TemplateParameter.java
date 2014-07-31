/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TemplateParameter represents an entity that keeps track of parameter name, name to be displayed on UI, default value,
 * its data type, description and id of the template to which it belongs.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TemplateParameter extends BaseModel {

  public static final String INTEGER = "java.lang.Integer";
  public static final String SHORT = "java.lang.Short";
  public static final String LONG = "java.lang.Long";
  public static final String BOOLEAN = "java.lang.Boolean";
  public static final String DATE = "java.util.Date";
  public static final String FLOAT = "java.lang.Float";
  public static final String DOUBLE = "java.lang.Double";
  public static final String BIG_DECIMAL = "java.math.BigDecimal";

  private Long templateId;

  private String name;

  private String displayName;

  private String defaultValue;

  private String dataType;

  private String description;

  public Object getParsedValueOf(String value) throws ParseException {
    Object objectValue = value;

    switch (this.getDataType()) {
      case INTEGER:
        objectValue = Integer.parseInt(value);
        break;
      case SHORT:
        objectValue = Short.parseShort(value);
        break;
      case LONG:
        objectValue = Long.parseLong(value);
        break;
      case BOOLEAN:
        objectValue = Boolean.parseBoolean(value);
        break;
      case DATE:
        SimpleDateFormat clientFormat = new SimpleDateFormat("dd/MM/yy");
        Date dateInClientFormat = clientFormat.parse(value);

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
        objectValue = dbFormat.parse(dbFormat.format(dateInClientFormat));
        break;
      case FLOAT:
        objectValue = Float.parseFloat(value);
        break;
      case DOUBLE:
        objectValue = Double.parseDouble(value);
        break;
      case BIG_DECIMAL:
        objectValue = BigDecimal.valueOf(Double.parseDouble(value));
        break;
    }
    return objectValue;
  }
}
