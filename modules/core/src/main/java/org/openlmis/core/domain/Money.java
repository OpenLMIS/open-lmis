package org.openlmis.core.domain;


import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.serializer.MoneyDeSerializer;
import org.openlmis.core.serializer.MoneySerializer;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@JsonSerialize(using = MoneySerializer.class)
@JsonDeserialize(using = MoneyDeSerializer.class)
public class Money {
  private BigDecimal value ;

  public Money(String value) {
    this.value = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
  }

  public Money(BigDecimal value) {
    this(value.toString());
  }

  public Money multiply(BigDecimal decimal) {
      return new Money(value.multiply(decimal));
  }

  public Integer compareTo(Money other) {
    return value.compareTo(other.value);
  }

  public Money add(Money other) {
    return new Money(value.add(other.value));
  }


  @Override
  public String toString() {
    return value.toString();
  }
}
