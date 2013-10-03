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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Data
@NoArgsConstructor
public class FacilityProgramPeriodSearch extends RequisitionSearchStrategy {

  private RequisitionSearchCriteria criteria;
  private RequisitionRepository requisitionRepository;

  public FacilityProgramPeriodSearch(RequisitionSearchCriteria criteria, RequisitionRepository requisitionRepository) {
    this.criteria = criteria;
    this.requisitionRepository = requisitionRepository;
  }

  @Override
  List<Rnr> findRequisitions() {
    Facility facility = new Facility(criteria.getFacilityId());
    Program program = new Program(criteria.getProgramId());
    ProcessingPeriod period = new ProcessingPeriod(criteria.getPeriodId());
    Rnr requisitionWithLineItems = requisitionRepository.getRequisitionWithLineItems(facility, program, period);
    if (requisitionWithLineItems != null) {
      return asList(requisitionWithLineItems);
    } else {
      return new ArrayList<>();
    }
  }
}
