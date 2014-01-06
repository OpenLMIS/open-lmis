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
import org.openlmis.distribution.domain.VaccinationCoverage;
import org.openlmis.distribution.domain.VaccinationFullCoverage;
import org.openlmis.distribution.repository.VaccinationCoverageRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccinationCoverageServiceTest {

  @Mock
  private VaccinationCoverageRepository repository;

  @InjectMocks
  private VaccinationCoverageService service;

  @Test
  public void shouldSaveVaccinationCoverageAlongWithFullCoverage() throws Exception {
    VaccinationFullCoverage vaccinationFullCoverage = new VaccinationFullCoverage();
    VaccinationCoverage vaccinationCoverage = new VaccinationCoverage();
    vaccinationCoverage.setId(55L);
    vaccinationCoverage.setFullCoverage(vaccinationFullCoverage);

    service.save(vaccinationCoverage);

    verify(repository).save(vaccinationCoverage);
    verify(repository).saveFullCoverage(vaccinationFullCoverage);
    assertThat(vaccinationFullCoverage.getVaccinationCoverageId(), is(vaccinationCoverage.getId()));
  }
}
