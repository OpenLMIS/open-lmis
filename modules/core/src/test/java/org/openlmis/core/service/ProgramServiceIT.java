package org.openlmis.core.service;

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

    @Test
    public void shouldGetAllPrograms() {
        List<Program> programs = programService.getAll();
        assertEquals(2, programs.size());
        assertEquals("ARV", programs.get(0).getName());
        assertEquals("some ARV program", programs.get(0).getDescription());
    }
}
