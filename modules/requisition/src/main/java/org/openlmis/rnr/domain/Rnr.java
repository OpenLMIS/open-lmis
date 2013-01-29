package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.ALWAYS;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class Rnr {

  private Integer id;
  private Facility facility;
  private Program program;
  private ProcessingPeriod period;
  private Integer facilityId;
  private Integer programId;
  private Integer periodId;
  private RnrStatus status;
  private Money fullSupplyItemsSubmittedCost = new Money("0");
  private Money nonFullSupplyItemsSubmittedCost = new Money("0");

  private List<RnrLineItem> lineItems = new ArrayList<>();
  @JsonSerialize(include = ALWAYS)
  private List<RnrLineItem> nonFullSupplyLineItems = new ArrayList<>();

  private Integer modifiedBy;
  private Date modifiedDate;
  private Date submittedDate;
  private Integer supervisoryNodeId;
  public static final String RNR_VALIDATION_ERROR = "rnr.validation.error";

  public Rnr(Integer facilityId, Integer programId, Integer periodId, Integer modifiedBy) {
    this.facilityId = facilityId;
    this.programId = programId;
    this.periodId = periodId;
    this.modifiedBy = modifiedBy;
  }

  public Rnr(Integer facilityId, Integer programId, Integer periodId, List<FacilityApprovedProduct> facilityApprovedProducts, Integer modifiedBy) {
    this(facilityId, programId, periodId, modifiedBy);
    fillLineItems(facilityApprovedProducts);
  }

  public void add(RnrLineItem rnrLineItem, Boolean fullSupply) {
    if (fullSupply) {
      lineItems.add(rnrLineItem);
    } else {
      nonFullSupplyLineItems.add(rnrLineItem);
    }
  }

  public boolean validate(List<RnrColumn> templateColumns) {
    for (RnrLineItem lineItem : lineItems) {
      lineItem.validate(templateColumns);
    }
    return true;
  }

  public void calculate() {
    Money totalFullSupplyCost = new Money("0");
    for (RnrLineItem lineItem : lineItems) {
      lineItem.calculate();
      Money costPerItem = lineItem.getPrice().multiply(BigDecimal.valueOf(lineItem.getPacksToShip()));
      totalFullSupplyCost = totalFullSupplyCost.add(costPerItem);

    }
    this.fullSupplyItemsSubmittedCost = totalFullSupplyCost;
  }

  public void fillLineItems(List<FacilityApprovedProduct> facilityApprovedProducts) {
    for (FacilityApprovedProduct programProduct : facilityApprovedProducts) {
      RnrLineItem requisitionLineItem = new RnrLineItem(null, programProduct, modifiedBy);
      add(requisitionLineItem, true);
    }
  }

  public void setBeginningBalanceForEachLineItem(Rnr previousRequisition) {
    if (previousRequisition == null) return;
    for (RnrLineItem previousLineItem : previousRequisition.getLineItems()) {
      for (RnrLineItem currentLineItem : this.lineItems) {
        if (currentLineItem.getProductCode().equals(previousLineItem.getProductCode())) {
          currentLineItem.setBeginningBalanceWhenPreviousStockInHandAvailable(previousLineItem.getStockInHand());
          currentLineItem.setPreviousStockInHandAvailable(Boolean.TRUE);
          break;
        }
      }
    }
  }
}

