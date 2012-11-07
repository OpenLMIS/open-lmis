package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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
    public void shouldGetAllProgram() {
        List<Program> programs = programMapper.getAll();
        assertEquals(3, programs.size());
        assertTrue(programs.contains(new Program(1, "ARV", "some ARV program", true)));
        assertTrue(programs.contains(new Program(2, "HIV", "some HIV program", true)));
        assertTrue(programs.contains(new Program(3, "INACTIVE", "some inactive program", false)));
    }

    @Test
    public void shouldGetProgramsByFacilityId() {
        Facility facility = new FacilityBuilder().withDefaults().withCode("blah").build();
        facilityMapper.insert(facility);
        int programId = 1;
        int status = facilityMapper.map(facility.getCode(), programId, true);
        List<Program> programs = programMapper.getByFacilityCode("blah");
        assertThat(programs.size(), is(1));
        assertThat(programs.get(0).getId(), is(programId));
        assertThat(status, is(1));
    }


}
