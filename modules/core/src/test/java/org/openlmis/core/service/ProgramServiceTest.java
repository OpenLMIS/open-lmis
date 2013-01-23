package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
    when(programSupportedRepository.getProgramStartDate(1, 2)).thenReturn(programStartDate);

    assertThat(service.getProgramStartDate(1, 2), is(programStartDate));
  }

  @Test
  public void shouldGetProgramById() throws Exception {
    final Program expectedProgram = new Program();
    when(programRepository.getById(1)).thenReturn(expectedProgram);

    final Program actualProgram = service.getById(1);

    verify(programRepository).getById(1);
    assertThat(actualProgram, is(expectedProgram));
  }
}

