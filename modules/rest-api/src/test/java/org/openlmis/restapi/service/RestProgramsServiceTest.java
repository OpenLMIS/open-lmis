package org.openlmis.restapi.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.db.categories.UnitTests;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RestProgramsServiceTest {

  @Mock
  private ProgramRepository programRepository;

  @InjectMocks
  private RestProgramsService restProgramsService;

  @Test
  public void shouldCallProgramsRepositoryWhenAssociatingProgramsToTheirParent() {
    restProgramsService.associate(1L, asList("P1", "P2"));
    verify(programRepository, times(1)).associateParent(1L, "P1");
    verify(programRepository, times(1)).associateParent(1L, "P2");
  }

}