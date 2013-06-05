/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.*;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;
import static org.openlmis.rnr.domain.RnrStatus.IN_APPROVAL;
import static org.openlmis.rnr.domain.RnrStatus.RELEASED;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_NULL)
@EqualsAndHashCode(callSuper = false)
public class Rnr extends BaseModel{
  private Facility facility;
  private Program program;
  private ProcessingPeriod period;
  private RnrStatus status;
  private Money fullSupplyItemsSubmittedCost = new Money("0");
  private Money nonFullSupplyItemsSubmittedCost = new Money("0");

  private List<RnrLineItem> fullSupplyLineItems = new ArrayList<>();
  private List<RnrLineItem> nonFullSupplyLineItems = new ArrayList<>();

  @Transient
  @JsonIgnore
  private List<RnrLineItem> allLineItems = new ArrayList<>();

  private Facility supplyingFacility;
  private Long supervisoryNodeId;
  private Date submittedDate;
  private List<Comment> comments = new ArrayList<>();

  public Rnr(Long facilityId, Long programId, Long periodId, Long modifiedBy) {
    facility = new Facility();
    facility.setId(facilityId);
    program = new Program();
    program.setId(programId);
    period = new ProcessingPeriod();
    period.setId(periodId);
    this.modifiedBy = modifiedBy;
  }

  public Rnr(Long facilityId, Long programId, Long periodId, List<FacilityApprovedProduct> facilityApprovedProducts, Long modifiedBy) {
    this(facilityId, programId, periodId, modifiedBy);
    fillLineItems(facilityApprovedProducts);
  }

  public Rnr(Facility facility, Program program, ProcessingPeriod period) {
    this.facility = facility;
    this.program = program;
    this.period = period;
  }

  public Rnr(Long id) {
    this.id = id;
  }

  public void add(RnrLineItem rnrLineItem, Boolean fullSupply) {
    if (fullSupply) {
      fullSupplyLineItems.add(rnrLineItem);
    } else {
      nonFullSupplyLineItems.add(rnrLineItem);
    }
  }

  public void calculateAndValidate(List<RnrColumn> programRnrColumns, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    for (RnrLineItem lineItem : fullSupplyLineItems) {
      lineItem.validateMandatoryFields(programRnrColumns);
      lineItem.calculate(period, programRnrColumns, this.getStatus(), lossesAndAdjustmentsTypes);
      lineItem.validateCalculatedFields(programRnrColumns);
    }

    for (RnrLineItem lineItem : nonFullSupplyLineItems) {
      lineItem.validateNonFullSupply();
    }

    this.fullSupplyItemsSubmittedCost = calculateCost(fullSupplyLineItems);
    this.nonFullSupplyItemsSubmittedCost = calculateCost(nonFullSupplyLineItems);
  }

  private Money calculateCost(List<RnrLineItem> lineItems) {
    Money totalFullSupplyCost = new Money("0");
    for (RnrLineItem lineItem : lineItems) {
      Money costPerItem = lineItem.calculateCost();
      totalFullSupplyCost = totalFullSupplyCost.add(costPerItem);
    }
    return totalFullSupplyCost;
  }

  public void fillLineItems(List<FacilityApprovedProduct> facilityApprovedProducts) {
    for (FacilityApprovedProduct facilityApprovedProduct : facilityApprovedProducts) {
      RnrLineItem requisitionLineItem = new RnrLineItem(null, facilityApprovedProduct, modifiedBy);
      add(requisitionLineItem, true);
    }
  }

  public void setBeginningBalances(Rnr previousRequisition, boolean beginningBalanceVisible) {
    if (previousRequisition == null) {
      if (!beginningBalanceVisible) resetBeginningBalances();
      return;
    }
    for (RnrLineItem currentLineItem : this.fullSupplyLineItems) {
      RnrLineItem previousLineItem = previousRequisition.findCorrespondingLineItem(currentLineItem);
      currentLineItem.setBeginningBalanceWhenPreviousStockInHandAvailable(previousLineItem);
    }
  }

  private void resetBeginningBalances() {
    for (RnrLineItem lineItem : fullSupplyLineItems) {
      lineItem.setBeginningBalance(0);
    }
  }

