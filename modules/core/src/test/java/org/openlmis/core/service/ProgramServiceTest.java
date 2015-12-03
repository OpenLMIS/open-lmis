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

import org.ict4h.atomfeed.server.service.EventService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.event.ProgramChangeEvent;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.domain.RightName.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.RightName.VIEW_REQUISITION;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class ProgramServiceTest {
  @Mock
  private ProgramSupportedRepository programSupportedRepository;

  @Mock
  private ProgramRepository programRepository;

  @Mock
  EventService eventService;

  @InjectMocks
  private ProgramService service;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldGetProgramStartDate() throws Exception {
    Date programStartDate = now().toDate();
    when(programSupportedRepository.getProgramStartDate(1L, 2L)).thenReturn(programStartDate);

    assertThat(service.getProgramStartDate(1L, 2L), is(programStartDate));
  }

  @Test
  public void shouldGetProgramById() throws Exception {
    final Program expectedProgram = new Program();
    when(programRepository.getById(1L)).thenReturn(expectedProgram);

    final Program actualProgram = service.getById(1L);

    verify(programRepository).getById(1L);
    assertThat(actualProgram, is(expectedProgram));
  }

  @Test
  public void shouldGetProgramsForFacilityUserAndRights() throws Exception {
    final List<Program> expectedPrograms = new ArrayList<>();
    when(programRepository.getProgramsForUserByFacilityAndRights(1L, 1L, VIEW_REQUISITION, AUTHORIZE_REQUISITION)).thenReturn(expectedPrograms);

    List<Program> programs = service.getProgramsForUserByFacilityAndRights(1l, 1l, VIEW_REQUISITION, AUTHORIZE_REQUISITION);

    verify(programRepository).getProgramsForUserByFacilityAndRights(1l, 1l, VIEW_REQUISITION, AUTHORIZE_REQUISITION);
    assertThat(programs, is(expectedPrograms));
  }

  @Test
  public void shouldSetTemplateConfiguredFlag() {
    service.setTemplateConfigured(1L);
    verify(programRepository).setTemplateConfigured(1L);

  }

  @Test
  public void shouldSetRegimenTemplateConfiguredFlag() {
    service.setRegimenTemplateConfigured(1L);
    verify(programRepository).setRegimenTemplateConfigured(1L);

  }

  @Test
  public void shouldSetFlagForSendProgramFeed() throws Exception {
    Program program = new Program();
    service.setFeedSendFlag(program, true);

    verify(programRepository).setFeedSendFlag(program, true);
  }

  @Test
  public void shouldNotifyProgramsForWhichFeedSendFlagIsSet() throws Exception {
    List<Program> programList = new ArrayList<Program>() {{
      add(make(a(defaultProgram)));
      add(make(a(defaultProgram, with(programCode, "second program"))));
    }};
    when(programRepository.getProgramsForNotification()).thenReturn(programList);

    service.notifyProgramChange();

    verify(programRepository).getProgramsForNotification();
    verify(eventService, times(2)).notify(any(ProgramChangeEvent.class));
  }

  @Test
  public void shouldResetSendFeedFlagAfterNotifying() throws Exception {
    List<Program> programList = new ArrayList<Program>() {{
      add(make(a(defaultProgram)));
      add(make(a(defaultProgram, with(programCode, "second program"))));
    }};
    when(programRepository.getProgramsForNotification()).thenReturn(programList);

    service.notifyProgramChange();

    verify(programRepository).setFeedSendFlag(programList.get(0), false);
    verify(programRepository).setFeedSendFlag(programList.get(1), false);
  }

  @Test
  public void shouldGetValidatedProgram() throws Exception {
    String programCode = "p_code";
    Program program = make(a(defaultProgram));
    when(programRepository.getByCode(programCode)).thenReturn(program);
    Program actualProgram = service.getValidatedProgramByCode(programCode);
    verify(programRepository).getByCode(programCode);
    assertThat(actualProgram, is(program));
  }
}
