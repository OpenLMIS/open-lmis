/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Program;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrColumnOption;
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

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ProgramRnrColumnMapperIT {

  public static final Long PROGRAM_ID = 1L;
  @Autowired
  ProgramRnrColumnMapper programRnrColumnMapper;

  @Test
  public void shouldInsertConfiguredDataForProgramColumn() throws Exception {
    RnrColumn rnrColumn = programRnrColumnMapper.fetchAllMasterRnRColumns().get(0);

    addProgramRnrColumn(rnrColumn, null, 5, false, "Some Random Label", USER_INPUT, true);

    List<RnrColumn> fetchedColumns = programRnrColumnMapper.fetchDefinedRnrColumnsForProgram(PROGRAM_ID);

    assertThat(fetchedColumns.size(), is(1));

    RnrColumn rnrColumn1 = fetchedColumns.get(0);

    assertThat(rnrColumn1.getLabel(), is("Some Random Label"));
    assertThat(rnrColumn1.getVisible(), is(false));
    assertThat(rnrColumn1.getPosition(), is(5));
    assertThat(rnrColumn1.getSource(), is(USER_INPUT));
    assertThat(rnrColumn1.isFormulaValidationRequired(), is(true));
  }

  @Test
  public void shouldReturnListOfRnrColumnOptionsByMasterRnrColmnId() {
    List<RnrColumnOption> rnrColumnOptions = programRnrColumnMapper.getRnrColumnOptionsByMasterRnrColumnId(
      11);

    assertThat(rnrColumnOptions.size(), is(2));
    assertThat(rnrColumnOptions.get(0).getName(), is("newPatientCount"));
    assertThat(rnrColumnOptions.get(1).getName(), is("dispensingUnitsForNewPatients"));
  }

  @Test
  public void shouldUpdateConfiguredDataForProgramColumn() throws Exception {
    RnrColumn rnrColumn = programRnrColumnMapper.fetchAllMasterRnRColumns().get(0);
    RnrColumnOption rnrColumnOption1 = programRnrColumnMapper.getRnrColumnOptionById(1);
    addProgramRnrColumn(rnrColumn, rnrColumnOption1, 3, true, "Some Random Label", USER_INPUT, false);

    RnrColumnOption rnrColumnOption2 = programRnrColumnMapper.getRnrColumnOptionById(2);
    updateProgramRnrColumn(rnrColumn.getId(), 5, false, "Some Random Label", RnRColumnSource.CALCULATED, true,
      rnrColumnOption2);

    RnrColumn updatedRnrColumn = programRnrColumnMapper.fetchDefinedRnrColumnsForProgram(PROGRAM_ID).get(0);

    assertThat(updatedRnrColumn.getId(), is(rnrColumn.getId()));
    assertThat(updatedRnrColumn.getVisible(), is(false));
    assertThat(updatedRnrColumn.getPosition(), is(5));
    assertThat(updatedRnrColumn.getLabel(), is("Some Random Label"));
    assertThat(updatedRnrColumn.getSource(), is(RnRColumnSource.CALCULATED));
    assertThat(updatedRnrColumn.isFormulaValidationRequired(), is(true));
    assertThat(updatedRnrColumn.getConfiguredOption(), is(rnrColumnOption2));
  }

  @Test
  public void shouldFetchColumnsInOrderOfVisibleAndPositionDefined() throws Exception {
    RnrColumn visibleColumn1 = programRnrColumnMapper.fetchAllMasterRnRColumns().get(0);
    addProgramRnrColumn(visibleColumn1, null, 4, true, "Some Random Label", USER_INPUT, true);
    RnrColumn visibleColumn2 = programRnrColumnMapper.fetchAllMasterRnRColumns().get(1);
    addProgramRnrColumn(visibleColumn2, null, 3, true, "Some Random Label", USER_INPUT, true);
    RnrColumn notVisibleColumn1 = programRnrColumnMapper.fetchAllMasterRnRColumns().get(2);
    addProgramRnrColumn(notVisibleColumn1, null, 2, false, "Some Random Label", USER_INPUT, true);
    RnrColumn notVisibleColumn2 = programRnrColumnMapper.fetchAllMasterRnRColumns().get(3);
    addProgramRnrColumn(notVisibleColumn2, null, 1, false, "Some Random Label", USER_INPUT, true);

    List<RnrColumn> allRnrColumnsForProgram = programRnrColumnMapper.fetchDefinedRnrColumnsForProgram(PROGRAM_ID);
    assertThat(allRnrColumnsForProgram.get(0), is(visibleColumn2));
    assertThat(allRnrColumnsForProgram.get(1), is(visibleColumn1));
    assertThat(allRnrColumnsForProgram.get(2), is(notVisibleColumn2));
    assertThat(allRnrColumnsForProgram.get(3), is(notVisibleColumn1));
  }

  @Test
  public void shouldFetchColumnsWithRnrOptionsFilled() {
    RnrColumn column1 = programRnrColumnMapper.fetchAllMasterRnRColumns().get(11);
    RnrColumnOption rnrColumnOption = programRnrColumnMapper.getRnrColumnOptionById(1);
    addProgramRnrColumn(column1, rnrColumnOption, 4, true, "Some Random Label", USER_INPUT, true);

    List<RnrColumn> allRnrColumnsForProgram = programRnrColumnMapper.fetchDefinedRnrColumnsForProgram(PROGRAM_ID);

    assertThat(allRnrColumnsForProgram.size(), is(1));
//TODO: revisit this code
//    RnrColumn patientCountColumn = allRnrColumnsForProgram.get(0);
//    assertThat(patientCountColumn.getConfiguredOption().getName(), is("newPatientCount"));
//    assertThat(patientCountColumn.getRnrColumnOptions().size(), is(2));
//    assertThat(patientCountColumn.getRnrColumnOptions().get(0).getName(), is("newPatientCount"));
//    assertThat(patientCountColumn.getRnrColumnOptions().get(1).getName(), is("dispensingUnitsForNewPatients"));
  }

  @Test
  public void shouldRetrieveVisibleProgramRnrColumn() {
    List<RnrColumn> allColumns = programRnrColumnMapper.fetchAllMasterRnRColumns();
    RnrColumn visibleColumn = allColumns.get(0);
    RnrColumn inVisibleColumn = allColumns.get(1);
    addProgramRnrColumn(visibleColumn, null, 1, true, "Col1", USER_INPUT, true);
    addProgramRnrColumn(inVisibleColumn, null, 2, false, "Col2", USER_INPUT, true);

    List<RnrColumn> rnrColumns = programRnrColumnMapper.getVisibleProgramRnrColumns(PROGRAM_ID);
    assertThat(rnrColumns.size(), is(1));
    assertThat(rnrColumns.get(0).getSource(), is(USER_INPUT));
    assertThat(rnrColumns.get(0).getVisible(), is(true));
    assertThat(rnrColumns.get(0).isFormulaValidationRequired(), is(true));
  }

  @Test
  public void shouldReturnFormulaValidatedTrueForProgramTemplate() throws Exception {
    List<RnrColumn> allColumns = programRnrColumnMapper.fetchAllMasterRnRColumns();
    RnrColumn quantityDispensed = allColumns.get(5);
    RnrColumn stockInHand = allColumns.get(7);
    addProgramRnrColumn(quantityDispensed, null, 1, true, "Col1", USER_INPUT, true);
    addProgramRnrColumn(stockInHand, null, 2, true, "Col2", CALCULATED, true);

    assertThat(programRnrColumnMapper.isFormulaValidationRequired(new Program(PROGRAM_ID)), is(true));

  }

  @Test
  public void shouldReturnFormulaValidatedFalseIfConditionsNotMet() throws Exception {
    List<RnrColumn> allColumns = programRnrColumnMapper.fetchAllMasterRnRColumns();
    RnrColumn quantityDispensed = allColumns.get(5);
    RnrColumn stockInHand = allColumns.get(7);
    addProgramRnrColumn(quantityDispensed, null, 1, true, "Col1", USER_INPUT, false);
    addProgramRnrColumn(stockInHand, null, 2, true, "Col2", CALCULATED, false);

    assertThat(programRnrColumnMapper.isFormulaValidationRequired(new Program(PROGRAM_ID)), is(false));

  }

  @Test
  public void shouldGetRnrColumnOptionById() {
    RnrColumnOption columnOption = programRnrColumnMapper.getRnrColumnOptionById(1);

    assertThat(columnOption.getId(), is(1L));
  }

  private int addProgramRnrColumn(RnrColumn rnrColumn,
                                  RnrColumnOption rnrColumnOption,
                                  int position,
                                  boolean visible,
                                  String label,
                                  RnRColumnSource columnSource,
                                  Boolean validated) {
    rnrColumn.setLabel(label);
    rnrColumn.setConfiguredOption(rnrColumnOption);
    rnrColumn.setVisible(visible);
    rnrColumn.setPosition(position);
    rnrColumn.setSource(columnSource);
    rnrColumn.setFormulaValidationRequired(validated);
    return programRnrColumnMapper.insert(PROGRAM_ID, rnrColumn);
  }

  private void updateProgramRnrColumn(Long id,
                                      int position,
                                      boolean visible,
                                      String label,
                                      RnRColumnSource columnSource,
                                      Boolean validated,
                                      RnrColumnOption rnrColumnOption) {
    RnrColumn rnrColumn = new RnrColumn();
    rnrColumn.setId(id);
    rnrColumn.setLabel(label);
    rnrColumn.setVisible(visible);
    rnrColumn.setPosition(position);
    rnrColumn.setSource(columnSource);
    rnrColumn.setFormulaValidationRequired(validated);
    rnrColumn.setConfiguredOption(rnrColumnOption);
    programRnrColumnMapper.update(PROGRAM_ID, rnrColumn);
  }


}
