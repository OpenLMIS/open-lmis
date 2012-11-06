package org.openlmis.rnr.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.dao.ProgramMapper;
import org.openlmis.core.domain.Program;
import org.openlmis.rnr.domain.ProgramRnrColumn;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
public class ProgramRnrColumnMapperIT {

    public static final int PROGRAM_ID = 999;
    @Autowired
    RnRColumnMapper rnRColumnMapper;
    @Autowired
    ProgramRnRColumnMapper programRnRColumnMapper;
    @Autowired
    ProgramMapper programMapper;

    RnrColumn rnrColumn;

    @Before
    public void setUp() throws Exception {
        programMapper.deleteAll();
        programRnRColumnMapper.deleteAll();

        programMapper.insert(new Program(PROGRAM_ID, "program", "description"));
        rnrColumn = rnRColumnMapper.fetchAllMasterRnRColumns().get(0);
    }

    @Test
    public void shouldInsertRnRColumnForProgram() throws Exception {
        rnrColumn.setUsed(true);
        int resultCode = programRnRColumnMapper.insert(PROGRAM_ID, rnrColumn);

        assertEquals(1, resultCode);
        ProgramRnrColumn savedProgramRnrColumn = programRnRColumnMapper.get(PROGRAM_ID, rnrColumn.getId());
        assertEquals(PROGRAM_ID, savedProgramRnrColumn.getProgramId().intValue());
        assertEquals(rnrColumn.getId(), savedProgramRnrColumn.getColumnId());
        assertEquals(true, savedProgramRnrColumn.isUsed());
    }

}
