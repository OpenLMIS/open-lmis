/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.rnr.service;

import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.rnr.calculation.RnrCalculationStrategy;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CalculationService {

  public static final int MILLI_SECONDS_IN_ONE_DAY = 24 * 60 * 60 * 1000;

  @Autowired
  RequisitionRepository requisitionRepository;

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  public void perform(Rnr requisition, ProgramRnrTemplate template) {
    RnrCalculationStrategy calcStrategy = requisition.getRnrCalcStrategy();

    requisition.setFullSupplyItemsSubmittedCost(new Money("0"));
    requisition.setNonFullSupplyItemsSubmittedCost(new Money("0"));

    calculateForFullSupply(requisition, calcStrategy, template);
    calculateForNonFullSupply(requisition, calcStrategy);
  }


  private void calculateForNonFullSupply(Rnr requisition, RnrCalculationStrategy calcStrategy) {
    for (RnrLineItem lineItem : requisition.getNonFullSupplyLineItems()) {
      lineItem.validateNonFullSupply();

      lineItem.calculatePacksToShip(calcStrategy);

      requisition.addToNonFullSupplyCost(lineItem.calculateCost());
    }
  }

  private void calculateForFullSupply(Rnr requisition, RnrCalculationStrategy calcStrategy, ProgramRnrTemplate template) {
    List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes = requisitionRepository.getLossesAndAdjustmentsTypes();

    for (RnrLineItem lineItem : requisition.getFullSupplyLineItems()) {
      if (!lineItem.getSkipped()) {

        lineItem.validateMandatoryFields(template);
        lineItem.calculateForFullSupply(calcStrategy, requisition.getPeriod(), template, requisition.getStatus(), lossesAndAdjustmentsTypes);
        lineItem.validateCalculatedFields(template);

        requisition.addToFullSupplyCost(lineItem.calculateCost());
      }
    }
  }

  public void calculateDaysDifference(Rnr requisition) {
    Date startDate = findDateToStartTracking(requisition);

    for (RnrLineItem lineItem : requisition.getFullSupplyLineItems()) {
      if (lineItem.getSkipped()) continue;

      Date authorizedDateForPreviousLineItem = requisitionRepository.getCreatedDateForPreviousLineItem(requisition, lineItem.getProductCode(), startDate);
      if (authorizedDateForPreviousLineItem != null) {
        Integer daysDifference = Math.round((requisition.getCreatedDate().getTime() - authorizedDateForPreviousLineItem.getTime()) / MILLI_SECONDS_IN_ONE_DAY);
        lineItem.setDaysSinceLastLineItem(daysDifference);
      }
    }
  }

  private Date findDateToStartTracking(Rnr requisition) {
    Date startDate;

    ProcessingPeriod immediatePreviousPeriod = processingScheduleService.getImmediatePreviousPeriod(requisition.getPeriod());
    if (immediatePreviousPeriod != null) {
      Integer M = immediatePreviousPeriod.getNumberOfMonths();
      ProcessingPeriod secondImmediatePreviousPeriod = processingScheduleService.getImmediatePreviousPeriod(immediatePreviousPeriod);
      startDate = (M < 3 && secondImmediatePreviousPeriod != null) ? secondImmediatePreviousPeriod.getStartDate() :
          immediatePreviousPeriod.getStartDate();
    } else {
      startDate = requisition.getPeriod().getStartDate();
    }
    return startDate;
  }
}
