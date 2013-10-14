/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.rnr.domain;

import org.openlmis.core.domain.ProcessingPeriod;

import java.math.BigDecimal;
import java.util.List;

public final class EmergencyRnrCalcStrategy extends RnrCalcStrategy {

  @Override
  public Integer calculateNormalizedConsumption(Integer stockOutDays, Integer quantityDispensed,
                                                Integer newPatientCount, Integer dosesPerMonth,
                                                Integer dosesPerDispensingUnit) {
    return null;
  }

  @Override
  public Integer calculateAmc(ProcessingPeriod period, Integer normalizedConsumption, List<Integer> previousNormalizedConsumptions, BigDecimal sumOfPreviousNormalizedConsumptions) {
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
}
