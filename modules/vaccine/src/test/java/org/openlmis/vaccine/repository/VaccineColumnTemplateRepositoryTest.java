package org.openlmis.vaccine.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.LogisticsColumn;
import org.openlmis.vaccine.repository.mapper.VaccineColumnTemplateMapper;

import java.util.List;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineColumnTemplateRepositoryTest {

  @Mock
  VaccineColumnTemplateMapper mapper;

  @InjectMocks
  VaccineColumnTemplateRepository repository;

  @Test
  public void shouldGetMasterColumns() throws Exception {
    repository.getMasterColumns();
    verify(mapper).getAllMasterColumns();
  }

  @Test
  public void shouldGetTemplateForProgram() throws Exception {
    repository.getTemplateForProgram(2L);
    verify(mapper).getForProgram(2L);
  }

  @Test
  public void shouldUpdateProgramColumn() throws Exception {
    LogisticsColumn column = new LogisticsColumn();
    repository.updateProgramColumn(column);
    verify(mapper).updateProgramColumn(column);
  }

  @Test
  public void shouldInsertProgramColumn() throws Exception {
    LogisticsColumn column = new LogisticsColumn();
    column.setId(2L);
    repository.updateProgramColumn(column);
    verify(mapper).updateProgramColumn(column);
  }
}