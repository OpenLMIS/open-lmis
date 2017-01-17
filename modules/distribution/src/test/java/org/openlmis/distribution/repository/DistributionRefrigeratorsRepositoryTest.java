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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.repository.mapper.DistributionRefrigeratorsMapper;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionRefrigeratorsRepositoryTest {

  @InjectMocks
  private DistributionRefrigeratorsRepository repository;

  @Mock
  private DistributionRefrigeratorsMapper mapper;

  @Mock
  private RefrigeratorReading reading;

  @Mock
  private RefrigeratorProblem problem;

  @Captor
  private ArgumentCaptor<RefrigeratorProblem> problemCaptor;

  @Test
  public void shouldInsertOnlyReading() {
    doReturn(null).when(reading).getId();
    doReturn(problem).when(reading).getProblem();

    repository.saveReading(reading, false);

    verify(mapper).insertReading(reading);
    verify(mapper, never()).insertProblem(any(RefrigeratorProblem.class));
  }

  @Test
  public void shouldInsertReadingAndProblem() {
    doReturn(null).when(reading).getId();
    doReturn(problem).when(reading).getProblem();

    repository.saveReading(reading, true);

    verify(mapper).insertReading(reading);
    verify(problem).setReadingId(anyLong());
    verify(mapper).insertProblem(problem);
  }

  @Test
  public void shouldInsertReadingAndCreateNewProblemIfNotExist() {
    doReturn(null).when(reading).getId();
    doReturn(null).when(reading).getProblem();

    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        doReturn(1L).when(reading).getId();
        return null;
      }
    }).when(mapper).insertReading(reading);

    repository.saveReading(reading, true);

    verify(mapper).insertReading(reading);
    verify(mapper).insertProblem(problemCaptor.capture());

    RefrigeratorProblem captured = problemCaptor.getValue();
    assertThat(captured.getReadingId(), is(1L));
  }

  @Test
  public void shouldUpdateOnlyReading() {
    doReturn(1L).when(reading).getId();
    doReturn(problem).when(reading).getProblem();

    repository.saveReading(reading, false);

    verify(mapper).updateReading(reading);
    verify(mapper, never()).insertProblem(any(RefrigeratorProblem.class));
    verify(mapper, never()).updateProblem(any(RefrigeratorProblem.class));
  }

  @Test
  public void shouldUpdateReadingAndProblem() {
    doReturn(1L).when(reading).getId();
    doReturn(problem).when(reading).getProblem();

    repository.saveReading(reading, true);

    verify(mapper).updateReading(reading);
    verify(mapper).updateProblem(problem);
  }

  @Test
  public void shouldUpdateReadingAndCreateNewProblemIfNotExist() {
    doReturn(1L).when(reading).getId();
    doReturn(null).when(reading).getProblem();

    repository.saveReading(reading, true);

    verify(mapper).updateReading(reading);
    verify(mapper).insertProblem(problemCaptor.capture());
    verify(mapper, never()).updateProblem(any(RefrigeratorProblem.class));

    RefrigeratorProblem captured = problemCaptor.getValue();
    assertThat(captured.getReadingId(), is(1L));
  }

  @Test
  public void shouldInsertProblem() {
    doReturn(null).when(problem).getId();

    repository.saveProblem(problem);

    verify(mapper).insertProblem(problem);
    verify(mapper, never()).updateProblem(problem);
  }

  @Test
  public void shouldUpdateProblem() {
    doReturn(2L).when(problem).getId();

    repository.saveProblem(problem);

    verify(mapper).updateProblem(problem);
    verify(mapper, never()).insertProblem(problem);
  }

  @Test
  public void shouldReturnReading() {
    doReturn(problem).when(reading).getProblem();
    doReturn(reading).when(mapper).getReading(1L);

    RefrigeratorReading db = repository.getReading(1L);

    assertThat(db, is(reading));
    verify(reading, never()).setProblem(any(RefrigeratorProblem.class));
  }

  @Test
  public void shouldReturnReadingAndSetEmptyProblem() {
    doReturn(null).when(reading).getProblem();
    doReturn(reading).when(mapper).getReading(1L);

    RefrigeratorReading db = repository.getReading(1L);

    assertThat(db, is(reading));
    verify(reading).setProblem(any(RefrigeratorProblem.class));
  }
}
