package org.openlmis.core.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
public class ProgramMapperIT {

    @Autowired
    ProgramMapper programMapper;

    @Before
    public void setUp() {
        programMapper.deleteAll();
    }

    @Test
    public void shouldInsertProgram() {
        int id = programMapper.insert(new Program(999, "program", "description"));
        assertNotNull(id);
        Program program = programMapper.selectAll().get(0);
        assertEquals(999, program.getId().intValue());
        assertEquals("program", program.getName());
        assertEquals("description", program.getDescription());
    }

}
