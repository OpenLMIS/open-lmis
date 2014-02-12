/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.distribution.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.mapper.FacilityVisitMapper;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityVisitRepositoryTest {

  @Mock
  FacilityVisitMapper facilityVisitMapper;

  @Rule
  public ExpectedException expectedException = none();

  @InjectMocks
  FacilityVisitRepository facilityVisitRepository;

  @Test
  public void shouldReturnFacilityVisit() {
    Distribution distribution = new Distribution();
    distribution.setId(1L);
    distribution.setCreatedBy(3L);
    Facility facility = new Facility(2L);

    FacilityVisit facilityVisit = new FacilityVisit(facility, distribution);
    when(facilityVisitMapper.getBy(2L, 1L)).thenReturn(facilityVisit);

    FacilityVisit expectedFacilityVisit = facilityVisitRepository.get(facilityVisit);

    assertThat(expectedFacilityVisit, is(facilityVisit));

  }

  @Test
  public void shouldCreateFacilityVisitIfIdNotPresent() {
    FacilityVisit facilityVisit = new FacilityVisit();

    FacilityVisit returnedFacilityVisit = facilityVisitRepository.save(facilityVisit);

    verify(facilityVisitMapper).insert(facilityVisit);
    assertThat(returnedFacilityVisit, is(facilityVisit));
  }

  @Test
  public void shouldUpdateFacilityVisitIfIdPresent() {
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisit.setId(2L);

    FacilityVisit returnedFacilityVisit = facilityVisitRepository.save(facilityVisit);

    verify(facilityVisitMapper).update(facilityVisit);
    assertThat(returnedFacilityVisit, is(facilityVisit));
  }
}

