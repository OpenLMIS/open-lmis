/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.VIEW_REQUISITION;

@Category(UnitTests.class)
public class ProgramServiceTest {
  @Mock
  private ProgramSupportedRepository programSupportedRepository;
  @Mock
  private ProgramRepository programRepository;
  private ProgramService service;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    service = new ProgramService(programRepository, programSupportedRepository);
  }

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
}

