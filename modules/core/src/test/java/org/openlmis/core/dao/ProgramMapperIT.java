package org.openlmis.core.dao;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class ProgramMapperIT extends SpringIntegrationTest {

    @Autowired
    ProgramMapper programMapper;

    @Before
    public void setUp() {
    }

    @Test
    public void shouldGetAllProgram() {
        List<Program> programs = programMapper.getAll();
        assertEquals(3, programs.size());
        assertTrue(programs.contains(new Program(1, "ARV", "some ARV program", true)));
        assertTrue(programs.contains(new Program(2, "HIV", "some HIV program", true)));
        assertTrue(programs.contains(new Program(3, "INACTIVE", "some inactive program", false)));
    }

}
