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
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.pod.domain.PODLineItem;
import org.openlmis.pod.service.PODService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.rnr.service.RnrTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class RestRequisitionCalculator {

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private RnrTemplateService rnrTemplateService;

  @Autowired
  private PODService podService;

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  public void validatePeriod(Facility reportingFacility, Program reportingProgram) {

    if (!reportingFacility.getVirtualFacility()) {

      RequisitionSearchCriteria searchCriteria = new RequisitionSearchCriteria();
      searchCriteria.setProgramId(reportingProgram.getId());
      searchCriteria.setFacilityId(reportingFacility.getId());

      if (!requisitionService.getCurrentPeriod(searchCriteria).getId().equals
        (requisitionService.getPeriodForInitiating(reportingFacility, reportingProgram).getId())) {
        throw new DataException("error.rnr.previous.not.filled");
      }
    }
  }

  public void validateProducts(List<RnrLineItem> products, Rnr savedRequisition) {
    if (products == null) {
      return;
    }

    List<String> invalidProductCodes = new ArrayList<>();
    for (final RnrLineItem product : products) {
      RnrLineItem correspondingLineItem = savedRequisition.findCorrespondingLineItem(product);
      if (correspondingLineItem == null || correspondingLineItem.getSkipped()) {
        invalidProductCodes.add(product.getProductCode());
      }
    }
    if (invalidProductCodes.size() != 0) {
      throw new DataException(messageService.message("invalid.product.codes", invalidProductCodes.toString()));
    }
  }

  public Rnr setDefaultValues(Rnr requisition) {
    Integer M = processingScheduleService.findM(requisition.getPeriod());

    List<ProcessingPeriod> nPreviousPeriods = M >= 3 ? processingScheduleService.getNPreviousPeriods(requisition.getPeriod(), 1) : processingScheduleService.getNPreviousPeriods(requisition.getPeriod(), 2);
    Date trackingDate = nPreviousPeriods.size() > 0 ? nPreviousPeriods.get(nPreviousPeriods.size() - 1).getStartDate() : requisition.getPeriod().getStartDate();

    for (RnrLineItem rnrLineItem : requisition.getFullSupplyLineItems()) {
      if (rnrLineItem.getSkipped())
        continue;

      setBeginningBalance(rnrLineItem, requisition, trackingDate);
      setQuantityReceived(rnrLineItem, requisition, trackingDate);
    }
    return requisition;
  }

  private void setQuantityReceived(RnrLineItem rnrLineItem, Rnr requisition, Date trackingDate) {
    if (rnrLineItem.getQuantityReceived() != null)
      return;
    List<PODLineItem> nPodLineItems = podService.getNPodLineItems(rnrLineItem.getProductCode(), requisition, 1, trackingDate);

    Integer quantityReceived = nPodLineItems.size() != 0 ? nPodLineItems.get(0).getQuantityReceived() : 0;

    rnrLineItem.setQuantityReceived(quantityReceived);
  }

  private void setBeginningBalance(RnrLineItem rnrLineItem, Rnr requisition, Date trackingDate) {
    if (rnrLineItem.getBeginningBalance() != null)
      return;
    List<RnrLineItem> nRnrLineItems = requisitionService.getNRnrLineItems(rnrLineItem.getProductCode(), requisition, 1, trackingDate);
    if (nRnrLineItems.size() != 0) {
      rnrLineItem.setBeginningBalance(nRnrLineItems.get(0).getStockInHand());
      return;
    }
    Integer beginningBalance = rnrLineItem.getStockInHand() != null ? rnrLineItem.getStockInHand() : 0;
    rnrLineItem.setBeginningBalance(beginningBalance);
  }
}
