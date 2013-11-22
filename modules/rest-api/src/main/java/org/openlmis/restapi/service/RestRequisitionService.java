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
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.order.service.OrderService;
import org.openlmis.restapi.RequisitionValidator;
import org.openlmis.restapi.domain.ReplenishmentDTO;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.domain.Column;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
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
  private RequisitionValidator requisitionValidator;

  private static final Logger logger = Logger.getLogger(RestRequisitionService.class);

  @Transactional
  public Rnr submitReport(Report report, Long userId) {
    report.validate();

    Facility reportingFacility = facilityService.getOperativeFacilityByCode(report.getAgentCode());
    Program reportingProgram = programService.getValidatedProgramByCode(report.getProgramCode());

    requisitionValidator.validatePeriod(reportingFacility, reportingProgram);

    Rnr rnr = requisitionService.initiate(reportingFacility, reportingProgram, userId, false);

    requisitionValidator.validateProducts(report.getProducts(), rnr);

    markSkippedLineItems(rnr, report);

    requisitionService.save(rnr);

    return requisitionService.submit(rnr);
  }


  @Transactional
  public void approve(Report report, Long requisitionId, Long userId) {
    Rnr requisition = report.getRequisition(requisitionId, userId);

    Rnr savedRequisition = requisitionService.getFullRequisitionById(requisition.getId());

    if (!savedRequisition.getFacility().getVirtualFacility())
      throw new DataException("error.approval.not.allowed");

    if (savedRequisition.getFullSupplyLineItems().size() != report.getProducts().size()) {
      throw new DataException("error.number.of.line.items.mismatch");
    }

    requisitionValidator.validateProducts(report.getProducts(), savedRequisition);

    requisitionService.save(requisition);
    requisitionService.approve(requisition, report.getApproverName());
  }

  public ReplenishmentDTO getReplenishmentDetails(Long id) {
    Rnr requisition = requisitionService.getFullRequisitionById(id);
    ReplenishmentDTO replenishmentDTO = prepareForREST(requisition, orderService.getOrder(id));
    return replenishmentDTO;
  }


  private void markSkippedLineItems(Rnr rnr, Report report) {

    ProgramRnrTemplate rnrTemplate = rnrTemplateService.fetchProgramTemplateForRequisition(rnr.getProgram().getId());

    List<RnrLineItem> fullSupplyLineItems = rnr.getFullSupplyLineItems();
    List<RnrLineItem> products = report.getProducts();

    for (final RnrLineItem fullSupplyLineItem : fullSupplyLineItems) {
      RnrLineItem productLineItem = (RnrLineItem) find(products, new Predicate() {
        @Override
        public boolean evaluate(Object product) {
          return ((RnrLineItem) product).getProductCode().equals(fullSupplyLineItem.getProductCode());
        }
      });

      copyInto(fullSupplyLineItem, productLineItem, rnrTemplate);
    }
  }

  private void copyInto(RnrLineItem fullSupplyLineItem, RnrLineItem productLineItem, ProgramRnrTemplate rnrTemplate) {
    if (productLineItem == null) {
      fullSupplyLineItem.setSkipped(true);
      return;
    }

    for (Column column : rnrTemplate.getColumns()) {

      if (column.getVisible() && rnrTemplate.columnsUserInput(column.getName())) {
        try {
          Field field = RnrLineItem.class.getDeclaredField(column.getName());
          field.setAccessible(true);

          Object reportedValue = field.get(productLineItem);
          Object toBeSavedValue = (reportedValue != null ? reportedValue : field.get(fullSupplyLineItem));
          field.set(fullSupplyLineItem, toBeSavedValue);

        } catch (Exception e) {
          logger.error("could not copy field: " + column.getName());
        }
      }
    }
  }
}
