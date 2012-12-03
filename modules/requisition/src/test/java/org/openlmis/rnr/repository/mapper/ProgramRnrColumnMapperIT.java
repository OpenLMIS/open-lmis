package org.openlmis.rnr.repository.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrColumnType;
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

    @Before
    @After
    public void setUp() throws Exception {
        programRnrColumnMapper.deleteAll();
    }

    @Test
    public void shouldInsertConfiguredDataForProgramColumn() throws Exception {
        RnrColumn rnrColumn = programRnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        addProgramRnrColumn(rnrColumn, 5, false, "Some Random Label", RnrColumnType.User_Input);

        List<RnrColumn> fetchedColumns = programRnrColumnMapper.getAllRnrColumnsForProgram(HIV);
        assertThat(fetchedColumns.size(), is(1));
        assertThat(fetchedColumns.get(0).getLabel(), is("Some Random Label"));
        assertThat(fetchedColumns.get(0).isVisible(), is(false));
        assertThat(fetchedColumns.get(0).getPosition(), is(5));
        assertThat(fetchedColumns.get(0).getSelectedColumnType(), is(RnrColumnType.User_Input));
    }

    @Test
    public void shouldUpdateConfiguredDataForProgramColumn() throws Exception {
        RnrColumn rnrColumn = programRnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        addProgramRnrColumn(rnrColumn, 3, true, "Some Random Label", RnrColumnType.User_Input);
        updateProgramRnrColumn(rnrColumn.getId(), 5, false, "Some Random Label", RnrColumnType.Calculated);

        RnrColumn updatedRnrColumn = programRnrColumnMapper.getAllRnrColumnsForProgram(HIV).get(0);

        assertThat(updatedRnrColumn.getId(), is(rnrColumn.getId()));
        assertThat(updatedRnrColumn.isVisible(), is(false));
        assertThat(updatedRnrColumn.getPosition(), is(5));
        assertThat(updatedRnrColumn.getLabel(), is("Some Random Label"));
        assertThat(updatedRnrColumn.getSelectedColumnType(), is(RnrColumnType.Calculated));
    }

    @Test
    public void shouldFetchColumnsInOrderOfVisibleAndPositionDefined() throws Exception {
        RnrColumn visibleColumn1 = programRnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        addProgramRnrColumn(visibleColumn1, 4, true, "Some Random Label", RnrColumnType.User_Input);
        RnrColumn visibleColumn2 = programRnrColumnMapper.fetchAllMasterRnRColumns().get(1);
        addProgramRnrColumn(visibleColumn2, 3, true, "Some Random Label", RnrColumnType.User_Input);
        RnrColumn notVisibleColumn1 = programRnrColumnMapper.fetchAllMasterRnRColumns().get(2);
        addProgramRnrColumn(notVisibleColumn1, 2, false, "Some Random Label", RnrColumnType.User_Input);
        RnrColumn notVisibleColumn2 = programRnrColumnMapper.fetchAllMasterRnRColumns().get(3);
        addProgramRnrColumn(notVisibleColumn2, 1, false, "Some Random Label", RnrColumnType.User_Input);

        List<RnrColumn> allRnrColumnsForProgram = programRnrColumnMapper.getAllRnrColumnsForProgram(HIV);
        assertThat(allRnrColumnsForProgram.get(0), is(visibleColumn2));
        assertThat(allRnrColumnsForProgram.get(1), is(visibleColumn1));
        assertThat(allRnrColumnsForProgram.get(2), is(notVisibleColumn2));
        assertThat(allRnrColumnsForProgram.get(3), is(notVisibleColumn1));
    }

    @Test
    public void shouldRetrieveVisibleProgramRnrColumn() {
        RnrColumn visibleColumn = programRnrColumnMapper.fetchAllMasterRnRColumns().get(0);
        RnrColumn inVisibleColumn = programRnrColumnMapper.fetchAllMasterRnRColumns().get(1);
        addProgramRnrColumn(visibleColumn, 1, true, "Col1", RnrColumnType.User_Input);
        addProgramRnrColumn(inVisibleColumn, 2, false, "Col2", RnrColumnType.User_Input);

        List<RnrColumn> rnrColumns = programRnrColumnMapper.getVisibleProgramRnrColumns(HIV);
        assertThat(rnrColumns.size(), is(1));
        assertThat(rnrColumns.get(0).getSelectedColumnType(), is(RnrColumnType.User_Input));
        assertThat(rnrColumns.get(0).isVisible(), is(true));
    }


    private int addProgramRnrColumn(RnrColumn rnrColumn, int position, boolean visible, String label, RnrColumnType columnType) {
        rnrColumn.setLabel(label);
        rnrColumn.setVisible(visible);
        rnrColumn.setPosition(position);
        rnrColumn.setSelectedColumnType(columnType);
        return programRnrColumnMapper.insert(HIV, rnrColumn);
    }

    private void updateProgramRnrColumn(Integer id, int position, boolean visible, String label, RnrColumnType columnType) {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setId(id);
        rnrColumn.setLabel(label);
        rnrColumn.setVisible(visible);
        rnrColumn.setPosition(position);
        rnrColumn.setSelectedColumnType(columnType);
        programRnrColumnMapper.update(HIV, rnrColumn);
    }
}
