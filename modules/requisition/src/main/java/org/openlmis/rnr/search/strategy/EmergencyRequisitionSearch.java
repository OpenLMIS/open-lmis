/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.search.strategy;

/**
 * This class is a strategy to search for emergency requisitions.
 */

import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionPermissionService;

import java.util.List;

public class EmergencyRequisitionSearch extends RequisitionOnlySearch {

  public EmergencyRequisitionSearch(RequisitionSearchCriteria criteria,
                                    RequisitionPermissionService requisitionPermissionService,
                                    RequisitionRepository requisitionRepository) {
    super(criteria, requisitionPermissionService, requisitionRepository);
  }

  @Override
  List<Rnr> findRequisitions() {
    return requisitionRepository.getInitiatedOrSubmittedEmergencyRequisitions(criteria.getFacilityId(), criteria.getProgramId());
  }
}
