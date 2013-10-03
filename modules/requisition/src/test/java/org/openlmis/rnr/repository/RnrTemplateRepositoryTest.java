/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.mapper.ProgramRnrColumnMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RnrTemplateRepositoryTest {

  public static final Long EXISTING_PROGRAM_ID = 1L;
  ProgramRnrTemplate template;

  @Mock
  ProgramRnrColumnMapper programRnrColumnMapper;
  @InjectMocks
  RnrTemplateRepository rnrRepository;

  private List<RnrColumn> rnrColumns = new ArrayList<>();


  @Before
  public void setUp() throws Exception {
    template = new ProgramRnrTemplate(EXISTING_PROGRAM_ID, rnrColumns);
  }

  @Test
  public void shouldRetrieveAllColumnsFromMasterTable() throws Exception {
    when(programRnrColumnMapper.isRnrTemplateDefined(EXISTING_PROGRAM_ID)).thenReturn(false);
    when(programRnrColumnMapper.fetchAllMasterRnRColumns()).thenReturn(rnrColumns);
    List<RnrColumn> result = rnrRepository.fetchRnrTemplateColumnsOrMasterColumns(EXISTING_PROGRAM_ID);

    assertThat(result, is(rnrColumns));
    verify(programRnrColumnMapper).fetchAllMasterRnRColumns();
  }

  @Test
  public void shouldRetrieveAlreadyDefinedRnrColumnsForAProgram() throws Exception {
    when(programRnrColumnMapper.isRnrTemplateDefined(EXISTING_PROGRAM_ID)).thenReturn(true);
    when(programRnrColumnMapper.fetchDefinedRnrColumnsForProgram(EXISTING_PROGRAM_ID)).thenReturn(rnrColumns);
    List<RnrColumn> result = rnrRepository.fetchRnrTemplateColumnsOrMasterColumns(EXISTING_PROGRAM_ID);
    assertThat(result, is(rnrColumns));
    verify(programRnrColumnMapper, never()).fetchAllMasterRnRColumns();
    verify(programRnrColumnMapper).fetchDefinedRnrColumnsForProgram(EXISTING_PROGRAM_ID);

  }

  @Test
  public void shouldInsertRnRColumnsForAProgram() throws Exception {
    rnrColumns.add(new RnrColumn());
    rnrColumns.add(new RnrColumn());
    rnrRepository.saveProgramRnrTemplate(template);
    verify(programRnrColumnMapper, times(2)).insert(eq(EXISTING_PROGRAM_ID), any(RnrColumn.class));
  }

  @Test
  public void shouldUpdateRnRColumnsForAProgramIfRnrTemplateAlreadyDefined() throws Exception {
    when(programRnrColumnMapper.isRnrTemplateDefined(EXISTING_PROGRAM_ID)).thenReturn(true);
    rnrColumns.add(new RnrColumn());
    rnrColumns.add(new RnrColumn());
    rnrRepository.saveProgramRnrTemplate(template);
    verify(programRnrColumnMapper, times(2)).update(eq(EXISTING_PROGRAM_ID), any(RnrColumn.class));
  }

  @Test
  public void shouldReturnFormulaValidatedFlag() throws Exception {
    boolean expectedFormulaValidated = true;
    Long programId = 1L;
    Program program = new Program(programId);
    when(programRnrColumnMapper.isFormulaValidationRequired(program)).thenReturn(expectedFormulaValidated);

    boolean formulaValidatedResult = rnrRepository.isFormulaValidationRequired(program);

    assertThat(formulaValidatedResult, is(expectedFormulaValidated));
  }
}
