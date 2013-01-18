package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.builder.ProgramSupportedBuilder;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class ProgramSupportedRepositoryTest {
  @Mock
  private ProgramSupportedMapper mapper;
  private ProgramSupportedRepository repository;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    repository = new ProgramSupportedRepository(mapper);
  }

  @Test
  public void shouldGetProgramStartDate() throws Exception {
    ProgramSupported programSupported = make(a(ProgramSupportedBuilder.defaultProgramSupported));
    when(mapper.getBy(1, 2)).thenReturn(programSupported);

    assertThat(repository.getProgramStartDate(1, 2), is(programSupported.getStartDate()));
  }
}
