/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.EpiInventoryLineItem;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class EpiInventoryLineItemDTOTest {

  @Test
  public void shouldTransformEpiInventoryLineItemDTOToEpiInventoryLineItem() throws Exception {

    EpiInventoryLineItemDTO epiInventoryLineItemDTO = new EpiInventoryLineItemDTO(1L, new Reading("34", false), new Reading(null, true), 56);
    EpiInventoryLineItem epiInventoryLineItem = epiInventoryLineItemDTO.transform();

    assertThat(epiInventoryLineItem.getDeliveredQuantity(), is(56));
    assertThat(epiInventoryLineItem.getExistingQuantity(), is(34));
    assertThat(epiInventoryLineItem.getSpoiledQuantity(), is(nullValue()));
    assertThat(epiInventoryLineItem.getFacilityVisitId(), is(1L));
  }
}
