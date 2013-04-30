/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.serializer.MoneyDeSerializer;
import org.openlmis.core.serializer.MoneySerializer;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Data
@JsonSerialize(using = MoneySerializer.class)
@JsonDeserialize(using = MoneyDeSerializer.class)
@EqualsAndHashCode(callSuper = false)
public class Money extends BaseModel {

  private BigDecimal value ;

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
    return value.signum()<0;
  }

  public Money add(Money other) {
    return new Money(value.add(other.value));
  }

  public Integer compareTo(Money other) {
    return value.compareTo(other.value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
