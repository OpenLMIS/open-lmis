package org.openlmis.core.repository.mapper;

import org.joda.time.DateTime;
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

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProgramMapperIT extends SpringIntegrationTest {

    public static final String PROGRAM_CODE = "HIV";

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
        assertThat(programs, hasItem(new Program(PROGRAM_CODE, PROGRAM_CODE, PROGRAM_CODE, true)));
    }

    @Test
    public void shouldGetProgramsWhichAreActiveByFacilityCode() {
        Facility facility = make(a(FacilityBuilder.defaultFacility));
        int facilityId = facilityMapper.insert(facility);
        programSupportedMapper.addSupportedProgram(new ProgramSupported(facility.getCode(), PROGRAM_CODE, true, "test", DateTime.now().toDate()));

        List<Program> programs = programMapper.getActiveByFacility(facilityId);

        assertThat(programs.size(), is(1));
        assertThat(programs.get(0).getCode(), is(PROGRAM_CODE));
    }

    @Test
    public void shouldGetAllPrograms() throws Exception {
        Program program = make(a(ProgramBuilder.defaultProgram));
        program.setActive(false);
        programMapper.insert(program);
        List<Program> programs = programMapper.getAll();
        assertEquals(8, programs.size());
        assertTrue(programs.contains(program));
    }

    @Test
    public void shouldGetProgramsSupportedByFacility() throws Exception {
        Facility facility = make(a(defaultFacility));
        int facilityId = facilityMapper.insert(facility);
        programSupportedMapper.addSupportedProgram(new ProgramSupported(facility.getCode(),PROGRAM_CODE,true,facility.getModifiedBy(),facility.getModifiedDate()));
        List<Program> supportedPrograms = programMapper.getByFacilityId(facilityId);
        assertThat(supportedPrograms.get(0).getCode(), is(PROGRAM_CODE));
    }
}
