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
    public void shouldFetchColumnsInOrderOfVisibleAndPositionDefined() throws Exception {
        RnrColumn visibleColumn1 = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        visibleColumn1.setPosition(4);
        visibleColumn1.setVisible(true);
        programRnrColumnMapper.insert(HIV, visibleColumn1);

        RnrColumn visibleColumn2 = rnrColumnMapper.fetchAllMasterRnRColumns().get(1);
        visibleColumn2.setPosition(3);
        visibleColumn2.setVisible(true);
        programRnrColumnMapper.insert(HIV, visibleColumn2);

        RnrColumn notVisibleColumn1 = rnrColumnMapper.fetchAllMasterRnRColumns().get(2);
        notVisibleColumn1.setVisible(false);
        notVisibleColumn1.setPosition(2);
        programRnrColumnMapper.insert(HIV, notVisibleColumn1);

        RnrColumn notVisibleColumn2 = rnrColumnMapper.fetchAllMasterRnRColumns().get(3);
        notVisibleColumn2.setPosition(1);
        notVisibleColumn2.setVisible(false);
        programRnrColumnMapper.insert(HIV, notVisibleColumn2);

        List<RnrColumn> allRnrColumnsForProgram = programRnrColumnMapper.getAllRnrColumnsForProgram(HIV);
        assertThat(allRnrColumnsForProgram.get(0), is(visibleColumn2));
        assertThat(allRnrColumnsForProgram.get(1), is(visibleColumn1));
        assertThat(allRnrColumnsForProgram.get(2), is(notVisibleColumn2));
        assertThat(allRnrColumnsForProgram.get(3), is(notVisibleColumn1));

    }
}
