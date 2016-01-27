/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.serializer.MoneyDeSerializer;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * Money represents data type for all monetary entities. Provides methods to add, multiply, compare monetary entity.
 */
@Data
@JsonSerialize(as = BigDecimal.class)
@JsonDeserialize(using = MoneyDeSerializer.class)
@EqualsAndHashCode(callSuper = false)
public class Money extends Number {

  private BigDecimal value;

  public Money(String value) {
    this.value = new BigDecimal(value).setScale(2, ROUND_HALF_UP);
  }

  public Money(BigDecimal value) {
    this(value.toString());
  }

  public Money multiply(BigDecimal decimal) {
    return new Money(value.multiply(decimal));
  }

  public boolean isNegative() {
    return value.signum() < 0;
  }

  public Money add(Money other) {
    return new Money(value.add(other.value));
  }

  public Integer compareTo(Money other) {
    return value.compareTo(other.value);
  }

  public BigDecimal toDecimal() {
    return value;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public int intValue() {
    return value.toBigInteger().intValue();
  }

  @Override
  public long longValue() {
    return value.toBigInteger().longValue();
  }

  @Override
  public float floatValue() {
    return value.floatValue();
  }

  @Override
  public double doubleValue() {
    return value.doubleValue();
  }
}
