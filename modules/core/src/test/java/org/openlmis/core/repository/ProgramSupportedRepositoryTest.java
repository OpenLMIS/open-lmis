package org.openlmis.core.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramSupportedBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.ProgramBuilder.programId;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DateTime.class})
public class ProgramSupportedRepositoryTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private ProgramSupportedMapper mapper;

  private ProgramSupportedRepository repository;
  private DateTime now;

  @Before
  public void setUp() throws Exception {
    mockStatic(DateTime.class);
    now = new DateTime(2012, 10, 10, 8, 0);
    when(DateTime.now()).thenReturn(now);
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

  @Test
  public void shouldRaiseDuplicateProgramSupportedError() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("facility code");
    programSupported.setProgramCode("program code");

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Facility has already been mapped to the program");

    doThrow(new DuplicateKeyException("Facility has already been mapped to the program")).when(mapper).addSupportedProgram(programSupported);

    repository.addSupportedProgram(programSupported);
  }

  @Test
  public void shouldUpdateSupportedProgramsForFacilityIfIdIsDefined() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(1);

    List<Program> programs = new ArrayList<Program>() {{
      add(make(a(ProgramBuilder.defaultProgram)));
      add(make(a(ProgramBuilder.defaultProgram, with(programCode, "HIV"), with(programId, 1))));
    }};

    facility.setSupportedPrograms(programs);

    List<Program> programsForFacility = new ArrayList<Program>() {{
      add(make(a(ProgramBuilder.defaultProgram)));
      add(make(a(ProgramBuilder.defaultProgram, with(programCode, "ARV"), with(programId, 2))));
    }};

    repository.updateSupportedPrograms(facility, programsForFacility);

    verify(mapper).delete(facility.getId(), 2);
    verify(mapper).addSupportedProgram(new ProgramSupported(facility.getId(), 1, true, now.toDate(), now.toDate(), facility.getModifiedBy()));
  }
}
