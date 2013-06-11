/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.repository;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.repository.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@Repository
public class RequisitionRepository {

  public static final String RNR_NOT_FOUND = "rnr.not.found";

  @Autowired
  private RequisitionMapper requisitionMapper;
  @Autowired
  private RnrLineItemMapper rnrLineItemMapper;
  @Autowired
  private LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;
  @Autowired
  private CommentMapper commentMapper;
  @Autowired
  private CommaSeparator commaSeparator;
  @Autowired
  private RequisitionStatusChangeMapper requisitionStatusChangeMapper;


  public void insert(Rnr requisition) {
    requisition.setStatus(INITIATED);
    requisitionMapper.insert(requisition);
    insertLineItems(requisition, requisition.getFullSupplyLineItems());
    insertLineItems(requisition, requisition.getNonFullSupplyLineItems());
  }

  private void insertLineItems(Rnr requisition, List<RnrLineItem> lineItems) {
    for (RnrLineItem lineItem : lineItems) {
      lineItem.setRnrId(requisition.getId());
      lineItem.setModifiedBy(requisition.getModifiedBy());
      rnrLineItemMapper.insert(lineItem);
    }
  }

  public void update(Rnr rnr) {
    requisitionMapper.update(rnr);
    updateFullSupplyLineItems(rnr);
    updateNonFullSupplyLineItems(rnr);
  }

  public void approve(Rnr rnr) {
    requisitionMapper.update(rnr);
    for (RnrLineItem lineItem : rnr.getFullSupplyLineItems()) {
      updateLineItem(rnr, lineItem);
    }
    for (RnrLineItem lineItem : rnr.getNonFullSupplyLineItems()) {
      updateLineItem(rnr, lineItem);
    }
  }

  private void updateNonFullSupplyLineItems(Rnr rnr) {
    for (RnrLineItem lineItem : rnr.getNonFullSupplyLineItems()) {
      RnrLineItem savedLineItem = rnrLineItemMapper.getExistingNonFullSupplyItemByRnrIdAndProductCode(rnr.getId(), lineItem.getProductCode());
      if (savedLineItem != null) {
        lineItem.setId(savedLineItem.getId());
        updateLineItem(rnr, lineItem);
        continue;
      }
      rnrLineItemMapper.insertNonFullSupply(lineItem);
    }
  }

  private void updateFullSupplyLineItems(Rnr requisition) {
    for (RnrLineItem lineItem : requisition.getFullSupplyLineItems()) {
      updateLineItem(requisition, lineItem);
      lossesAndAdjustmentsMapper.deleteByLineItemId(lineItem.getId());
      insertLossesAndAdjustmentsForLineItem(lineItem);
    }
  }

  private void updateLineItem(Rnr requisition, RnrLineItem lineItem) {
    lineItem.setModifiedBy(requisition.getModifiedBy());
    if (requisition.getStatus() == RnrStatus.IN_APPROVAL) {
      rnrLineItemMapper.updateOnApproval(lineItem);
      return;
    }
    rnrLineItemMapper.update(lineItem);
  }

  private void insertLossesAndAdjustmentsForLineItem(RnrLineItem lineItem) {
    for (LossesAndAdjustments lossAndAdjustment : lineItem.getLossesAndAdjustments()) {
      lossesAndAdjustmentsMapper.insert(lineItem, lossAndAdjustment);
    }
  }

  public Rnr getRequisitionWithLineItems(Facility facility, Program program, ProcessingPeriod period) {
    return requisitionMapper.getRequisitionWithLineItems(facility, program, period);
  }

  public Rnr getRequisitionWithoutLineItems(Long facilityId, Long programId, Long periodId) {
    return requisitionMapper.getRequisitionWithoutLineItems(facilityId, programId, periodId);
  }


  public List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes() {
    return lossesAndAdjustmentsMapper.getLossesAndAdjustmentsTypes();
  }

  public Rnr getById(Long rnrId) {
    Rnr requisition = requisitionMapper.getById(rnrId);
    if (requisition == null) throw new DataException(RNR_NOT_FOUND);
    return requisition;
  }

  public List<Rnr> getAuthorizedRequisitions(RoleAssignment roleAssignment) {
    return requisitionMapper.getAuthorizedRequisitions(roleAssignment);
  }

  public Rnr getLastRequisitionToEnterThePostSubmitFlow(Long facilityId, Long programId) {
    return requisitionMapper.getLastRequisitionToEnterThePostSubmitFlow(facilityId, programId);
  }

  public List<Rnr> getApprovedRequisitions() {
    return requisitionMapper.getApprovedRequisitions();
  }

  public List<Rnr> getPostSubmitRequisitions(Facility facility, Program program, List<ProcessingPeriod> periods) {
    return requisitionMapper.getPostSubmitRequisitions(facility, program, commaSeparator.commaSeparateIds(periods));
  }

  public Integer getCategoryCount(Rnr requisition, boolean fullSupply) {
    return rnrLineItemMapper.getCategoryCount(requisition, fullSupply);
  }

  public List<Comment> getCommentsByRnrID(Long rnrId) {
    return commentMapper.getByRnrId(rnrId);
  }

  public void insertComment(Comment comment) {
    commentMapper.insert(comment);
  }

  public void logStatusChange(Rnr requisition) {
    RequisitionStatusChange statusChange = new RequisitionStatusChange(requisition);
    requisitionStatusChangeMapper.insert(statusChange);
  }
}

