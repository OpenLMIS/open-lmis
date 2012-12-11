package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProgramMapperIT extends SpringIntegrationTest {

  public static final String PROGRAM_CODE = "HIV";
  public static final Long PROGRAM_ID = 1L;

  @Autowired
  ProgramSupportedMapper programSupportedMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  ProgramMapper programMapper;

  @Test
  public void shouldGetAllActiveProgram() {
    List<Program> programs = programMapper.getAllActive();
    assertEquals(6, programs.size());
    assertThat(programs, hasItem(new Program(PROGRAM_ID, PROGRAM_CODE, PROGRAM_CODE, PROGRAM_CODE, true)));
  }

  @Test
  public void shouldGetProgramsWhichAreActiveByFacilityCode() {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    Long facilityId = facilityMapper.insert(facility);
    Program program = make(a(defaultProgram));
    Long programId = programMapper.insert(program);
    ProgramSupported programSupported = make(a(defaultProgramSupported, with(supportedFacilityId, facilityId), with(supportedProgramId, programId)));
    programSupportedMapper.addSupportedProgram(programSupported);

    List<Program> programs = programMapper.getActiveByFacility(facilityId);

    assertThat(programs.size(), is(1));
    assertThat(programs.get(0).getCode(), is(ProgramBuilder.PROGRAM_CODE));
  }

  @Test
  public void shouldGetAllPrograms() throws Exception {
    Program program = make(a(defaultProgram));
    program.setActive(false);
    program.setId(programMapper.insert(program));
    List<Program> programs = programMapper.getAll();
    assertEquals(8, programs.size());

    assertTrue("inserted program not found",programs.contains(program));
  }

  @Test
  public void shouldGetProgramsSupportedByFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    Long facilityId = facilityMapper.insert(facility);
    Program program = make(a(defaultProgram));
    Long programId = programMapper.insert(program);
    ProgramSupported programSupported =
        make(a(defaultProgramSupported, with(supportedFacilityId, facilityId), with(supportedProgramId, programId)));

    programSupportedMapper.addSupportedProgram(programSupported);
    List<Program> supportedPrograms = programMapper.getByFacilityId(facilityId);
    assertThat(supportedPrograms.get(0).getCode(), is(ProgramBuilder.PROGRAM_CODE));
  }
}
