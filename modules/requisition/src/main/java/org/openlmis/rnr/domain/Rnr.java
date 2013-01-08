package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Rnr {

  private Integer id;
  private Integer facilityId;

  private Integer programId;
  private RnrStatus status;
  private Float fullSupplyItemsSubmittedCost = 0f;
  private Float nonFullSupplyItemsSubmittedCost = 0f;
  private Float totalSubmittedCost = 0f;

  private List<RnrLineItem> lineItems = new ArrayList<>();

  private Integer modifiedBy;
  private Date modifiedDate;

  public Rnr(Integer facilityId, Integer programId, Integer modifiedBy) {
    this.facilityId = facilityId;
    this.programId = programId;
    this.modifiedBy = modifiedBy;
  }

  public void add(RnrLineItem rnrLineItem) {
    lineItems.add(rnrLineItem);
  }

  public boolean validate(boolean formulaValidated) {
    if(!validateFullSupplyCost() || !validateTotalSubmittedCost()){
      throw new DataException("R&R has errors, please correct them before submission");
    }
    for(RnrLineItem lineItem : lineItems){
      lineItem.validate(formulaValidated);
    }
    return true;
  }

  private boolean validateTotalSubmittedCost() {
    return totalSubmittedCost == fullSupplyItemsSubmittedCost + nonFullSupplyItemsSubmittedCost;
  }

  private boolean validateFullSupplyCost() {
    Float cost = 0f;
    for(RnrLineItem lineItem : lineItems){
      cost += lineItem.getCost();
    }
    return fullSupplyItemsSubmittedCost.equals(cost);
  }
}

