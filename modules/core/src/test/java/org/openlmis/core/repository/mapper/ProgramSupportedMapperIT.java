package org.openlmis.core.repository.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ProgramSupported;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class ProgramSupportedMapperIT {

    public static final String YELLOW_FEVER = "YELL_FVR";

    @Autowired
    ProgramMapper programMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Autowired
    ProgramSupportedMapper programSupportedMapper;

    @Before
    public void setUp() throws Exception {
        programSupportedMapper.deleteAll();
        facilityMapper.deleteAll();

        facilityMapper.insert(make(a(defaultFacility)));
        programMapper.insert(make(a(program, with(code, YELLOW_FEVER))));
    }

    @Test
    public void shouldSaveProgramSupported() throws Exception {
        ProgramSupported programSupported = new ProgramSupported(FACILITY_CODE, YELLOW_FEVER, true, "user", now().toDate());

        programSupportedMapper.addSupportedProgram(programSupported);

        List<ProgramSupported> programsSupported = programSupportedMapper.getBy(FACILITY_CODE, YELLOW_FEVER);
        assertThat(programSupported, is(programsSupported.get(0)));
    }

    @After
    public void tearDown() throws Exception {
        programSupportedMapper.deleteAll();
        facilityMapper.deleteAll();
        programMapper.delete(YELLOW_FEVER);
    }

}
