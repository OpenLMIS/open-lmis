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

    public static final String HIV = "HIV";
    @Autowired
    ProgramRnrColumnMapper programRnrColumnMapper;

    @Autowired
    RnrColumnMapper rnrColumnMapper;

    @Before
    public void setUp() throws Exception {
        programRnrColumnMapper.deleteAll();
    }

    @Test
    public void shouldInsertConfiguredDataForProgramColumn() throws Exception {
        RnrColumn rnrColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        rnrColumn.setLabel("Some Random Label");
        rnrColumn.setPosition(5);
        rnrColumn.setVisible(false);
        programRnrColumnMapper.insert(HIV, rnrColumn);

        List<RnrColumn> fetchedColumns = programRnrColumnMapper.getAllRnrColumnsForProgram(HIV);
        assertThat(fetchedColumns.size(), is(1));
        assertThat(fetchedColumns.get(0).getLabel(), is("Some Random Label"));
        assertThat(fetchedColumns.get(0).isVisible(), is(false));
        assertThat(fetchedColumns.get(0).getPosition(), is(5));
    }

    @Test
    public void shouldUpdateConfiguredDataForProgramColumn() throws Exception {
        RnrColumn rnrColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        programRnrColumnMapper.insert(HIV, rnrColumn);

        RnrColumn newRnrColumn = new RnrColumn();
        newRnrColumn.setId(rnrColumn.getId());
        newRnrColumn.setLabel("Some Random Label");
        newRnrColumn.setVisible(false);
        newRnrColumn.setPosition(5);
        programRnrColumnMapper.update(HIV, newRnrColumn);

        RnrColumn updatedRnrColumn = programRnrColumnMapper.getAllRnrColumnsForProgram(HIV).get(0);
        assertThat(updatedRnrColumn.getId(), is(newRnrColumn.getId()));
        assertThat(updatedRnrColumn.isVisible(), is(false));
        assertThat(updatedRnrColumn.getPosition(), is(5));
        assertThat(updatedRnrColumn.getLabel(), is("Some Random Label"));
    }

    @Test
    public void shouldFetchColumnsInOrderOfPositionDefined() throws Exception {
        RnrColumn rnrColumn1 = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        rnrColumn1.setPosition(2);
        programRnrColumnMapper.insert(HIV, rnrColumn1);

        RnrColumn rnrColumn2 = rnrColumnMapper.fetchAllMasterRnRColumns().get(1);
        rnrColumn2.setPosition(1);
        programRnrColumnMapper.insert(HIV, rnrColumn2);

        List<RnrColumn> allRnrColumnsForProgram = programRnrColumnMapper.getAllRnrColumnsForProgram(HIV);
        assertThat(allRnrColumnsForProgram.get(0), is(rnrColumn2));
        assertThat(allRnrColumnsForProgram.get(1), is(rnrColumn1));

    }
}
