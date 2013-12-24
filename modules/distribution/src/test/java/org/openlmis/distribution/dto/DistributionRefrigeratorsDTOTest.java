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
import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.RefrigeratorReading;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DistributionRefrigeratorsDTOTest {

  @Test
  public void shouldTransformRefrigeratorsDTOIntoRefrigerators() throws Exception {
    RefrigeratorReadingDTO refrigeratorReadingDTO1 = mock(RefrigeratorReadingDTO.class);
    RefrigeratorReadingDTO refrigeratorReadingDTO2 = mock(RefrigeratorReadingDTO.class);
    List<RefrigeratorReadingDTO> readings = asList(refrigeratorReadingDTO1, refrigeratorReadingDTO2);
    Long facilityId = 3L;
    Long distributionId = 6L;
    DistributionRefrigeratorsDTO refrigeratorsDTO = new DistributionRefrigeratorsDTO(facilityId, distributionId, readings);

    refrigeratorsDTO.transform();

    verify(refrigeratorReadingDTO1).transform();
    verify(refrigeratorReadingDTO2).transform();
  }

  @Test
  public void shouldTransformRefrigeratorsDTOIntoRefrigeratorsWithBlankReadingsIfNotProvided() throws Exception {
    Long facilityId = 3L;
    Long distributionId = 6L;
    List<RefrigeratorReading> refrigeratorReadings = new ArrayList<>();
    DistributionRefrigeratorsDTO refrigeratorsDTO = new DistributionRefrigeratorsDTO(facilityId, distributionId, null);

    DistributionRefrigerators distributionRefrigerators = refrigeratorsDTO.transform();

    assertThat(distributionRefrigerators.getReadings(), is(refrigeratorReadings));
    assertThat(distributionRefrigerators.getFacilityId(), is(facilityId));
    assertThat(distributionRefrigerators.getDistributionId(), is(distributionId));
  }
}
