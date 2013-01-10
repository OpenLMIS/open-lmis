package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.*;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ProgramSupportedMapperIT {

  public static final String YELLOW_FEVER = "YELL_FVR";

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  ProgramSupportedMapper programSupportedMapper;

  @Autowired
  private RoleRightsMapper roleRightsMapper;

  @Autowired
  private RoleAssignmentMapper roleAssignmentMapper;

  @Autowired
  private UserMapper userMapper;

  @Test
  public void shouldSaveProgramSupported() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);
    ProgramSupported programSupported = make(a(defaultProgramSupported,
        with(supportedFacilityId, facility.getId()),
        with(supportedProgramId, program.getId())));
    programSupportedMapper.addSupportedProgram(programSupported);

    List<ProgramSupported> programsSupported = programSupportedMapper.getBy(facility.getId(), program.getId());
    ProgramSupported result = programsSupported.get(0);
    assertThat(result.getFacilityId(), is(facility.getId()));
    assertThat(result.getProgramId(), is(program.getId()));
  }

  @Test
  public void shouldDeleteProgramMapping() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    Program program = make(a(defaultProgram, with(programCode, YELLOW_FEVER)));
    programMapper.insert(program);
    ProgramSupported programSupported = new ProgramSupported(facility.getId(), program.getId(), true, "user", now().toDate());
    programSupportedMapper.addSupportedProgram(programSupported);

    programSupportedMapper.delete(facility.getId(), program.getId());

    List<ProgramSupported> programsSupported = programSupportedMapper.getBy(facility.getId(), program.getId());
    assertFalse(programsSupported.contains(programSupported));
  }
}