  public void fillLastTwoPeriodsNormalizedConsumptions(Rnr lastPeriodsRnr, Rnr secondLastPeriodsRnr) {
    addPreviousNormalizedConsumptionFrom(lastPeriodsRnr);
    addPreviousNormalizedConsumptionFrom(secondLastPeriodsRnr);
  }

  public void prepareForApproval() {
    status = IN_APPROVAL;
    for (RnrLineItem item : fullSupplyLineItems) {
      item.setDefaultApprovedQuantity();
    }
    for (RnrLineItem item : nonFullSupplyLineItems) {
      item.setDefaultApprovedQuantity();
    }
  }

  public void copyEditableFields(Rnr rnr, List<RnrColumn> programRnrColumns) {
    this.modifiedBy = rnr.getModifiedBy();
    ArrayList<RnrLineItem> lineItems = new ArrayList<>();

    for (RnrLineItem lineItem : rnr.getFullSupplyLineItems()) {
      RnrLineItem savedLineItem = this.findCorrespondingLineItem(lineItem);
      if(savedLineItem != null)  {
        if (this.status == IN_APPROVAL || this.status == AUTHORIZED) {
          savedLineItem.copyApproverEditableFields(lineItem);
        } else {
          savedLineItem.copyUserEditableFields(lineItem, programRnrColumns);
        }
        savedLineItem.setModifiedBy(rnr.getModifiedBy());
        lineItems.add(savedLineItem);
      }
    }
    this.setFullSupplyLineItems(lineItems);
    this.nonFullSupplyLineItems = rnr.nonFullSupplyLineItems;
    for (RnrLineItem thisLineItem : this.nonFullSupplyLineItems) {
      thisLineItem.setModifiedBy(rnr.getModifiedBy());
    }
  }

  private List<RnrLineItem> getAllLineItems() {
    if (this.allLineItems.isEmpty()) {
      this.allLineItems.addAll(this.getFullSupplyLineItems());
      this.allLineItems.addAll(this.getNonFullSupplyLineItems());
    }
    return allLineItems;
  }

  public void fillBasicInformation(Facility facility, Program program, ProcessingPeriod period) {
    this.program = program.basicInformation();
    this.period = period.basicInformation();
    this.facility = facility.basicInformation();
  }

  public void fillBasicInformationForSupplyingFacility(Facility facility) {
    this.supplyingFacility = facility.basicInformation();
  }

  private void addPreviousNormalizedConsumptionFrom(Rnr rnr) {
    if (rnr == null) return;
    for (RnrLineItem currentLineItem : fullSupplyLineItems) {
      RnrLineItem previousLineItem = rnr.findCorrespondingLineItem(currentLineItem);
      currentLineItem.addPreviousNormalizedConsumptionFrom(previousLineItem);
    }
  }

  private RnrLineItem findCorrespondingLineItem(final RnrLineItem item) {
    return (RnrLineItem) find(getAllLineItems(), new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        RnrLineItem lineItem = (RnrLineItem) o;
        return lineItem.getProductCode().equalsIgnoreCase(item.getProductCode());
      }
    });
  }

  public void setFieldsAccordingToTemplate(ProgramRnrTemplate template) {
    for (RnrLineItem lineItem : fullSupplyLineItems) {
      lineItem.setLineItemFieldsAccordingToTemplate(template);
    }
  }

  public void calculateForApproval() {
    for (RnrLineItem lineItem : fullSupplyLineItems) {
      lineItem.calculatePacksToShip();
    }
    this.fullSupplyItemsSubmittedCost = calculateCost(fullSupplyLineItems);
    this.nonFullSupplyItemsSubmittedCost = calculateCost(nonFullSupplyLineItems);
  }

  public void convertToOrder(Long userId) {
    this.status = RELEASED;
    this.modifiedBy = userId;
  }

  public void fillFullSupplyCost() {
    this.fullSupplyItemsSubmittedCost = calculateCost(this.fullSupplyLineItems);
  }

  public void fillNonFullSupplyCost() {
    this.nonFullSupplyItemsSubmittedCost = calculateCost(this.nonFullSupplyLineItems);
  }

  public void validateForApproval() {
    validateLineItemsForApproval(fullSupplyLineItems);
    validateLineItemsForApproval(nonFullSupplyLineItems);
  }

  private void validateLineItemsForApproval(List<RnrLineItem> lineItems) {
    for (RnrLineItem lineItem : lineItems) {
      lineItem.validateForApproval();
    }
  }
}

