package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.mapper.RnrColumnMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RnrTemplateRepositoryTest {

  public static final Integer EXISTING_PROGRAM_ID = 1;
  ProgramRnrTemplate template;

  RnrTemplateRepository rnrRepository;

  @Mock
  RnrColumnMapper rnrColumnMapper;

  private List<RnrColumn> rnrColumns = new ArrayList<>();


  @Before
  public void setUp() throws Exception {
    rnrRepository = new RnrTemplateRepository(rnrColumnMapper);
    template = new ProgramRnrTemplate(EXISTING_PROGRAM_ID, rnrColumns);
  }

  @Test
  public void shouldRetrieveAllColumnsFromMasterTable() throws Exception {
    when(rnrColumnMapper.isRnrTemplateDefined(EXISTING_PROGRAM_ID)).thenReturn(false);
    when(rnrColumnMapper.fetchAllMasterRnRColumns()).thenReturn(rnrColumns);
    List<RnrColumn> result = rnrRepository.fetchRnrTemplateColumns(EXISTING_PROGRAM_ID);

    assertThat(result, is(rnrColumns));
    verify(rnrColumnMapper).fetchAllMasterRnRColumns();
  }

  @Test
  public void shouldRetrieveAlreadyDefinedRnrColumnsForAProgram() throws Exception {
    when(rnrColumnMapper.isRnrTemplateDefined(EXISTING_PROGRAM_ID)).thenReturn(true);
    when(rnrColumnMapper.fetchDefinedRnrColumnsForProgram(EXISTING_PROGRAM_ID)).thenReturn(rnrColumns);
    List<RnrColumn> result = rnrRepository.fetchRnrTemplateColumns(EXISTING_PROGRAM_ID);
    assertThat(result, is(rnrColumns));
    verify(rnrColumnMapper, never()).fetchAllMasterRnRColumns();
    verify(rnrColumnMapper).fetchDefinedRnrColumnsForProgram(EXISTING_PROGRAM_ID);

  }

  @Test
  public void shouldInsertRnRColumnsForAProgram() throws Exception {
    rnrColumns.add(new RnrColumn());
    rnrColumns.add(new RnrColumn());
    rnrRepository.saveProgramRnrTemplate(template);
    verify(rnrColumnMapper, times(2)).insert(eq(EXISTING_PROGRAM_ID), any(RnrColumn.class));
  }

  @Test
  public void shouldUpdateRnRColumnsForAProgramIfRnrTemplateAlreadyDefined() throws Exception {
    when(rnrColumnMapper.isRnrTemplateDefined(EXISTING_PROGRAM_ID)).thenReturn(true);
    rnrColumns.add(new RnrColumn());
    rnrColumns.add(new RnrColumn());
    rnrRepository.saveProgramRnrTemplate(template);
    verify(rnrColumnMapper, times(2)).update(eq(EXISTING_PROGRAM_ID), any(RnrColumn.class));
  }

  @Test
  public void shouldReturnFormulaValidatedFlag() throws Exception {
    boolean expectedFormulaValidated = true;
    Integer programId = 1;
    Program program = new Program(programId);
    when(rnrColumnMapper.isFormulaValidationRequired(program)).thenReturn(expectedFormulaValidated);

    boolean formulaValidatedResult = rnrRepository.isFormulaValidationRequired(program);

    assertThat(formulaValidatedResult, is(expectedFormulaValidated));
  }
}
