package org.openlmis.core.repository.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.openlmis.core.builder.FacilityBuilder.*;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class ProgramMapperIT extends SpringIntegrationTest {

    @Autowired
    ProgramMapper programMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Before
    public void setUp() {
        facilityMapper.deleteProgramMappings();
        facilityMapper.deleteAll();
    }

    @Test
    public void shouldGetAllActiveProgram() {
        List<Program> programs = programMapper.getAllActive();
        assertEquals(6, programs.size());
        assertTrue("program does not exists", programs.contains(new Program(1, "HIV", "HIV", true)));
    }

    @Test
    public void shouldGetProgramsWhichAreActiveByFacilityId() {
        Facility facility = make(a(defaultFacility));
        facilityMapper.insert(facility);
        int programId = 1;
        int status = facilityMapper.map(facility.getCode(), programId, true);
        List<Program> programs = programMapper.getActiveByFacilityCode("F10010");
        assertThat(programs.size(), is(1));
        assertThat(programs.get(0).getId(), is(programId));
        assertThat(status, is(1));
    }

    @After
    public void tearDown() throws Exception {
        Facility facility = make(a(defaultFacility,
                with(code, "DDM001"),
                with(name, "Dodoma Hospital"),
                with(type, 2)));
        facilityMapper.insert(facility);
        facilityMapper.map(facility.getCode(),5,true);
    }


}
