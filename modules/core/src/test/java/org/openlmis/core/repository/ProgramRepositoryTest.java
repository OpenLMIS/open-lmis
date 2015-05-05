/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.RightName.*;

@Category(UnitTests.class)
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
    when(programMapper.getProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, "{AUTHORIZE_REQUISITION, CREATE_REQUISITION}")).thenReturn(programs);

    List<Program> result = programRepository.getProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, AUTHORIZE_REQUISITION, CREATE_REQUISITION);

    verify(programMapper).getProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, "{AUTHORIZE_REQUISITION, CREATE_REQUISITION}");
    assertThat(result, is(programs));
  }

  @Test
  public void shouldGetIvdProgramsSupportedByFacilityForUserWithRight() throws Exception {
    Long facilityId = 1L;
    Long userId = 1L;
    List<Program> programs = new ArrayList<>();
    when(programMapper.getIvdProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, "{AUTHORIZE_REQUISITION, CREATE_REQUISITION}")).thenReturn(programs);

    List<Program> result = programRepository.getIvdProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, AUTHORIZE_REQUISITION, CREATE_REQUISITION);

    verify(programMapper).getIvdProgramsSupportedByUserHomeFacilityWithRights(facilityId, userId, "{AUTHORIZE_REQUISITION, CREATE_REQUISITION}");
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
  public void shouldGetSupportedProgramsForFacilityForAUserForGivenRights() throws Exception {
    List<Program> expectedPrograms = new ArrayList<>();
    when(programMapper.getProgramsForUserByFacilityAndRights(1L, 1L, "{AUTHORIZE_REQUISITION, VIEW_REQUISITION}")).thenReturn(expectedPrograms);

    List<Program> programs = programRepository.getProgramsForUserByFacilityAndRights(1L, 1L, AUTHORIZE_REQUISITION, VIEW_REQUISITION);

    assertThat(programs, is(expectedPrograms));
    verify(programMapper, times(1)).getProgramsForUserByFacilityAndRights(1L, 1L, "{AUTHORIZE_REQUISITION, VIEW_REQUISITION}");
  }

  @Test
  public void shouldSetTemplateConfiguredFlag() throws Exception {
    programRepository.setTemplateConfigured(1L);

    verify(programMapper).setTemplateConfigured(1L);
  }

  @Test
  public void shouldSetRegimenTemplateConfiguredFlag() throws Exception {
    programRepository.setRegimenTemplateConfigured(1L);

    verify(programMapper).setRegimenTemplateConfigured(1L);
  }

  @Test
  public void shouldSetSendFeedFlag() throws Exception {
    Program program = new Program();
    programRepository.setFeedSendFlag(program, true);

    verify(programMapper).setFeedSendFlag(program, true);
  }

  @Test
  public void shouldGetAllProgramsWithSendFeedFlagTrue() throws Exception {
    List<Program> returnedPrograms = new ArrayList<>();
    when(programMapper.getProgramsForNotification()).thenReturn(returnedPrograms);

    List<Program> programs = programRepository.getProgramsForNotification();

    assertThat(programs, is(returnedPrograms));
    verify(programMapper).getProgramsForNotification();
  }

  @Test
  public void shouldGetProgramsThatSupportIVDForm() throws Exception{
    List<Program> returnedPrograms = new ArrayList<>();
    when(programMapper.getAllIvdPrograms()).thenReturn(returnedPrograms);

    List<Program> programs = programRepository.getAllIvdPrograms();

    assertThat(programs, is(returnedPrograms));
    verify(programMapper).getAllIvdPrograms();
  }
}
