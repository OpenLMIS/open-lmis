package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProgramSupportedBuilder;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProgramSupportedRepositoryTest {
  @Mock
  private ProgramSupportedMapper mapper;

  private ProgramSupportedRepository repository;

  @Before
  public void setUp() throws Exception {
    repository = new ProgramSupportedRepository(mapper);
  }

  @Test
  public void shouldGetProgramStartDate() throws Exception {
    ProgramSupported programSupported = make(a(ProgramSupportedBuilder.defaultProgramSupported));
    when(mapper.getBy(1, 2)).thenReturn(programSupported);

    assertThat(repository.getProgramStartDate(1, 2), is(programSupported.getStartDate()));
  }

  @Test
  public void shouldDeleteSupportedPrograms() throws Exception {
    repository.deleteSupportedPrograms(1, 2);

    verify(mapper).delete(1, 2);
  }

  @Test
  public void shouldAddSupportedPrograms() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();

    repository.addSupportedProgram(programSupported);

    verify(mapper).addSupportedProgram(programSupported);
  }
}
