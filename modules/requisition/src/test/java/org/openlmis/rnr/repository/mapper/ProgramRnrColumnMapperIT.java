package org.openlmis.rnr.repository.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnRColumnSource;
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
    RnrColumnMapper rnrColumnMapper;

    @Before
    @After
    public void setUp() throws Exception {
        rnrColumnMapper.deleteAll();
    }

    @Test
    public void shouldInsertConfiguredDataForProgramColumn() throws Exception {
        RnrColumn rnrColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        addProgramRnrColumn(rnrColumn, 5, false, "Some Random Label", RnRColumnSource.USER_INPUT);

        List<RnrColumn> fetchedColumns = rnrColumnMapper.getAllRnrColumnsForProgram(HIV);
        assertThat(fetchedColumns.size(), is(1));
        assertThat(fetchedColumns.get(0).getLabel(), is("Some Random Label"));
        assertThat(fetchedColumns.get(0).isVisible(), is(false));
        assertThat(fetchedColumns.get(0).getPosition(), is(5));
        assertThat(fetchedColumns.get(0).getSource(), is(RnRColumnSource.USER_INPUT));
    }

    @Test
    public void shouldUpdateConfiguredDataForProgramColumn() throws Exception {
        RnrColumn rnrColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        addProgramRnrColumn(rnrColumn, 3, true, "Some Random Label", RnRColumnSource.USER_INPUT);
        updateProgramRnrColumn(rnrColumn.getId(), 5, false, "Some Random Label", RnRColumnSource.CALCULATED);

        RnrColumn updatedRnrColumn = rnrColumnMapper.getAllRnrColumnsForProgram(HIV).get(0);

        assertThat(updatedRnrColumn.getId(), is(rnrColumn.getId()));
        assertThat(updatedRnrColumn.isVisible(), is(false));
        assertThat(updatedRnrColumn.getPosition(), is(5));
        assertThat(updatedRnrColumn.getLabel(), is("Some Random Label"));
        assertThat(updatedRnrColumn.getSource(), is(RnRColumnSource.CALCULATED));
    }

    @Test
    public void shouldFetchColumnsInOrderOfVisibleAndPositionDefined() throws Exception {
        RnrColumn visibleColumn1 = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        addProgramRnrColumn(visibleColumn1, 4, true, "Some Random Label", RnRColumnSource.USER_INPUT);
        RnrColumn visibleColumn2 = rnrColumnMapper.fetchAllMasterRnRColumns().get(1);
        addProgramRnrColumn(visibleColumn2, 3, true, "Some Random Label", RnRColumnSource.USER_INPUT);
        RnrColumn notVisibleColumn1 = rnrColumnMapper.fetchAllMasterRnRColumns().get(2);
        addProgramRnrColumn(notVisibleColumn1, 2, false, "Some Random Label", RnRColumnSource.USER_INPUT);
        RnrColumn notVisibleColumn2 = rnrColumnMapper.fetchAllMasterRnRColumns().get(3);
        addProgramRnrColumn(notVisibleColumn2, 1, false, "Some Random Label", RnRColumnSource.USER_INPUT);

        List<RnrColumn> allRnrColumnsForProgram = rnrColumnMapper.getAllRnrColumnsForProgram(HIV);
        assertThat(allRnrColumnsForProgram.get(0), is(visibleColumn2));
        assertThat(allRnrColumnsForProgram.get(1), is(visibleColumn1));
        assertThat(allRnrColumnsForProgram.get(2), is(notVisibleColumn2));
        assertThat(allRnrColumnsForProgram.get(3), is(notVisibleColumn1));
    }

    @Test
    public void shouldRetrieveVisibleProgramRnrColumn() {
        RnrColumn visibleColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        RnrColumn inVisibleColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(1);
        addProgramRnrColumn(visibleColumn, 1, true, "Col1", RnRColumnSource.USER_INPUT);
        addProgramRnrColumn(inVisibleColumn, 2, false, "Col2", RnRColumnSource.USER_INPUT);

        List<RnrColumn> rnrColumns = rnrColumnMapper.getVisibleProgramRnrColumns(HIV);
        assertThat(rnrColumns.size(), is(1));
        assertThat(rnrColumns.get(0).getSource(), is(RnRColumnSource.USER_INPUT));
        assertThat(rnrColumns.get(0).isVisible(), is(true));
    }


    private int addProgramRnrColumn(RnrColumn rnrColumn, int position, boolean visible, String label, RnRColumnSource columnSource) {
        rnrColumn.setLabel(label);
        rnrColumn.setVisible(visible);
        rnrColumn.setPosition(position);
        rnrColumn.setSource(columnSource);
        return rnrColumnMapper.insert(HIV, rnrColumn);
    }

    private void updateProgramRnrColumn(Integer id, int position, boolean visible, String label, RnRColumnSource columnSource) {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setId(id);
        rnrColumn.setLabel(label);
        rnrColumn.setVisible(visible);
        rnrColumn.setPosition(position);
        rnrColumn.setSource(columnSource);
        rnrColumnMapper.update(HIV, rnrColumn);
    }
}
