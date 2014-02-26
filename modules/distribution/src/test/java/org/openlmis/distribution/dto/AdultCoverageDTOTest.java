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
import org.openlmis.distribution.domain.OpenedVialLineItem;
import org.openlmis.distribution.domain.VaccinationAdultCoverage;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class AdultCoverageDTOTest {

  @Test
  public void shouldTransformAdultCoverageDTOToVaccinationAdultCoverage() throws Exception {
    AdultCoverageLineItemDTO adultCoverageLineItemDTO = mock(AdultCoverageLineItemDTO.class);
    OpenedVialLineItemDTO openedVialLineItemDTO = mock(OpenedVialLineItemDTO.class);
    AdultCoverageDTO adultCoverageDTO = new AdultCoverageDTO();
    adultCoverageDTO.setModifiedBy(12345L);
    adultCoverageDTO.setAdultCoverageLineItems(asList(adultCoverageLineItemDTO));
    adultCoverageDTO.setOpenedVialLineItems(asList(openedVialLineItemDTO));

    AdultCoverageLineItem adultCoverageLineItem = new AdultCoverageLineItem();
    when(adultCoverageLineItemDTO.transform()).thenReturn(adultCoverageLineItem);
    OpenedVialLineItem openedVialLineItem = new OpenedVialLineItem();
    when(openedVialLineItemDTO.transform()).thenReturn(openedVialLineItem);

    VaccinationAdultCoverage vaccinationAdultCoverage = adultCoverageDTO.transform();

    assertThat(vaccinationAdultCoverage.getAdultCoverageLineItems().get(0), is(adultCoverageLineItem));
    assertThat(vaccinationAdultCoverage.getOpenedVialLineItems().get(0), is(openedVialLineItem));

    verify(adultCoverageLineItemDTO).setModifiedBy(12345L);
    verify(openedVialLineItemDTO).setModifiedBy(12345L);

  }
}
