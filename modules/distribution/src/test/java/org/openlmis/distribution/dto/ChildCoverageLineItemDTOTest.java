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
import org.openlmis.distribution.domain.ChildCoverageLineItem;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class ChildCoverageLineItemDTOTest {

  @Test
  public void shouldTransformDTOIntoObject() throws Exception {
    ChildCoverageLineItemDTO childCoverageLineItemDTO = new ChildCoverageLineItemDTO();
    childCoverageLineItemDTO.setHealthCenter11Months(new Reading("123", false));
    childCoverageLineItemDTO.setHealthCenter23Months(new Reading("12", false));
    childCoverageLineItemDTO.setOutreach11Months(new Reading("10", false));
    childCoverageLineItemDTO.setOutreach23Months(new Reading("20", false));
    childCoverageLineItemDTO.setModifiedBy(2121L);
    childCoverageLineItemDTO.setId(11122L);

    ChildCoverageLineItem lineItem = childCoverageLineItemDTO.transform();

    assertThat(lineItem.getHealthCenter11Months(), is(123));
    assertThat(lineItem.getHealthCenter23Months(), is(12));
    assertThat(lineItem.getOutreach11Months(), is(10));
    assertThat(lineItem.getOutreach23Months(), is(20));
    assertThat(lineItem.getModifiedBy(), is(2121L));
    assertThat(lineItem.getId(), is(11122L));
  }
}
