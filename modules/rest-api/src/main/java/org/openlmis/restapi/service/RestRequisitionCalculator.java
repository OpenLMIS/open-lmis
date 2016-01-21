/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright Â© 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Â 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.Â  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.Â  If not, see http://www.gnu.org/licenses. Â For additional information contact info@OpenLMIS.org.Â 
 */

package org.openlmis.restapi.service;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.pod.service.PODService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class acts as helper class exposing methods to validate requisition attributes,
 * also has methods to compute attributes like quantity received, beginning balance.
 */

@Component
public class RestRequisitionCalculator {

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private PODService podService;

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  public void validatePeriod(Facility reportingFacility, Program reportingProgram) {

    if (!reportingFacility.getVirtualFacility()) {

      RequisitionSearchCriteria searchCriteria = new RequisitionSearchCriteria();
      searchCriteria.setProgramId(reportingProgram.getId());
      searchCriteria.setFacilityId(reportingFacility.getId());

      if (requisitionService.getCurrentPeriod(searchCriteria) != null && !requisitionService.getCurrentPeriod(searchCriteria).getId().equals
          (requisitionService.getPeriodForInitiating(reportingFacility, reportingProgram).getId())) {
        throw new DataException("error.rnr.previous.not.filled");
      }
    }
  }

  public void validateCustomPeriod(Facility reportingFacility, Program reportingProgram, ProcessingPeriod period, Long userId) {

    if (period == null) {
      throw new DataException("error.rnr.period.provided.is.invalid");
    }

    RequisitionSearchCriteria searchCriteria = new RequisitionSearchCriteria();
    searchCriteria.setProgramId(reportingProgram.getId());
    searchCriteria.setFacilityId(reportingFacility.getId());

    List<ProcessingPeriod> periods = new ArrayList<ProcessingPeriod>();
    periods.add(period);

    searchCriteria.setWithoutLineItems(true);
    searchCriteria.setUserId(userId);
    List<Rnr> list = requisitionService.getRequisitionsFor(searchCriteria, periods);
    if (list != null && !list.isEmpty() && !list.get(0).preAuthorize()) {
      throw new DataException("error.rnr.already.submitted.for.this.period");
    }
  }

  public void validateProducts(List<RnrLineItem> products, Rnr savedRequisition) {
    if (products == null) {
      return;
    }

    List<String> invalidProductCodes = new ArrayList<>();
    for (final RnrLineItem product : products) {
      RnrLineItem correspondingLineItem = savedRequisition.findCorrespondingLineItem(product);
      if (correspondingLineItem == null) {
        invalidProductCodes.add(product.getProductCode());
      }
    }
    if (!invalidProductCodes.isEmpty()) {
      throw new DataException("invalid.product.codes", invalidProductCodes.toString());
    }
  }

  public Rnr setDefaultValues(Rnr requisition) {
    Integer M = processingScheduleService.findM(requisition.getPeriod());

    List<ProcessingPeriod> nPreviousPeriods = processingScheduleService.getNPreviousPeriodsInDescOrder(requisition.getPeriod(), 2);
    Date trackingDate = requisition.getPeriod().getStartDate();

    if (!nPreviousPeriods.isEmpty()) {
      trackingDate = M >= 3 ? nPreviousPeriods.get(0).getStartDate() : nPreviousPeriods.get(nPreviousPeriods.size() - 1).getStartDate();
    }

    for (RnrLineItem rnrLineItem : requisition.getNonSkippedLineItems()) {
      setBeginningBalance(rnrLineItem, requisition, trackingDate);
      setQuantityReceived(rnrLineItem, requisition, trackingDate);
    }
    return requisition;
  }

  private void setQuantityReceived(RnrLineItem rnrLineItem, Rnr requisition, Date trackingDate) {
    if (rnrLineItem.getQuantityReceived() != null)
      return;

    List<OrderPODLineItem> nOrderPodLineItems = podService.getNPreviousOrderPodLineItems(rnrLineItem.getProductCode(), requisition, 1, trackingDate);

    Integer quantityReceived = !nOrderPodLineItems.isEmpty() ? nOrderPodLineItems.get(0).getQuantityReceived() : 0;

    rnrLineItem.setQuantityReceived(quantityReceived);
  }

  private void setBeginningBalance(RnrLineItem rnrLineItem, Rnr requisition, Date trackingDate) {
    List<RnrLineItem> nRnrLineItems = requisitionService.getNRnrLineItems(rnrLineItem.getProductCode(), requisition, 1, trackingDate);

    if (!nRnrLineItems.isEmpty()) {
      if (rnrLineItem.getBeginningBalance() != null) {
        rnrLineItem.setPreviousStockInHand(nRnrLineItems.get(0).getStockInHand());
      } else {
        rnrLineItem.setBeginningBalance(nRnrLineItems.get(0).getStockInHand());
        rnrLineItem.setPreviousStockInHand(nRnrLineItems.get(0).getStockInHand());
      }
      return;
    } else {
      if (rnrLineItem.getBeginningBalance() != null)
        return;
    }

    Integer beginningBalance = rnrLineItem.getStockInHand() != null ? rnrLineItem.getStockInHand() : 0;
    rnrLineItem.setBeginningBalance(beginningBalance);
  }
}
