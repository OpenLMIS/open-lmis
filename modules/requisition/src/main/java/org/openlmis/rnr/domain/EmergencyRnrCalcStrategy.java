/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.domain;

import org.openlmis.core.domain.ProcessingPeriod;

import java.util.List;

public final class EmergencyRnrCalcStrategy extends RnrCalcStrategy {

  @Override
  public Integer calculateNormalizedConsumption(Integer stockOutDays, Integer quantityDispensed,
                                                Integer newPatientCount, Integer dosesPerMonth,
                                                Integer dosesPerDispensingUnit) {
    return null;
  }

  @Override
  public Integer calculateAmc(ProcessingPeriod period, Integer normalizedConsumption, List<Integer> previousNormalizedConsumptions) {
    return null;
  }

  @Override
  public Integer calculateMaxStockQuantity(Integer maxMonthsOfStock, Integer amc) {
    return null;
  }

  @Override
  public Integer calculateOrderQuantity(Integer maxStockQuantity, Integer stockInHand) {
    return null;
  }

  @Override
  public Integer calculateDefaultApprovedQuantity(boolean fullSupply, Integer calculatedOrderQuantity, Integer quantityRequested) {
    return 0;
  }
}
