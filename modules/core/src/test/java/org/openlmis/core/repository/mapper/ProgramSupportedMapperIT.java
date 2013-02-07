package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ProgramSupportedMapperIT {

  public static final String YELLOW_FEVER = "YELL_FVR";

  @Autowired
  private ProgramMapper programMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  private ProgramSupportedMapper programSupportedMapper;

  @Test
  public void shouldSaveProgramSupported() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);

    ProgramSupported programSupported = make(a(defaultProgramSupported,
        with(supportedFacilityId, facility.getId()),
        with(supportedProgram,program)));

    programSupportedMapper.addSupportedProgram(programSupported);

    ProgramSupported result = programSupportedMapper.getBy(facility.getId(), program.getId());
    assertThat(result.getFacilityId(), is(facility.getId()));
    assertThat(result.getProgram().getId(), is(program.getId()));
    assertThat(result.getStartDate(), is(programSupported.getStartDate()));
  }

  @Test
  public void shouldDeleteProgramMapping() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);

    ProgramSupported programSupported = make(a(defaultProgramSupported,
        with(supportedFacilityId, facility.getId()),
        with(supportedProgram, program)));
    programSupportedMapper.addSupportedProgram(programSupported);

    programSupportedMapper.delete(facility.getId(), program.getId());

    ProgramSupported programsSupported = programSupportedMapper.getBy(facility.getId(), program.getId());
    assertThat(programsSupported, is(nullValue()));
  }

  @Test
  public void shouldGetAllProgramsSupportedForFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);

    ProgramSupported programSupported = make(a(defaultProgramSupported,
      with(supportedFacilityId, facility.getId()),
      with(supportedProgram, program)));
    programSupportedMapper.addSupportedProgram(programSupported);

    List<ProgramSupported> programsSupported = programSupportedMapper.getAllByFacilityId(facility.getId());
    assertThat(programsSupported.size(), is(1));
    assertThat(programsSupported.get(0).getFacilityId(), is(programSupported.getFacilityId()));
    assertThat(programsSupported.get(0).getStartDate(), is(programSupported.getStartDate()));
    assertThat(programsSupported.get(0).getActive(), is(programSupported.getActive()));
    assertThat(programsSupported.get(0).getProgram().getId(),is(programSupported.getProgram().getId()));
    assertThat(programsSupported.get(0).getProgram().getCode(),is(programSupported.getProgram().getCode()));
    assertThat(programsSupported.get(0).getProgram().getName(),is(programSupported.getProgram().getName()));
  }
}
