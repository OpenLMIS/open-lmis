/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.IntegrationTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
public class OrderNumberConfigurationTest {

  @Test
  public void shouldGenerateOrderNumberAccordingToConfiguration() {
    OrderNumberConfiguration orderNumberConfiguration = new OrderNumberConfiguration("Ord", true, true, true, true);
    Program program = new Program(1L);
    program.setCode("MALARIA");
    String orderNumber = orderNumberConfiguration.getOrderNumberFor(1L, program, false);

    assertThat(orderNumber, is("OrdMALARIA00000001R"));

    orderNumberConfiguration = new OrderNumberConfiguration("Ord", true, false, true, true);
    orderNumber = orderNumberConfiguration.getOrderNumberFor(1L, program, false);

    assertThat(orderNumber, is("Ord00000001R"));
  }
}
