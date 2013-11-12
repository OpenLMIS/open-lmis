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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.UserService;
import org.openlmis.order.service.OrderService;
import org.openlmis.restapi.domain.ReplenishmentDTO;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.openlmis.restapi.domain.ReplenishmentDTO.prepareForREST;

@Service
@NoArgsConstructor
public class RestRequisitionService {

  @Autowired
  private UserService userService;

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private OrderService orderService;

  @Transactional
  public Rnr submitReport(Report report) {
    report.validate();

    User user = getValidatedUser(report);

    Rnr requisition = requisitionService.initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), user.getId(), report.getEmergency());

    Rnr reportedRequisition = createReportedRequisition(report, requisition);

    requisitionService.save(reportedRequisition);

    requisitionService.submit(reportedRequisition);

    requisitionService.authorize(requisition);

    return requisition;
  }

  @Transactional
  public Rnr approve(Report report) {
    User user = getValidatedUser(report);
    Rnr requisition = report.getRequisition();
    requisition.setModifiedBy(user.getId());

    Long facilityId = requisitionService.getFacilityId(requisition.getId());
    if(facilityId == null){
      throw new DataException("error.invalid.requisition.id");
    }
    Facility facility = facilityService.getById(facilityId);
    if (!facility.getVirtualFacility())
      throw new DataException("error.approval.not.allowed");

    requisitionService.save(requisition);
    requisitionService.approve(requisition);
    return requisition;
  }

  private Rnr createReportedRequisition(Report report, Rnr requisition) {
    Rnr reportedRequisition = new Rnr(requisition.getId());
    reportedRequisition.setModifiedBy(requisition.getModifiedBy());
    reportedRequisition.setFullSupplyLineItems(report.getProducts());
    reportedRequisition.setStatus(requisition.getStatus());
    return reportedRequisition;
  }

  private User getValidatedUser(Report report) {
    User user = userService.getByUserName(report.getUserName());
    if (user == null) {
      throw new DataException("user.username.incorrect");
    }
    return user;
  }

  public ReplenishmentDTO getReplenishmentDetails(Long id) {
    Rnr requisition = requisitionService.getFullRequisitionById(id);
    ReplenishmentDTO replenishmentDTO = prepareForREST(requisition, orderService.getOrder(id));
    return replenishmentDTO;
  }
}
