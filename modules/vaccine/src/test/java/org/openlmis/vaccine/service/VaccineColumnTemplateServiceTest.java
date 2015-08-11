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

package org.openlmis.vaccine.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.LogisticsColumn;
import org.openlmis.vaccine.repository.VaccineColumnTemplateRepository;

import java.util.ArrayList;
import java.util.List;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineColumnTemplateServiceTest {

  @Mock
  VaccineColumnTemplateRepository repository;

  @InjectMocks
  VaccineColumnTemplateService service;

  @Test
  public void shouldGetTemplateForNewProgram() throws Exception {
    List<LogisticsColumn> masterColumns = new ArrayList<LogisticsColumn>();
    masterColumns.add(new LogisticsColumn());
    List<LogisticsColumn> emptyList = new ArrayList<>();
    when(repository.getTemplateForProgram(1L)).thenReturn(emptyList);
    when(repository.getMasterColumns()).thenReturn(masterColumns);

    List<LogisticsColumn> columns =  service.getTemplate(1L);
    assertThat(columns.size(), is(1));
    assertThat(columns.get(0).getProgramId(), is(1L));
  }

  @Test
  public void shouldGetTemplateForConfiguredProgram() throws Exception {
    List<LogisticsColumn> configuredColumns = new ArrayList<LogisticsColumn>();
    configuredColumns.add(new LogisticsColumn());
    List<LogisticsColumn> emptyList = new ArrayList<>();
    when(repository.getTemplateForProgram(1L)).thenReturn(configuredColumns);

    List<LogisticsColumn> columns =  service.getTemplate(1L);
    assertThat(columns.size(), is(1));
  }

  @Test
  public void shouldSaveChangesWhenNewItemIsSaved() throws Exception {
    List<LogisticsColumn> columns = new ArrayList<>();
    columns.add(new LogisticsColumn());
    doNothing().when(repository).insertProgramColumn(columns.get(0));

    service.saveChanges(columns);
    verify(repository).insertProgramColumn(columns.get(0));
  }

  @Test
  public void shouldSaveChangesWhenExistingRecordIsEdited() throws Exception {
    List<LogisticsColumn> columns = new ArrayList<>();
    columns.add(new LogisticsColumn());
    columns.get(0).setId(1L);
    doNothing().when(repository).updateProgramColumn(columns.get(0));

    service.saveChanges(columns);
    verify(repository).updateProgramColumn(columns.get(0));
  }
}