/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.repository.mapper.DistributionRefrigeratorsMapper;

import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionRefrigeratorsRepositoryTest {

  @InjectMocks
  private DistributionRefrigeratorsRepository repository;

  @Mock
  private DistributionRefrigeratorsMapper mapper;

  @Test
  public void shouldSaveReading() throws Exception {
    Long readingId = 3L;
    RefrigeratorReading refrigeratorReading = mock(RefrigeratorReading.class);
    RefrigeratorProblem refrigeratorProblem = mock(RefrigeratorProblem.class);

    when(refrigeratorReading.getProblem()).thenReturn(refrigeratorProblem);
    when(refrigeratorReading.getId()).thenReturn(readingId);
    repository.saveReading(refrigeratorReading);

    verify(mapper).insertReading(refrigeratorReading);
    verify(refrigeratorProblem).setReadingId(readingId);
    verify(mapper).insertProblem(refrigeratorReading.getProblem());
  }

  @Test
  public void shouldNotSaveProblemIfNotAny() throws Exception {
    Long readingId = 3L;
    RefrigeratorReading refrigeratorReading = mock(RefrigeratorReading.class);

    when(refrigeratorReading.getProblem()).thenReturn(null);
    when(refrigeratorReading.getId()).thenReturn(readingId);
    repository.saveReading(refrigeratorReading);

    verify(mapper).insertReading(refrigeratorReading);
    verify(mapper, never()).insertProblem(refrigeratorReading.getProblem());
  }
}
