/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.db.categories.UnitTests;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class EmergencyRnrCalcStrategyTest {

  private RnrCalcStrategy emergencyCalcStrategy;

  @Before
  public void setUp() throws Exception {
    emergencyCalcStrategy = new EmergencyRnrCalcStrategy();
  }

  @Test
  public void shouldReturnNormalizedConsumptionAsNull() throws Exception {
    Integer normalizedConsumption = emergencyCalcStrategy.calculateNormalizedConsumption(3, 4, 5, 6, 7);

    assertThat(normalizedConsumption, is(nullValue()));
  }

  @Test
  public void shouldReturnAmcAsNull() throws Exception {
    Integer amc = emergencyCalcStrategy.calculateAmc(new ProcessingPeriod(), 3, Collections.<Integer>emptyList());

    assertThat(amc, is(nullValue()));
  }

  @Test
  public void shouldReturnMaxStockQuantityAsNull() throws Exception {
    Integer maxStockQuantity = emergencyCalcStrategy.calculateMaxStockQuantity(3, 4);

    assertThat(maxStockQuantity, is(nullValue()));
  }

  @Test
  public void shouldReturnCalculatedOrderQuantityAsNull() throws Exception {
    Integer orderQuantity = emergencyCalcStrategy.calculateOrderQuantity(5, 7);

    assertThat(orderQuantity, is(nullValue()));
  }
}
