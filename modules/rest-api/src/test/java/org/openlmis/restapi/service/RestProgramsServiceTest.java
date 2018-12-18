package org.openlmis.restapi.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.openlmis.core.repository.RegimenRepository;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.ProgramWithRegimens;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RestProgramsServiceTest {

  @Mock
  private ProgramRepository programRepository;

  @Mock
  private ProgramSupportedRepository programSupportedRepository;

  @Mock
  private RegimenRepository regimenRepository;

  @InjectMocks
  private RestProgramsService restProgramsService;

  private List<ProgramSupported> programSupportedList;

  private Program program;

  @Before
  public void setUp() throws Exception {
    ProgramSupported programSupported = ProgramSupported.builder().program(new Program(1L)).build();
    program = Program.builder().code("test").name("test").parent(new Program(2L)).isSupportEmergency(false).build();
    program.setId(1L);
    programSupportedList = new ArrayList<>();
    programSupportedList.add(programSupported);
  }

  @Test
  public void shouldCallProgramsRepositoryWhenAssociatingProgramsToTheirParent() {
    restProgramsService.associate(1L, asList("P1", "P2"));
    verify(programRepository, times(1)).associateParent(1L, "P1");
    verify(programRepository, times(1)).associateParent(1L, "P2");
  }

  @Test
  public void shouldReturnEmptyValueWhenCallGetAllProgramWithRegimenByFacilityId() {
    when(programSupportedRepository.getAllByFacilityId(anyLong())).thenReturn(new ArrayList<ProgramSupported>());
    List<ProgramWithRegimens> actualResult = restProgramsService.getAllProgramWithRegimenByFacilityId(1L);
    assertThat(actualResult.size(), is(0));
  }

  @Test
  public void shouldReturnOneProgramWithRegimensWhenCallGetAllProgramWithRegimenByFacilityId() {
    when(programSupportedRepository.getAllByFacilityId(anyLong())).thenReturn(programSupportedList);
    when(programRepository.getProgramWithParentById(anyLong())).thenReturn(program);
    when(regimenRepository.getRegimensByProgramAndIsCustom(anyLong(), eq(false))).thenReturn(new ArrayList<Regimen>());

    List<ProgramWithRegimens> actualResult = restProgramsService.getAllProgramWithRegimenByFacilityId(1L);
    assertThat(actualResult.size(), is(1));
  }

  @Test
  public void shouldCallProgramsRepositoryWhenCallGetAllProgramWithRegimenByFacilityId() {
    when(programSupportedRepository.getAllByFacilityId(anyLong())).thenReturn(programSupportedList);
    when(programRepository.getProgramWithParentById(anyLong())).thenReturn(program);
    when(regimenRepository.getRegimensByProgramAndIsCustom(anyLong(), eq(false))).thenReturn(new ArrayList<Regimen>());

    restProgramsService.getAllProgramWithRegimenByFacilityId(1L);
    verify(programRepository, times(1)).getProgramWithParentById(1L);
    verify(regimenRepository, times(1)).getRegimensByProgramAndIsCustom(1L, false);
  }

}