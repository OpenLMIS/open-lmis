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
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.*;

public class ProgramRepositoryTest {

  private ProgramMapper programMapper;
  private ProgramRepository programRepository;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() {
    programMapper = mock(ProgramMapper.class);
    programRepository = new ProgramRepository(programMapper);
  }

  @Test
  public void shouldReturnIdForTheGivenCode() {
    when(programMapper.getIdForCode("ABC")).thenReturn(10L);
    assertThat(programRepository.getIdByCode("ABC"), is(10L));
  }

  @Test
  public void shouldGetProgramsSupportedByFacilityForUserWithRight() throws Exception {
    Long facilityId = 1L;
    Long userId = 1L;
    List<Program> programs = new ArrayList<>();
    when(programMapper.getProgramsSupportedByFacilityForUserWithRights(facilityId, userId, "{AUTHORIZE_REQUISITION, CREATE_REQUISITION}")).thenReturn(programs);

    List<Program> result = programRepository.getProgramsSupportedByFacilityForUserWithRights(facilityId, userId, AUTHORIZE_REQUISITION, CREATE_REQUISITION);

    verify(programMapper).getProgramsSupportedByFacilityForUserWithRights(facilityId, userId, "{AUTHORIZE_REQUISITION, CREATE_REQUISITION}");
    assertThat(result, is(programs));
  }

  @Test
  public void shouldGetProgramsSupportedByFacilitySupervisedByUserWithRights() throws Exception {
    Long userId = 1L;
    List<Program> programs = new ArrayList<>();
    when(programMapper.getUserSupervisedActivePrograms(userId, "{AUTHORIZE_REQUISITION, CREATE_REQUISITION}")).thenReturn(programs);

    List<Program> result = programRepository.getUserSupervisedActiveProgramsWithRights(userId, AUTHORIZE_REQUISITION, CREATE_REQUISITION);

    verify(programMapper).getUserSupervisedActivePrograms(userId, "{AUTHORIZE_REQUISITION, CREATE_REQUISITION}");
    assertThat(result, is(programs));
  }

  @Test
  public void shouldThrowExceptionWhenCodeDoesNotExist() {
    when(programMapper.getIdForCode("ABC")).thenReturn(null);
    exception.expect(DataException.class);
    exception.expectMessage("program.code.invalid");
    programRepository.getIdByCode("ABC");
  }

  @Test
  public void shouldReturnActiveProgramsForUserWithGivenRight() throws Exception {
    List<Program> expectedPrograms = new ArrayList<>();
    when(programMapper.getActiveProgramsForUserWithRights(1L, "{APPROVE_REQUISITION, CREATE_REQUISITION}")).thenReturn(expectedPrograms);
    List<Program> resultPrograms = programRepository.getActiveProgramsForUserWithRights(1L, APPROVE_REQUISITION, CREATE_REQUISITION);

    verify(programMapper).getActiveProgramsForUserWithRights(1L, "{APPROVE_REQUISITION, CREATE_REQUISITION}");
    assertThat(resultPrograms, is(expectedPrograms));
  }

  @Test
  public void shouldGetProgramById() throws Exception {
    final Program expectedProgram = new Program();
    when(programMapper.getById(1L)).thenReturn(expectedProgram);
    final Program actualProgram = programRepository.getById(1L);

    verify(programMapper).getById(1L);
    assertThat(actualProgram, is(expectedProgram));
  }

  @Test
  public void shouldGetAllSupportedProgramsForFacility() throws Exception {
    List<Program> expectedPrograms = new ArrayList<>();
    when(programMapper.getByFacilityId(1L)).thenReturn(expectedPrograms);

    List<Program> programs = programRepository.getByFacility(1L);

    assertThat(programs, is(expectedPrograms));
    verify(programMapper, times(1)).getByFacilityId(1L);
  }

  @Test
  public void shouldSetTemplateConfiguredFlag() throws Exception {
    programRepository.setTemplateConfigured(1L);

    verify(programMapper).setTemplateConfigured(1L);
  }
}
