package org.openlmis.rnr.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Program;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.domain.RnRColumnSource.CALCULATED;
import static org.openlmis.rnr.domain.RnRColumnSource.USER_INPUT;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProgramRnrColumnMapperIT {

  public static final int PROGRAM_ID = 1;
  @Autowired
  RnrColumnMapper rnrColumnMapper;

  @Test
  public void shouldInsertConfiguredDataForProgramColumn() throws Exception {
    RnrColumn rnrColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);

    addProgramRnrColumn(rnrColumn, 5, false, "Some Random Label", USER_INPUT, true);

    List<RnrColumn> fetchedColumns = rnrColumnMapper.fetchDefinedRnrColumnsForProgram(PROGRAM_ID);

    assertThat(fetchedColumns.size(), is(1));

    RnrColumn rnrColumn1 = fetchedColumns.get(0);

    assertThat(rnrColumn1.getLabel(), is("Some Random Label"));
    assertThat(rnrColumn1.isVisible(), is(false));
    assertThat(rnrColumn1.getPosition(), is(5));
    assertThat(rnrColumn1.getSource(), is(USER_INPUT));
    assertThat(rnrColumn1.isFormulaValidationRequired(), is(true));
  }

  @Test
  public void shouldUpdateConfiguredDataForProgramColumn() throws Exception {
    RnrColumn rnrColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
    addProgramRnrColumn(rnrColumn, 3, true, "Some Random Label", USER_INPUT, false);
    updateProgramRnrColumn(rnrColumn.getId(), 5, false, "Some Random Label", RnRColumnSource.CALCULATED, true);

    RnrColumn updatedRnrColumn = rnrColumnMapper.fetchDefinedRnrColumnsForProgram(PROGRAM_ID).get(0);

    assertThat(updatedRnrColumn.getId(), is(rnrColumn.getId()));
    assertThat(updatedRnrColumn.isVisible(), is(false));
    assertThat(updatedRnrColumn.getPosition(), is(5));
    assertThat(updatedRnrColumn.getLabel(), is("Some Random Label"));
    assertThat(updatedRnrColumn.getSource(), is(RnRColumnSource.CALCULATED));
    assertThat(updatedRnrColumn.isFormulaValidationRequired(), is(true));
  }

  @Test
  public void shouldFetchColumnsInOrderOfVisibleAndPositionDefined() throws Exception {
    RnrColumn visibleColumn1 = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);
    addProgramRnrColumn(visibleColumn1, 4, true, "Some Random Label", USER_INPUT, true);
    RnrColumn visibleColumn2 = rnrColumnMapper.fetchAllMasterRnRColumns().get(1);
    addProgramRnrColumn(visibleColumn2, 3, true, "Some Random Label", USER_INPUT, true);
    RnrColumn notVisibleColumn1 = rnrColumnMapper.fetchAllMasterRnRColumns().get(2);
    addProgramRnrColumn(notVisibleColumn1, 2, false, "Some Random Label", USER_INPUT, true);
    RnrColumn notVisibleColumn2 = rnrColumnMapper.fetchAllMasterRnRColumns().get(3);
    addProgramRnrColumn(notVisibleColumn2, 1, false, "Some Random Label", USER_INPUT, true);

    List<RnrColumn> allRnrColumnsForProgram = rnrColumnMapper.fetchDefinedRnrColumnsForProgram(PROGRAM_ID);
    assertThat(allRnrColumnsForProgram.get(0), is(visibleColumn2));
    assertThat(allRnrColumnsForProgram.get(1), is(visibleColumn1));
    assertThat(allRnrColumnsForProgram.get(2), is(notVisibleColumn2));
    assertThat(allRnrColumnsForProgram.get(3), is(notVisibleColumn1));
  }

  @Test
  public void shouldRetrieveVisibleProgramRnrColumn() {
    List<RnrColumn> allColumns = rnrColumnMapper.fetchAllMasterRnRColumns();
    RnrColumn visibleColumn = allColumns.get(0);
    RnrColumn inVisibleColumn = allColumns.get(1);
    addProgramRnrColumn(visibleColumn, 1, true, "Col1", USER_INPUT, true);
    addProgramRnrColumn(inVisibleColumn, 2, false, "Col2", USER_INPUT, true);

    List<RnrColumn> rnrColumns = rnrColumnMapper.getVisibleProgramRnrColumns(PROGRAM_ID);
    assertThat(rnrColumns.size(), is(1));
    assertThat(rnrColumns.get(0).getSource(), is(USER_INPUT));
    assertThat(rnrColumns.get(0).isVisible(), is(true));
    assertThat(rnrColumns.get(0).isFormulaValidationRequired(), is(true));
  }

  @Test
  public void shouldReturnFormulaValidatedTrueForProgramTemplate() throws Exception {
    List<RnrColumn> allColumns = rnrColumnMapper.fetchAllMasterRnRColumns();
    RnrColumn quantityDispensed = allColumns.get(5);
    RnrColumn stockInHand = allColumns.get(7);
    addProgramRnrColumn(quantityDispensed, 1, true, "Col1", USER_INPUT, true);
    addProgramRnrColumn(stockInHand, 2, true, "Col2", CALCULATED, true);

    assertThat(rnrColumnMapper.isFormulaValidationRequired(new Program(PROGRAM_ID)), is(true));

  }

  @Test
  public void shouldReturnFormulaValidatedFalseIfConditionsNotMet() throws Exception {
    List<RnrColumn> allColumns = rnrColumnMapper.fetchAllMasterRnRColumns();
    RnrColumn quantityDispensed = allColumns.get(5);
    RnrColumn stockInHand = allColumns.get(7);
    addProgramRnrColumn(quantityDispensed, 1, true, "Col1", USER_INPUT, false);
    addProgramRnrColumn(stockInHand, 2, true, "Col2", CALCULATED, false);

    assertThat(rnrColumnMapper.isFormulaValidationRequired(new Program(PROGRAM_ID)), is(false));

  }

  private int addProgramRnrColumn(RnrColumn rnrColumn, int position, boolean visible, String label, RnRColumnSource columnSource, Boolean validated) {
    rnrColumn.setLabel(label);
    rnrColumn.setVisible(visible);
    rnrColumn.setPosition(position);
    rnrColumn.setSource(columnSource);
    rnrColumn.setFormulaValidationRequired(validated);
    return rnrColumnMapper.insert(PROGRAM_ID, rnrColumn);
  }

  private void updateProgramRnrColumn(Integer id, int position, boolean visible, String label, RnRColumnSource columnSource, Boolean validated) {
    RnrColumn rnrColumn = new RnrColumn();
    rnrColumn.setId(id);
    rnrColumn.setLabel(label);
    rnrColumn.setVisible(visible);
    rnrColumn.setPosition(position);
    rnrColumn.setSource(columnSource);
    rnrColumn.setFormulaValidationRequired(validated);
    rnrColumnMapper.update(PROGRAM_ID, rnrColumn);
  }


}
