package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderBatch {
  private Integer id;
  private Date createTimeStamp;
  private Integer createdByUserId;
  private Facility supplyingFacility;

  public OrderBatch(Facility supplyingFacility, Integer orderedBy) {
    this.supplyingFacility = supplyingFacility;
    this.createdByUserId = orderedBy;
  }
}
