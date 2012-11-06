package org.openlmis.core.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class ProgramMapperIT {

    @Autowired
    ProgramMapper programMapper;

    @Before
    public void setUp() {
    }

    @Test
    public void shouldGetAllProgram() {
        List<Program> programs = programMapper.getAll();
        assertEquals(2, programs.size());
        assertTrue(programs.contains(new Program(1, "ARV", "some ARV program")));
        assertTrue(programs.contains(new Program(2, "HIV", "some HIV program")));
    }

}
