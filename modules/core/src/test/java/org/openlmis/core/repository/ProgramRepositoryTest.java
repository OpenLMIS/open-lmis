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
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

public class ProgramRepositoryTest {

  private ProgramMapper programMapper;
  private ProgramRepository programRepository;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() {
    programMapper = mock(ProgramMapper.class);
    programRepository = new ProgramRepository(programMapper, null, null);
  }

  @Test
  public void shouldReturnIdForTheGivenCode() {
    when(programMapper.getIdForCode("ABC")).thenReturn(10);
    assertThat(programRepository.getIdForCode("ABC"), is(10));
  }

  @Test
  public void shouldGetProgramsSupportedByFacilityForUserWithRight() throws Exception {
    Integer facilityId = 1;
    Integer userId = 1;
    List<Program> programs = new ArrayList<>();
    when(programMapper.getProgramsSupportedByFacilityForUserWithRight(facilityId, userId, "{AUTHORIZE_REQUISITION, CREATE_REQUISITION}")).thenReturn(programs);

    List<Program> result = programRepository.getProgramsSupportedByFacilityForUserWithRight(facilityId, userId, AUTHORIZE_REQUISITION, CREATE_REQUISITION);

    verify(programMapper).getProgramsSupportedByFacilityForUserWithRight(facilityId, userId, "{AUTHORIZE_REQUISITION, CREATE_REQUISITION}");
    assertThat(result, is(programs));
  }

  @Test
  public void shouldGetProgramsSupportedByFacilitySupervisedByUserWithRights() throws Exception {
    Integer userId = 1;
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
    exception.expectMessage("Invalid Program Code");
    programRepository.getIdForCode("ABC");
  }
}
