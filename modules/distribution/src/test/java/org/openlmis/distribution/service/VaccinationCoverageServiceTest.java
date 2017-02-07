/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.VaccinationAdultCoverage;
import org.openlmis.distribution.domain.VaccinationChildCoverage;
import org.openlmis.distribution.domain.VaccinationFullCoverage;
import org.openlmis.distribution.repository.VaccinationCoverageRepository;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccinationCoverageServiceTest {

  @Mock
  private VaccinationCoverageRepository repository;

  @InjectMocks
  private VaccinationCoverageService service;

  @Test
  public void shouldSaveCoverageData() {
    VaccinationFullCoverage fullCoverage = new VaccinationFullCoverage();
    VaccinationChildCoverage childCoverage = new VaccinationChildCoverage();
    VaccinationAdultCoverage adultCoverage = new VaccinationAdultCoverage();
    FacilityDistribution facilityDistribution = new FacilityDistribution();
    facilityDistribution.setFullCoverage(fullCoverage);
    facilityDistribution.setChildCoverage(childCoverage);
    facilityDistribution.setAdultCoverage(adultCoverage);

    service.save(facilityDistribution);

    verify(repository).saveFullCoverage(fullCoverage);
    verify(repository).saveChildCoverage(childCoverage);
    verify(repository).saveAdultCoverage(adultCoverage);
  }

  @Test
  public void shouldNotSaveEmptyCoverageData() {
    FacilityDistribution facilityDistribution = new FacilityDistribution();
    facilityDistribution.setFullCoverage(null);
    facilityDistribution.setChildCoverage(null);
    facilityDistribution.setAdultCoverage(null);

    service.save(facilityDistribution);

    verify(repository, never()).saveFullCoverage(any(VaccinationFullCoverage.class));
    verify(repository, never()).saveChildCoverage(any(VaccinationChildCoverage.class));
    verify(repository, never()).saveAdultCoverage(any(VaccinationAdultCoverage.class));
  }
}
