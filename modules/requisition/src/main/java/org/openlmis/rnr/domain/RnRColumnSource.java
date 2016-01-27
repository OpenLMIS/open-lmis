/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.rnr.serializer.RnrColumnSourceDeSerializer;
import org.openlmis.rnr.serializer.RnrColumnSourceSerializer;

/**
 * This class represents the source for values in the columns configured in rnr template for a program. The source can be
 * user input, calculated or some reference data.
 */

@JsonSerialize(using = RnrColumnSourceSerializer.class)
@JsonDeserialize(using = RnrColumnSourceDeSerializer.class)
public enum RnRColumnSource {

  REFERENCE("R", "label.column.source.reference.data"),
  USER_INPUT("U", "label.column.source.user.input"),
  CALCULATED("C", "label.column.source.calculated");

  private final String code;

  private final String description;

  RnRColumnSource(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public static RnRColumnSource getValueOf(String value) {
    for (RnRColumnSource columnSource : RnRColumnSource.values()) {
      if (columnSource.code.equalsIgnoreCase(value)) return columnSource;
    }
    return null;
  }

  public String getDescription() {
    return description;
  }

  public String getCode() {
    return code;
  }
}
