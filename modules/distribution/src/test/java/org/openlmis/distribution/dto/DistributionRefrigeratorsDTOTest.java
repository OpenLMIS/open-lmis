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
import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.RefrigeratorReading;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
public class DistributionRefrigeratorsDTOTest {

  @Test
  public void shouldTransformRefrigeratorsDTOIntoRefrigerators() throws Exception {
    RefrigeratorReadingDTO refrigeratorReadingDTO1 = mock(RefrigeratorReadingDTO.class);
    RefrigeratorReadingDTO refrigeratorReadingDTO2 = mock(RefrigeratorReadingDTO.class);
    RefrigeratorReading reading1 = new RefrigeratorReading();
    RefrigeratorReading reading2 = new RefrigeratorReading();
    when(refrigeratorReadingDTO1.transform()).thenReturn(reading1);
    when(refrigeratorReadingDTO2.transform()).thenReturn(reading2);
    List<RefrigeratorReadingDTO> readings = asList(refrigeratorReadingDTO1, refrigeratorReadingDTO2);
    DistributionRefrigeratorsDTO refrigeratorsDTO = new DistributionRefrigeratorsDTO(readings);

    refrigeratorsDTO.transform();

    verify(refrigeratorReadingDTO1).transform();
    verify(refrigeratorReadingDTO2).transform();
  }

  @Test
  public void shouldTransformRefrigeratorsDTOIntoRefrigeratorsWithBlankReadingsIfNotProvided() throws Exception {
    List<RefrigeratorReading> refrigeratorReadings = new ArrayList<>();
    DistributionRefrigeratorsDTO refrigeratorsDTO = new DistributionRefrigeratorsDTO();

    DistributionRefrigerators distributionRefrigerators = refrigeratorsDTO.transform();

    assertThat(distributionRefrigerators.getReadings(), is(refrigeratorReadings));
  }
}
