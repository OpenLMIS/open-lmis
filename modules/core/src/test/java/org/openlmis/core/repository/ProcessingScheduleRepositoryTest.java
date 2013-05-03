/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessingScheduleRepositoryTest {

  @Rule
  public ExpectedException expectedEx = org.junit.rules.ExpectedException.none();

  @Mock
  private ProcessingScheduleMapper processingScheduleMapper;

  private ProcessingScheduleRepository repository;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    repository = new ProcessingScheduleRepository(processingScheduleMapper);
  }

  @Test
  public void shouldGetAll() throws Exception {
    List<ProcessingSchedule> scheduleList = new ArrayList<>();
    scheduleList.add(new ProcessingSchedule());
    when(processingScheduleMapper.getAll()).thenReturn(scheduleList);

    List<ProcessingSchedule> schedules = repository.getAll();

    assertThat(schedules, is(scheduleList));
  }

  @Test
  public void shouldInsertASchedule() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule("testScheduleCode", "testScheduleName");
    repository.create(processingSchedule);
    verify(processingScheduleMapper).insert(processingSchedule);
  }

  @Test
  public void shouldNotSaveScheduleWithoutItsCode() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("schedule.without.code");
    repository.create(processingSchedule);
  }

  @Test
  public void shouldNotSaveScheduleWithoutItsName() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setCode("testCode");
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("schedule.without.name");
    repository.create(processingSchedule);
  }

  @Test
  public void shouldGetAScheduleById() throws Exception {
    ProcessingSchedule mockedProcessingSchedule = mock(ProcessingSchedule.class);
    when(processingScheduleMapper.get(1L)).thenReturn(mockedProcessingSchedule);
    ProcessingSchedule fetchedSchedule = repository.get(1L);
    assertThat(fetchedSchedule, is(mockedProcessingSchedule));
  }

  @Test
  public void shouldUpdateAnExistingSchedule() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule("testScheduleCode", "testScheduleName");
    repository.update(processingSchedule);
    verify(processingScheduleMapper).update(processingSchedule);
  }

  @Test
  public void shouldNotUpdateScheduleWithoutItsCode() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("schedule.without.code");
    repository.update(processingSchedule);
  }

  @Test
  public void shouldNotUpdateScheduleWithoutItsName() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setCode("testCode");
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("schedule.without.name");
    repository.update(processingSchedule);
  }

  @Test
  public void shouldThrowExceptionForDuplicateCodeWhenTryingToInsertScheduleWithExistingCode() throws Exception {
    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    when(processingScheduleMapper.insert(processingSchedule)).thenThrow(new DuplicateKeyException(""));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("A Schedule with this code already exists");

    repository.create(processingSchedule);
  }

  @Test
  public void shouldThrowExceptionForDuplicateCodeWhenTryingToUpdateScheduleWithExistingCode() throws Exception {
    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    when(processingScheduleMapper.update(processingSchedule)).thenThrow(new DuplicateKeyException(""));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("A Schedule with this code already exists");

    repository.update(processingSchedule);
  }
}
