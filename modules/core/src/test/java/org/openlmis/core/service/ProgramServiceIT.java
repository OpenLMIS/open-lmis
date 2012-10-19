package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.domain.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:/applicationContext-core.xml")
public class ProgramServiceIT extends SpringIntegrationTest {

    @Autowired
    private ProgramService programService;

    @Before
    public void setUp() {
        programService.removeAll();
    }

    @Test
    public void shouldInsertProgram() {
        Program program = new Program("TB", "TB Desc");

        programService.add(program);

        List<Program> programs = programService.getAll();
        assertEquals(1, programs.size());
        assertEquals("TB", programs.get(0).getName());
        assertEquals("TB Desc", programs.get(0).getDescription());
    }

}
