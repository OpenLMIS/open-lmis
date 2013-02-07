package org.openlmis.core.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DateTime.class})
public class ProgramSupportedRepositoryTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private ProgramSupportedMapper programSupportedMapper;

  private ProgramSupportedRepository programSupportedRepository;
  private DateTime now;

  @Before
  public void setUp() throws Exception {
    mockStatic(DateTime.class);
    now = new DateTime(2012, 10, 10, 8, 0);
    when(DateTime.now()).thenReturn(now);
    programSupportedRepository = new ProgramSupportedRepository(programSupportedMapper);
  }

  @Test
  public void shouldGetProgramStartDate() throws Exception {
    ProgramSupported programSupported = make(a(defaultProgramSupported));
    when(programSupportedMapper.getBy(1, 2)).thenReturn(programSupported);

    assertThat(programSupportedRepository.getProgramStartDate(1, 2), is(programSupported.getStartDate()));
  }

  @Test
  public void shouldDeleteSupportedPrograms() throws Exception {
    programSupportedRepository.deleteSupportedPrograms(1, 2);

    verify(programSupportedMapper).delete(1, 2);
  }

  @Test
  public void shouldAddProgramSupported() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();

    programSupportedRepository.addSupportedProgram(programSupported);

    verify(programSupportedMapper).addSupportedProgram(programSupported);
  }

  @Test
  public void shouldRaiseDuplicateProgramSupportedError() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("facility code");
    Program program = new Program();
    program.setCode("program code");
    programSupported.setProgram(program);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Facility has already been mapped to the program");

    doThrow(new DuplicateKeyException("Facility has already been mapped to the program")).when(programSupportedMapper).addSupportedProgram(programSupported);

    programSupportedRepository.addSupportedProgram(programSupported);
  }

  @Test
  public void shouldAddSupportedProgramsForFacility() throws Exception {
    Facility facility = new Facility();
    ArrayList<ProgramSupported> supportedPrograms = new ArrayList<>();
    ProgramSupported firstProgramSupported = make(a(ProgramSupportedBuilder.defaultProgramSupported));
    ProgramSupported secondProgramSupported = new ProgramSupported();
    supportedPrograms.add(firstProgramSupported);
    supportedPrograms.add(secondProgramSupported);
    facility.setSupportedPrograms(supportedPrograms);

    programSupportedRepository.addSupportedProgramsFor(facility);

    verify(programSupportedMapper).addSupportedProgram(firstProgramSupported);
    verify(programSupportedMapper).addSupportedProgram(secondProgramSupported);
  }

  @Test
  public void shouldUpdateSupportedProgramsForFacilityIfIdIsDefined() throws Exception {
    final Facility facility = make(a(defaultFacility));
    facility.setId(1);

    final ProgramSupported hivProgram = make(a(defaultProgramSupported, with(supportedProgram, new Program(1,"HIV")), with(supportedFacilityId, facility.getId())));

    List<ProgramSupported> programs = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported, with(supportedFacilityId, facility.getId()))));
      add(hivProgram);
    }};

    facility.setSupportedPrograms(programs);
    final ProgramSupported arvProgram = make(a(defaultProgramSupported, with(supportedProgram, new Program(2,"ARV")), with(supportedFacilityId, facility.getId())));

    List<ProgramSupported> previouslySupportedProgramsForFacility = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported, with(supportedFacilityId, facility.getId()))));
      add(arvProgram);
    }};

    programSupportedRepository.updateSupportedPrograms(facility, previouslySupportedProgramsForFacility);

    verify(programSupportedMapper).delete(facility.getId(), 2);
    verify(programSupportedMapper).addSupportedProgram(hivProgram);
  }

  @Test
  public void shouldGetAllSupportedProgramsForFacility() throws Exception {
    final Facility facility = make(a(defaultFacility));
    facility.setId(1);

    final ProgramSupported hivProgram = make(a(defaultProgramSupported, with(supportedProgram, new Program(1,"HIV")), with(supportedFacilityId, facility.getId())));
    List<ProgramSupported> programs = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported, with(supportedFacilityId, facility.getId()))));
      add(hivProgram);
    }};
    when(programSupportedMapper.getAllByFacilityId(facility.getId())).thenReturn(programs);

    facility.setSupportedPrograms(programs);

    assertThat(programSupportedRepository.getAllByFacilityId(facility.getId()), is(programs));
    verify(programSupportedMapper).getAllByFacilityId(facility.getId());
  }
}
