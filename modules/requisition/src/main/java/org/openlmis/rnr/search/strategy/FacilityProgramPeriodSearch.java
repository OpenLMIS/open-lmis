/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
public class FacilityProgramPeriodSearch implements RequisitionSearchStrategy {

  private RequisitionSearchCriteria criteria;
  private RequisitionRepository requisitionRepository;

  public FacilityProgramPeriodSearch(RequisitionSearchCriteria criteria, RequisitionRepository requisitionRepository) {
    this.criteria = criteria;
    this.requisitionRepository = requisitionRepository;
  }

  @Override
  public List<Rnr> search() {
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
