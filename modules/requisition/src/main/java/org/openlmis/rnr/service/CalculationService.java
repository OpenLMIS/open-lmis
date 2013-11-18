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
import org.openlmis.rnr.calculation.RnrCalculationStrategy;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculationService {

  @Autowired
  RequisitionRepository requisitionRepository;

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

}
