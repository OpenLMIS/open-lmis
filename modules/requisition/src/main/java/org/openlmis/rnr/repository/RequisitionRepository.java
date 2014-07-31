/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

import java.util.Date;
import java.util.List;

import static org.openlmis.rnr.domain.RnrStatus.*;

/**
 * Repository class for Requisition related database operations.
 */

@Repository
public class RequisitionRepository {

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

  @Autowired
  private RegimenLineItemMapper regimenLineItemMapper;

  public void insert(Rnr requisition) {
    requisition.setStatus(INITIATED);
    requisitionMapper.insert(requisition);
    insertLineItems(requisition, requisition.getFullSupplyLineItems());
    insertLineItems(requisition, requisition.getNonFullSupplyLineItems());
    insertRegimenLineItems(requisition, requisition.getRegimenLineItems());
  }

  private void insertRegimenLineItems(Rnr requisition, List<RegimenLineItem> regimenLineItems) {
    for (RegimenLineItem regimenLineItem : regimenLineItems) {
      regimenLineItem.setRnrId(requisition.getId());
      regimenLineItem.setModifiedBy(requisition.getModifiedBy());
      regimenLineItemMapper.insert(regimenLineItem);
    }
  }

  private void insertLineItems(Rnr requisition, List<RnrLineItem> lineItems) {
    for (RnrLineItem lineItem : lineItems) {
      lineItem.setRnrId(requisition.getId());
      lineItem.setModifiedBy(requisition.getModifiedBy());
      rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());
    }
  }

  public void update(Rnr rnr) {
    requisitionMapper.update(rnr);
    updateFullSupplyLineItems(rnr);
    updateNonFullSupplyLineItems(rnr);
    if (!(rnr.getStatus() == AUTHORIZED || rnr.getStatus() == IN_APPROVAL)) {
      updateRegimenLineItems(rnr);
    }
  }

  private void updateRegimenLineItems(Rnr rnr) {
    for (RegimenLineItem regimenLineItem : rnr.getRegimenLineItems()) {
      regimenLineItemMapper.update(regimenLineItem);
    }
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

  public Rnr getRegularRequisitionWithLineItems(Facility facility, Program program, ProcessingPeriod period) {
    return requisitionMapper.getRegularRequisitionWithLineItems(facility, program, period);
  }

  public List<Rnr> getInitiatedOrSubmittedEmergencyRequisitions(Long facilityId, Long programId) {
    return requisitionMapper.getInitiatedOrSubmittedEmergencyRequisitions(facilityId, programId);
  }

  public List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes() {
    return lossesAndAdjustmentsMapper.getLossesAndAdjustmentsTypes();
  }

  public Rnr getById(Long rnrId) {
    Rnr requisition = requisitionMapper.getById(rnrId);
    if (requisition == null) throw new DataException("error.rnr.not.found");
    return requisition;
  }

  public List<Rnr> getAuthorizedRequisitions(RoleAssignment roleAssignment) {
    return requisitionMapper.getAuthorizedRequisitions(roleAssignment);
  }

  public Rnr getLastRegularRequisitionToEnterThePostSubmitFlow(Long facilityId, Long programId) {
    return requisitionMapper.getLastRegularRequisitionToEnterThePostSubmitFlow(facilityId, programId);
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

  public void logStatusChange(Rnr requisition, String name) {
    RequisitionStatusChange statusChange = new RequisitionStatusChange(requisition, name);
    requisitionStatusChangeMapper.insert(statusChange);
  }

  public Date getOperationDateFor(Long rnrId, String status) {
    return requisitionStatusChangeMapper.getOperationDateFor(rnrId, status);
  }

  public Rnr getLWById(Long rnrId) {
    return requisitionMapper.getLWById(rnrId);
  }

  public List<Rnr> getApprovedRequisitionsForCriteriaAndPageNumber(String searchType, String searchVal, Integer pageNumber,
                                                                   Integer pageSize, Long userId, String rightName, String sortBy,
                                                                   String sortDirection) {
    return requisitionMapper.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, searchVal, pageNumber, pageSize,
      userId, rightName, sortBy, sortDirection);
  }

  public Integer getCountOfApprovedRequisitionsForCriteria(String searchType, String searchVal, Long userId, String rightName) {
    return requisitionMapper.getCountOfApprovedRequisitionsForCriteria(searchType, searchVal, userId, rightName);
  }

  public Long getFacilityId(Long id) {
    return requisitionMapper.getFacilityId(id);
  }

  public Rnr getLastRegularRequisition(Facility facility, Program program) {
    return requisitionMapper.getLastRegularRequisition(facility, program);
  }

  public Date getAuthorizedDateForPreviousLineItem(Rnr rnr, String productCode, Date periodStartDate) {
    return rnrLineItemMapper.getAuthorizedDateForPreviousLineItem(rnr, productCode, periodStartDate);
  }

  public List<RnrLineItem> getAuthorizedRegularUnSkippedLineItems(String productCode, Rnr rnr, Integer n, Date startDate) {
    return rnrLineItemMapper.getAuthorizedRegularUnSkippedLineItems(productCode, rnr, n, startDate);
  }

  public RnrLineItem getNonSkippedLineItem(Long rnrId, String productCode) {
    return rnrLineItemMapper.getNonSkippedLineItem(rnrId, productCode);
  }

  public Long getProgramId(Long rnrId) {
    return requisitionMapper.getProgramId(rnrId);
  }
}
