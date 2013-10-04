/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.repository.DeliveryZoneProgramScheduleRepository;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.DeliveryZoneProgramScheduleService;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneProgramScheduleServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private DeliveryZoneProgramScheduleRepository repository;

  @Mock
  private ProgramService programService;

  @Mock
  DeliveryZoneService deliveryZoneService;

  @Mock
  ProcessingScheduleService scheduleService;

  @InjectMocks
  private DeliveryZoneProgramScheduleService service;

  DeliveryZoneProgramSchedule deliveryZoneProgramSchedule;

  @Before
  public void setUp() throws Exception {
    deliveryZoneProgramSchedule = new DeliveryZoneProgramSchedule();
    deliveryZoneProgramSchedule.setProgram(make(a(defaultProgram)));
    deliveryZoneProgramSchedule.setDeliveryZone(make(a(defaultDeliveryZone)));
    deliveryZoneProgramSchedule.setSchedule(make(a(defaultProcessingSchedule)));

    Program program = new Program();
    program.setPush(true);
    when(programService.getByCode(deliveryZoneProgramSchedule.getProgram().getCode())).thenReturn(program);
    when(deliveryZoneService.getByCode(deliveryZoneProgramSchedule.getDeliveryZone().getCode())).thenReturn(new DeliveryZone());
    when(scheduleService.getByCode(deliveryZoneProgramSchedule.getSchedule().getCode())).thenReturn(new ProcessingSchedule());
  }

  @Test
  public void shouldSaveDZProgramSchedule() throws Exception {
    service.save(deliveryZoneProgramSchedule);
    verify(repository).insert(deliveryZoneProgramSchedule);
  }

  @Test
  public void shouldUpdateDZProgramScheduleIfIdExists() throws Exception {
    deliveryZoneProgramSchedule.setId(1l);

    service.save(deliveryZoneProgramSchedule);

    verify(repository).update(deliveryZoneProgramSchedule);
  }

  @Test
  public void shouldThrowErrorIfInvalidProgramCode() throws Exception {
    when(programService.getByCode(deliveryZoneProgramSchedule.getProgram().getCode())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("program.code.invalid");

    service.save(deliveryZoneProgramSchedule);
  }

  @Test
  public void shouldThrowErrorIfInvalidDZCode() throws Exception {
    when(deliveryZoneService.getByCode(deliveryZoneProgramSchedule.getDeliveryZone().getCode())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("deliveryZone.code.invalid");

    service.save(deliveryZoneProgramSchedule);
  }

  @Test
  public void shouldThrowErrorIfInvalidScheduleCode() throws Exception {
    when(scheduleService.getByCode(deliveryZoneProgramSchedule.getSchedule().getCode())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("schedule.code.invalid");

    service.save(deliveryZoneProgramSchedule);
  }

  @Test
  public void shouldThrowErrorIfProgramNotOfTypePush() throws Exception {
    Program program = new Program();
    program.setPush(false);
    when(programService.getByCode(deliveryZoneProgramSchedule.getProgram().getCode())).thenReturn(program);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.program.not.push");

    service.save(deliveryZoneProgramSchedule);
  }

  @Test
  public void shouldGetProcessingScheduleByZoneAndProgram() throws Exception {
    ProcessingSchedule expectedSchedule = new ProcessingSchedule();
    Long scheduleId = 3l;
    expectedSchedule.setId(scheduleId);
    when(repository.getProcessingScheduleByZoneAndProgram(1l, 2l)).thenReturn(expectedSchedule);

    ProcessingSchedule processingSchedule = service.getProcessingScheduleByZoneAndProgram(1l, 2l);

    assertThat(processingSchedule, is(expectedSchedule));
    verify(repository).getProcessingScheduleByZoneAndProgram(1l, 2l);
  }

  @Test
  public void shouldGetPeriodsForProgramAndDeliveryZone() throws Exception {
    ProcessingSchedule expectedSchedule = new ProcessingSchedule();
    Long scheduleId = 3l;
    expectedSchedule.setId(scheduleId);
    List<ProcessingPeriod> periods = new ArrayList<>();
    when(scheduleService.getAllPeriodsBefore(scheduleId, null)).thenReturn(periods);
    when(repository.getProcessingScheduleByZoneAndProgram(1l, 2l)).thenReturn(expectedSchedule);

    List<ProcessingPeriod> returnedPeriods = service.getPeriodsForDeliveryZoneAndProgram(1l, 2l);

    assertThat(returnedPeriods, is(periods));
    verify(scheduleService).getAllPeriodsBefore(scheduleId, null);
    verify(repository).getProcessingScheduleByZoneAndProgram(1l, 2l);
  }
}
