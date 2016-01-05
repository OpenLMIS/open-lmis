package org.openlmis.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Getter
@Setter
@JsonSerialize(include = NON_EMPTY)
public class Kit extends Product {

  private final static String DEFAULT_KIT_UNIT = "1";
  private final static Integer DEFAULT_PACK_SIZE = 1;
  private final static Integer DEFAULT_DOSES_DISPENSING_UNIT = 1;
  private final static Boolean DEFAULT_FULL_SUPPLY = true;
  private final static Boolean DEFAULT_TRACER = false;
  private final static Boolean DEFAULT_ROUND_TO_ZERO = false;
  private final static Integer DEFAULT_PACK_ROUNDING_THRESHOLD = 0;

  private List<Product> products;

  public Kit() {
    setDispensingUnit(DEFAULT_KIT_UNIT);
    setPackSize(DEFAULT_PACK_SIZE);
    setDosesPerDispensingUnit(DEFAULT_DOSES_DISPENSING_UNIT);
    setFullSupply(DEFAULT_FULL_SUPPLY);
    setTracer(DEFAULT_TRACER);
    setRoundToZero(DEFAULT_ROUND_TO_ZERO);
    setPackRoundingThreshold(DEFAULT_PACK_ROUNDING_THRESHOLD);
  }

}
