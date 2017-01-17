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

import com.google.common.base.Optional;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.exception.DataException;
import org.openlmis.distribution.domain.ReasonForNotVisiting;
import org.openlmis.distribution.serializer.DistributionReadingDeSerializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

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
  static final Reading EMPTY = new Reading();

  public static Reading safeRead(Reading reading) {
    return Optional.fromNullable(reading).or(EMPTY);
  }

  private String type = "reading";
  private Reading original;
  private Object value;
  private Boolean notRecorded;

  public Reading(Object value, Boolean notRecorded) {
    this(null, value, notRecorded);
  }

  public Reading(Reading original, Object value, Boolean notRecorded) {
    this.original = original;
    this.value = value;

    if (value instanceof String) {
      this.notRecorded = ((isBlank((String) value)) && (!notRecorded)) ? true : notRecorded;
    } else {
      this.notRecorded = ((null == value) && (!notRecorded)) ? true : notRecorded;
    }
  }

  public Reading(Date date, String format) {
    if (null == date) {
      notRecorded = true;
    } else {
      value = new SimpleDateFormat(format).format(date);
      notRecorded = false;
    }

    original = new Reading(value, notRecorded);
  }

  public Reading(Object obj) {
    if (null == obj) {
      notRecorded = true;
    } else {
      value = obj;
      notRecorded = false;
    }

    original = new Reading(value, notRecorded);
  }

  public String getEffectiveValue() {
    return (notRecorded == null || !notRecorded) ? (null != value ? value.toString() : null) : null;
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

  public Boolean parseBoolean() {
    String stringValue = getEffectiveValue();
    if (stringValue == null) {
      return null;
    }

    return Boolean.parseBoolean(stringValue);
  }

  public Date parseDate() {
    String stringValue = getEffectiveValue();
    if (stringValue == null) {
      return null;
    }

    try {
      return new Date(Long.parseLong(stringValue));
    } catch (NumberFormatException e) {
      try {
        String format = stringValue.contains("/") ? "dd/MM/yyyy" : "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.parse(stringValue);
      } catch (ParseException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  public ReasonForNotVisiting parseReasonForNotVisiting() {
    String stringValue = getEffectiveValue();
    if (stringValue == null) {
      return null;
    }

    return ReasonForNotVisiting.valueOf(stringValue);
  }
}
