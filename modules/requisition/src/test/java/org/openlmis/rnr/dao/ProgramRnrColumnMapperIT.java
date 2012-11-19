package org.openlmis.rnr.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
public class ProgramRnrColumnMapperIT {

    @Autowired
    ProgramRnrColumnMapper programRnrColumnMapper;

    @Autowired
    RnrColumnMapper rnrColumnMapper;

    @Before
    public void setUp() throws Exception {
        programRnrColumnMapper.deleteAll();
    }

    @Test
    public void shouldReturnLabelFromProgramTemplate() throws Exception {
        RnrColumn rnrColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        rnrColumn.setLabel("Some Random Label");
        programRnrColumnMapper.insert(1, rnrColumn);

        List<RnrColumn> fetchedColumns = programRnrColumnMapper.getAllRnrColumnsForProgram(1);
        assertThat(fetchedColumns.size(), is(1));
        assertThat(fetchedColumns.get(0).getLabel(), is("Some Random Label"));
    }

    @Test
    public void shouldSaveLabelAndUsedFlag() throws Exception {
        int programId = 1;
        RnrColumn rnrColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        programRnrColumnMapper.insert(programId, rnrColumn);

        RnrColumn newRnrColumn = new RnrColumn();
        newRnrColumn.setId(rnrColumn.getId());
        newRnrColumn.setLabel("Some Random Label");
        newRnrColumn.setUsed(false);
        programRnrColumnMapper.update(programId, newRnrColumn);


        RnrColumn updatedRnrColumn = programRnrColumnMapper.getAllRnrColumnsForProgram(programId).get(0);
        assertThat(updatedRnrColumn.getId(), is(newRnrColumn.getId()));
        assertThat(updatedRnrColumn.isUsed(), is(false));
        assertThat(updatedRnrColumn.getLabel(), is("Some Random Label"));
    }
}
