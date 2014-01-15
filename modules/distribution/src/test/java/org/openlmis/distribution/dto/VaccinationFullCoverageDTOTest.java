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
import org.openlmis.distribution.domain.FullCoverage;
import org.openlmis.distribution.domain.VaccinationFullCoverage;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
public class VaccinationFullCoverageDTOTest {

  @Test
  public void shouldTransformCoverageDTOIntoCoverage() throws Exception {
    FullCoverage expectedFullCoverage = mock(FullCoverage.class);
    FullCoverageDTO fullCoverageDTO = mock(FullCoverageDTO.class);
    when(fullCoverageDTO.transform()).thenReturn(expectedFullCoverage);
    VaccinationCoverageDTO coverageDTO = new VaccinationCoverageDTO(fullCoverageDTO);

    VaccinationFullCoverage vaccinationFullCoverage = coverageDTO.transform();

    verify(fullCoverageDTO).transform();
    assertThat(vaccinationFullCoverage.getFullCoverage(), is(expectedFullCoverage));
  }
}
