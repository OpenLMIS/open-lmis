/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.model.TemplateParameter;
import org.openlmis.reporting.repository.mapper.TemplateMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class TemplateRepositoryTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private TemplateMapper templateMapper;
  @InjectMocks
  private TemplateRepository templateRepository;

  @Test
  public void shouldInsertReportTemplateWithParameters() throws Exception {
    Template template = new Template();
    ArrayList<TemplateParameter> templateParameters = new ArrayList<>();
    TemplateParameter templateParameter1 = new TemplateParameter();
    templateParameters.add(templateParameter1);
    template.setParameters(templateParameters);

    templateRepository.insertWithParameters(template);

    verify(templateMapper).insert(template);
    verify(templateMapper).insertParameter(templateParameter1);
  }

  @Test
  public void shouldThrowDataExceptionIfReportWithSameNameAlreadyExists() throws Exception {
    Template template = new Template();
    doThrow(DataIntegrityViolationException.class).when(templateMapper).insert(template);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("unexpected.exception");
    templateRepository.insertWithParameters(template);
  }

  @Test
  public void shouldRunTemplateParameterSelectSqlAndSetValues() {
    final Template t = new Template();
    TemplateParameter tpWithSelectSql = new TemplateParameter();
    final String selectSql = "select name from something";
    tpWithSelectSql.setSelectSql(selectSql);
    TemplateParameter tpNoSelectSql = new TemplateParameter();
    t.setParameters(asList(tpWithSelectSql, tpNoSelectSql));

    final long id = 1L;
    final List<String> selectValues = asList("first", "second");
    when(templateMapper.getLWById(id)).thenReturn(t);
    when(templateMapper.runSelectSql(selectSql)).thenReturn(selectValues);

    Template tRet = templateRepository.getLWById(id);

    assertThat(tRet, is(notNullValue()));
    verify(templateMapper, atLeastOnce()).runSelectSql(selectSql);
    assertThat(tRet.getParameters().size(), is(2));
    assertThat(tpWithSelectSql.getSelectValues(), is(selectValues));
    assertThat(tpNoSelectSql.getSelectValues().size(), is(0));
  }
}
