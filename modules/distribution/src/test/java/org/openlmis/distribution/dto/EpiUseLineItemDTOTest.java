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
import org.openlmis.distribution.domain.EpiUseLineItem;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class EpiUseLineItemDTOTest {

  @Test
  public void shouldReturnEpiUseLineItem() throws Exception {

    Reading stockAtFirstOfMonth = new Reading("12345", false);
    Reading stockAtEndOfMonth = new Reading("1000", false);
    Reading received = new Reading("12345", false);
    Reading loss = new Reading("12345", false);
    Reading distributed = new Reading("12345", false);
    Reading expirationDate = new Reading("12/2013", false);

    Long facilityVisitId = 1L;
    EpiUseLineItemDTO epiUseLineItemDTO = new EpiUseLineItemDTO(facilityVisitId, null, stockAtFirstOfMonth, stockAtEndOfMonth, received, loss, distributed, expirationDate);

    EpiUseLineItem epiUseLineItem = epiUseLineItemDTO.transform();

    assertThat(epiUseLineItem.getStockAtFirstOfMonth(), is(12345));
    assertThat(epiUseLineItem.getStockAtEndOfMonth(), is(1000));
    assertThat(epiUseLineItem.getReceived(), is(12345));
    assertThat(epiUseLineItem.getLoss(), is(12345));
    assertThat(epiUseLineItem.getDistributed(), is(12345));
    assertThat(epiUseLineItem.getExpirationDate(), is("12/2013"));
    assertThat(epiUseLineItem.getFacilityVisitId(), is(facilityVisitId));
  }
}
