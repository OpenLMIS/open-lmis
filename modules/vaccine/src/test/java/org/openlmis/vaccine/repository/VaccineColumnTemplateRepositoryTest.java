/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
    repository.insertProgramColumn(column);
    verify(mapper).insertProgramColumn(column);
  }
}