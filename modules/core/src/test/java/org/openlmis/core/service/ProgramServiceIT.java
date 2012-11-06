package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.dao.ProgramMapper;
import org.openlmis.core.domain.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:/applicationContext-core.xml")
public class ProgramServiceIT extends SpringIntegrationTest {

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramMapper programMapper;

    @Before
    public void setUp() {
        programMapper.deleteAll();
    }

    @Test
    public void shouldGetAllPrograms() {
        Program arv = new Program(1, "ARV", "ARV is a disease");
        Program hiv = new Program(2, "HIV", "HIV is a disease");

        programMapper.insert(arv);
        programMapper.insert(hiv);

        List<Program> programs = programService.getAll();

        assertEquals(2, programs.size());
        assertEquals(arv, programs.get(0));
        assertEquals(hiv, programs.get(1));
    }
}
