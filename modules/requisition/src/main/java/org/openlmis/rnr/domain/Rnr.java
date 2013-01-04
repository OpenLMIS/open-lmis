package org.openlmis.rnr.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Rnr {

  private Integer id;
  private Integer facilityId;

  private Integer programId;
  private RnrStatus status;
  private Float fullSupplyItemsSubmittedCost;
  private Float nonFullSupplyItemsSubmittedCost;
  private Float totalSubmittedCost;

  private List<RnrLineItem> lineItems = new ArrayList<>();

  private Integer modifiedBy;
  private Date modifiedDate;

  public Rnr() {
  }

  public Rnr(Integer facilityId, Integer programId, Integer modifiedBy) {
    this.facilityId = facilityId;
    this.programId = programId;
    this.modifiedBy = modifiedBy;
  }

  public void add(RnrLineItem rnrLineItem) {
    lineItems.add(rnrLineItem);
  }

}

