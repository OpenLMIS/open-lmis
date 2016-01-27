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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.exception.DataException;
import org.openlmis.distribution.serializer.DistributionReadingDeSerializer;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  The reading class represents a composite entity containing value and notRecorded fields.
 *  Reading objects will either hold a not null value and notRecorded as false or
 *  null value and notRecorded as true.
 */

@Data
@NoArgsConstructor
@JsonDeserialize(using = DistributionReadingDeSerializer.class)
@JsonSerialize(include = NON_EMPTY)
public class Reading {

  private String value;
  private Boolean notRecorded;

  public Reading(String value, Boolean notRecorded) {
    this.value = value;
    this.notRecorded = ((isBlank(value)) && (!notRecorded)) ? true : notRecorded;
  }

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
