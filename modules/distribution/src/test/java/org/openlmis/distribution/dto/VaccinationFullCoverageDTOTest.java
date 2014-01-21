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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
public class VaccinationFullCoverageDTOTest {

  @Test
  public void shouldTransformFullCoverageDTO() throws Exception {
    Long facilityVisitId = 1L;
    Reading femaleHealthCenterReading = mock(Reading.class);
    Reading femaleMobileBrigadeReading = mock(Reading.class);
    Reading maleMobileBrigadeReading = mock(Reading.class);
    Reading maleHealthCenterReading = mock(Reading.class);

    VaccinationFullCoverageDTO vaccinationFullCoverageDTO = new VaccinationFullCoverageDTO(facilityVisitId, femaleHealthCenterReading, femaleMobileBrigadeReading, maleHealthCenterReading, maleMobileBrigadeReading);
    vaccinationFullCoverageDTO.transform();

    verify(femaleHealthCenterReading).parsePositiveInt();
    verify(femaleMobileBrigadeReading).parsePositiveInt();
    verify(maleHealthCenterReading).parsePositiveInt();
    verify(maleMobileBrigadeReading).parsePositiveInt();
  }
}
