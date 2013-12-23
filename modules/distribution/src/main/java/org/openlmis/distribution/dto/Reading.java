/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.exception.DataException;
import org.openlmis.distribution.serializer.DistributionReadingDeSerializer;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonDeserialize(using = DistributionReadingDeSerializer.class)
@JsonSerialize(include = NON_EMPTY)
public class Reading {

  private String value;
  private Boolean notRecorded;

  public String getEffectiveValue() {
    return (notRecorded == null || !notRecorded) ? value : null;
  }

  public Integer parsePositiveInt() {
    String stringValue = getEffectiveValue();
    if (stringValue == null) {
      return null;
    }

    int intValue = Integer.parseInt(stringValue);
    if (intValue < 0) {
      throw new DataException("error.epi.use.line.item.invalid");
    }
    return intValue;
  }

  public Reading(String value, Boolean notRecorded) {
    if ((value == null || value.equals("")) && (!notRecorded)) {
      throw new DataException("error.invalid.reading.value");
    }
    this.value = value;
    this.notRecorded = notRecorded;
  }

  public Integer parseInt() {
    String stringValue = getEffectiveValue();
    if (stringValue == null) {
      return null;
    }

    return Integer.parseInt(stringValue);
  }

  public Float parseFloat() {
    String stringValue = getEffectiveValue();
    if (stringValue == null) {
      return null;
    }

    return Float.parseFloat(stringValue);
  }
}
