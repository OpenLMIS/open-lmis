/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.service;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProcessingPeriodService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.order.service.OrderService;
import org.openlmis.restapi.domain.ReplenishmentDTO;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.rnr.service.RnrTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.openlmis.restapi.domain.ReplenishmentDTO.prepareForREST;

@Service
@NoArgsConstructor
public class RestRequisitionService {

  public static final boolean EMERGENCY = false;

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProgramService programService;

  @Autowired
  private RnrTemplateService rnrTemplateService;

  @Autowired
  private RestRequisitionCalculator restRequisitionCalculator;

  @Autowired
  private ProcessingPeriodService processingPeriodService;

  private static final Logger logger = Logger.getLogger(RestRequisitionService.class);

  @Transactional
  public Rnr submitReport(Report report, Long userId) {
    report.validate();

    Facility reportingFacility = facilityService.getOperativeFacilityByCode(report.getAgentCode());
    Program reportingProgram = programService.getValidatedProgramByCode(report.getProgramCode());

    restRequisitionCalculator.validatePeriod(reportingFacility, reportingProgram);

    Rnr rnr = requisitionService.initiate(reportingFacility, reportingProgram, userId, EMERGENCY);

    restRequisitionCalculator.validateProducts(report.getProducts(), rnr);

    markSkippedLineItems(rnr, report);

    if (reportingFacility.getVirtualFacility())
      restRequisitionCalculator.setDefaultValues(rnr);

    copyRegimens(rnr, report);

    requisitionService.save(rnr);

    rnr = requisitionService.submit(rnr);

    return requisitionService.authorize(rnr);
  }

  @Transactional
  public Rnr submitSdpReport(Report report, Long userId) {
    report.validate();

    Facility reportingFacility = facilityService.getOperativeFacilityByCode(report.getAgentCode());
    Program reportingProgram = programService.getValidatedProgramByCode(report.getProgramCode());
    ProcessingPeriod period = processingPeriodService.getById(report.getPeriodId());

    //check if the requisition has already been initiated / submitted / authorized.
    restRequisitionCalculator.validateCustomPeriod(reportingFacility, reportingProgram, period);


    //TODO if the requisition was not initiated,  please do it now.
    //if not jump to the submission route.
    Rnr rnr = requisitionService.initiate(reportingFacility, reportingProgram, userId, report.getEmergency());

    restRequisitionCalculator.validateProducts(report.getProducts(), rnr);

    markSkippedLineItems(rnr, report);

    //TODO: if the previous submission was already approved, a big no no here
    // throw an exception

    // if you have come this far, then do it, it is your day. make the submission.
    if (reportingFacility.getVirtualFacility())
      restRequisitionCalculator.setDefaultValues(rnr);

    copyRegimens(rnr, report);

    requisitionService.save(rnr);

    rnr = requisitionService.submit(rnr);

    return requisitionService.authorize(rnr);
  }


  private void copyRegimens(Rnr rnr, Report report) {
    if (report.getRegimens() != null) {
      for (RegimenLineItem regimenLineItem : report.getRegimens()) {
        RegimenLineItem correspondingRegimenLineItem = rnr.findCorrespondingRegimenLineItem(regimenLineItem);
        if (correspondingRegimenLineItem == null)
          throw new DataException("error.invalid.regimen");
        correspondingRegimenLineItem.populate(regimenLineItem);
      }
    }
  }


  @Transactional
  public void approve(Report report, Long requisitionId, Long userId) {
    Rnr requisition = report.getRequisition(requisitionId, userId);

    Rnr savedRequisition = requisitionService.getFullRequisitionById(requisition.getId());

    if (!savedRequisition.getFacility().getVirtualFacility()) {
      throw new DataException("error.approval.not.allowed");
    }

    if (savedRequisition.getNonSkippedLineItems().size() != report.getProducts().size()) {
      throw new DataException("error.number.of.line.items.mismatch");
    }

    restRequisitionCalculator.validateProducts(report.getProducts(), savedRequisition);

    requisitionService.save(requisition);
    requisitionService.approve(requisition, report.getApproverName());
  }

  public ReplenishmentDTO getReplenishmentDetails(Long id) {
    Rnr requisition = requisitionService.getFullRequisitionById(id);
    return prepareForREST(requisition, orderService.getOrder(id));
  }


  private void markSkippedLineItems(Rnr rnr, Report report) {

    ProgramRnrTemplate rnrTemplate = rnrTemplateService.fetchProgramTemplateForRequisition(rnr.getProgram().getId());

    List<RnrLineItem> savedLineItems = rnr.getFullSupplyLineItems();
    List<RnrLineItem> reportedProducts = report.getProducts();

    for (final RnrLineItem savedLineItem : savedLineItems) {
      RnrLineItem reportedLineItem = (RnrLineItem) find(reportedProducts, new Predicate() {
        @Override
        public boolean evaluate(Object product) {
          return ((RnrLineItem) product).getProductCode().equals(savedLineItem.getProductCode());
        }
      });

      copyInto(savedLineItem, reportedLineItem, rnrTemplate);
    }
  }

  private void copyInto(RnrLineItem savedLineItem, RnrLineItem reportedLineItem, ProgramRnrTemplate rnrTemplate) {
    if (reportedLineItem == null) {
      savedLineItem.setSkipped(true);
      return;
    }

    for (Column column : rnrTemplate.getColumns()) {
      if (!column.getVisible() || !rnrTemplate.columnsUserInput(column.getName()))
        continue;
      try {
        Field field = RnrLineItem.class.getDeclaredField(column.getName());
        field.setAccessible(true);

        Object reportedValue = field.get(reportedLineItem);
        Object toBeSavedValue = (reportedValue != null ? reportedValue : field.get(savedLineItem));
        field.set(savedLineItem, toBeSavedValue);
      } catch (Exception e) {
        logger.error("could not copy field: " + column.getName());
      }
    }
  }
}
