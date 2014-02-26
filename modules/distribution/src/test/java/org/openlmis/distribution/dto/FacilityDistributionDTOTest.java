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
import org.openlmis.distribution.domain.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
public class FacilityDistributionDTOTest {

  @Test
  public void shouldTransformDTOIntoRealObject() throws Exception {
    FacilityVisit facilityVisit = new FacilityVisit();
    EpiUseDTO epiUseDTO = mock(EpiUseDTO.class);
    DistributionRefrigeratorsDTO distributionRefrigeratorsDTO = mock(DistributionRefrigeratorsDTO.class);
    VaccinationFullCoverageDTO coverageDTO = mock(VaccinationFullCoverageDTO.class);
    EpiInventoryDTO epiInventoryDTO = mock(EpiInventoryDTO.class);
    ChildCoverageDTO childCoverageDTO = mock(ChildCoverageDTO.class);
    AdultCoverageDTO adultCoverageDTO = mock(AdultCoverageDTO.class);

    FacilityDistributionDTO facilityDistributionDTO = new FacilityDistributionDTO(facilityVisit, epiUseDTO,
      epiInventoryDTO, distributionRefrigeratorsDTO, coverageDTO, childCoverageDTO, adultCoverageDTO);

    EpiUse epiUse = new EpiUse();
    when(epiUseDTO.transform()).thenReturn(epiUse);

    DistributionRefrigerators distributionRefrigerators = mock(DistributionRefrigerators.class);
    when(distributionRefrigeratorsDTO.transform()).thenReturn(distributionRefrigerators);

    VaccinationFullCoverage vaccinationFullCoverage = new VaccinationFullCoverage();
    when(coverageDTO.transform()).thenReturn(vaccinationFullCoverage);

    EpiInventory epiInventory = new EpiInventory();
    when(epiInventoryDTO.transform()).thenReturn(epiInventory);

    VaccinationChildCoverage childCoverage = new VaccinationChildCoverage();
    when(childCoverageDTO.transform()).thenReturn(childCoverage);

    VaccinationAdultCoverage adultCoverage = new VaccinationAdultCoverage();
    when(adultCoverageDTO.transform()).thenReturn(adultCoverage);

    FacilityDistribution facilityDistribution = facilityDistributionDTO.transform();

    assertThat(facilityDistribution.getFacilityVisit(), is(facilityDistributionDTO.getFacilityVisit()));
    assertThat(facilityDistribution.getEpiUse(), is(epiUse));
    assertThat(facilityDistribution.getRefrigerators(), is(distributionRefrigerators));
    assertThat(facilityDistribution.getFullCoverage(), is(vaccinationFullCoverage));
    assertThat(facilityDistribution.getEpiInventory(), is(epiInventory));
    assertThat(facilityDistribution.getChildCoverage(), is(childCoverage));
    assertThat(facilityDistribution.getAdultCoverage(), is(adultCoverage));
  }

  @Test
  public void shouldSetModifiedByForAllDistributionForms() throws Exception {
    FacilityDistributionDTO facilityDistributionDTO = new FacilityDistributionDTO(new FacilityVisit(), new EpiUseDTO(), new EpiInventoryDTO(),
      new DistributionRefrigeratorsDTO(), new VaccinationFullCoverageDTO(), new ChildCoverageDTO(),
      new AdultCoverageDTO());

    facilityDistributionDTO.setModifiedBy(4L);

    assertThat(facilityDistributionDTO.getFacilityVisit().getModifiedBy(), is(4L));
    assertThat(facilityDistributionDTO.getEpiInventory().getModifiedBy(), is(4L));
    assertThat(facilityDistributionDTO.getEpiUse().getModifiedBy(), is(4L));
    assertThat(facilityDistributionDTO.getChildCoverage().getModifiedBy(), is(4L));
    assertThat(facilityDistributionDTO.getAdultCoverage().getModifiedBy(), is(4L));

    assertThat(facilityDistributionDTO.getFullCoverage().getModifiedBy(), is(4L));
    assertThat(facilityDistributionDTO.getFullCoverage().getCreatedBy(), is(4L));

    assertThat(facilityDistributionDTO.getRefrigerators().getModifiedBy(), is(4L));
    assertThat(facilityDistributionDTO.getRefrigerators().getCreatedBy(), is(4L));
  }
}
