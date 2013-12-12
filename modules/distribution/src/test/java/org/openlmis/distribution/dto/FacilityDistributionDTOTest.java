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
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.FacilityVisit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FacilityDistributionDTOTest {

  @Test
  public void shouldTransformDTOIntoRealObject() throws Exception {

    FacilityVisit facilityVisit = new FacilityVisit();
    EpiUseDTO epiUseDTO = mock(EpiUseDTO.class);
    FacilityDistributionDTO facilityDistributionDTO = new FacilityDistributionDTO();
    facilityDistributionDTO.setFacilityVisit(facilityVisit);
    facilityDistributionDTO.setEpiUse(epiUseDTO);

    EpiUse epiUse = new EpiUse();
    when(epiUseDTO.transform()).thenReturn(epiUse);

    FacilityDistribution facilityDistribution = facilityDistributionDTO.transform();

    assertThat(facilityDistribution.getFacilityVisit(), is(facilityDistributionDTO.getFacilityVisit()));
    assertThat(facilityDistribution.getEpiUse(), is(epiUse));
  }
}
