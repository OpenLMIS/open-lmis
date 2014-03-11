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
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.AdultCoverageLineItem;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class AdultCoverageLineItemDTOTest {

  @Test
  public void shouldTransformDTOIntoObject() throws Exception {
    AdultCoverageLineItemDTO adultCoverageLineItemDTO = new AdultCoverageLineItemDTO();
    adultCoverageLineItemDTO.setModifiedBy(2121L);
    adultCoverageLineItemDTO.setId(11122L);
    adultCoverageLineItemDTO.setHealthCenterTetanus1(new Reading("100", false));
    adultCoverageLineItemDTO.setOutreachTetanus1(new Reading("", true));
    adultCoverageLineItemDTO.setHealthCenterTetanus2To5(new Reading("450", false));
    adultCoverageLineItemDTO.setOutreachTetanus2To5(new Reading("7878", false));

    AdultCoverageLineItem lineItem = adultCoverageLineItemDTO.transform();

    assertThat(lineItem.getHealthCenterTetanus1(), is(100));
    assertThat(lineItem.getOutreachTetanus1(), is(nullValue()));
    assertThat(lineItem.getHealthCenterTetanus2To5(), is(450));
    assertThat(lineItem.getOutreachTetanus2To5(), is(7878));
    assertThat(lineItem.getModifiedBy(), is(2121L));
    assertThat(lineItem.getId(), is(11122L));
  }
}
