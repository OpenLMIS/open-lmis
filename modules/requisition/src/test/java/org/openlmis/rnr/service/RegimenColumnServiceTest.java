/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.RegimenColumn;
import org.openlmis.rnr.domain.RegimenTemplate;
import org.openlmis.rnr.repository.RegimenColumnRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.builder.RegimenColumnBuilder.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest(RegimenColumnService.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
public class RegimenColumnServiceTest {

  @Mock
  RegimenColumnRepository repository;

  @Mock
  MessageService messageService;

  @Mock
  ProgramService programService;

  @InjectMocks
  RegimenColumnService service;

  Long userId = 1L;

  @Test
  public void shouldCallSaveAndSetRegimenTemplateConfigured() throws Exception {

    Long programId = 1L;
    RegimenTemplate regimenTemplate = new RegimenTemplate(programId, new ArrayList<RegimenColumn>());

    service.save(regimenTemplate, userId);

    verify(repository).save(regimenTemplate, userId);
    verify(programService).setRegimenTemplateConfigured(programId);
  }

  @Test
  public void shouldGetRegimenColumnsByProgramId() throws Exception {
    Long programId = 1L;
    RegimenColumn regimenColumn1 = new RegimenColumn(programId, "testName1", "testLabel1", "numeric", Boolean.TRUE,1, 1L);
    RegimenColumn regimenColumn2 = new RegimenColumn(programId, "testName2", "testLabel2", "numeric", Boolean.TRUE,2, 1L);

    when(repository.getRegimenColumnsByProgramId(programId)).thenReturn(asList(regimenColumn1, regimenColumn2));

    List<RegimenColumn> resultColumns = service.getRegimenColumnsByProgramId(programId);

    verify(repository).getRegimenColumnsByProgramId(programId);
    assertThat(resultColumns.size(), is(2));
    assertThat(resultColumns.get(0), is(regimenColumn1));
    assertThat(resultColumns.get(1), is(regimenColumn2));
  }

  @Test
  public void shouldGetRegimenTemplateForProgram() throws Exception {
    ArrayList<RegimenColumn> regimenColumns = new ArrayList<RegimenColumn>() {{
      add(new RegimenColumn());
    }};
    RegimenTemplate regimenTemplate = new RegimenTemplate(1L, regimenColumns);
    when(repository.getRegimenColumnsByProgramId(1L)).thenReturn(regimenColumns);
    whenNew(RegimenTemplate.class).withArguments(1L, regimenColumns).thenReturn(regimenTemplate);

    RegimenTemplate template = service.getRegimenTemplateOrMasterTemplate(1L);

    verifyNew(RegimenTemplate.class).withArguments(1L, regimenColumns);
    assertThat(template, is(regimenTemplate));
    verify(repository).getRegimenColumnsByProgramId(1L);
  }


  @Test
  public void shouldGetMasterRegimenTemplateForProgramIfRegimenTemplateNotConfigured() throws Exception {
    ArrayList<RegimenColumn> regimenColumns = new ArrayList<>();
    ArrayList<RegimenColumn> masterRegimenColumns = new ArrayList<RegimenColumn>() {{
      add(new RegimenColumn());
    }};

    Long programId = 1L;
    RegimenTemplate regimenTemplate = new RegimenTemplate(programId, masterRegimenColumns);
    when(repository.getRegimenColumnsByProgramId(programId)).thenReturn(regimenColumns);
    when(repository.getMasterRegimenColumnsByProgramId()).thenReturn(masterRegimenColumns);
    whenNew(RegimenTemplate.class).withArguments(programId, masterRegimenColumns).thenReturn(regimenTemplate);

    RegimenTemplate template = service.getRegimenTemplateOrMasterTemplate(programId);

    verifyNew(RegimenTemplate.class).withArguments(programId, masterRegimenColumns);
    assertThat(template, is(regimenTemplate));
    verify(repository).getRegimenColumnsByProgramId(programId);
    verify(repository).getMasterRegimenColumnsByProgramId();
  }

  @Test
  public void shouldGetProgramRegimenTemplate() throws Exception {
    List<RegimenColumn> regimenColumns = new ArrayList<>();
    Long programId = 1L;
    when(repository.getRegimenColumnsByProgramId(programId)).thenReturn(regimenColumns);
    RegimenTemplate programRegimenTemplate = new RegimenTemplate();
    whenNew(RegimenTemplate.class).withArguments(programId, regimenColumns).thenReturn(programRegimenTemplate);

    RegimenTemplate regimenTemplate = service.getRegimenTemplateByProgramId(programId);

    verifyNew(RegimenTemplate.class).withArguments(programId, regimenColumns);
    assertThat(regimenTemplate, is(programRegimenTemplate));
    verify(repository).getRegimenColumnsByProgramId(programId);
  }

  @Test
  public void shouldGetProgramRegimenColumnsForPrint() throws Exception {

    Long programId = 1L;
    RegimenColumn regimenColumn = make(a(defaultRegimenColumn, with(name, "name"), with(label, "header.name")));
    RegimenColumn regimenColumn1 = make(a(defaultRegimenColumn, with(name, "code"), with(label, "header.code")));
    RegimenColumn regimenColumn2 = make(a(defaultRegimenColumn));
    List<RegimenColumn> regimenColumnList = asList(regimenColumn, regimenColumn1, regimenColumn2);
    when(repository.getRegimenColumnsByProgramId(programId)).thenReturn(regimenColumnList);
    when(messageService.message("header.name")).thenReturn("Name");
    when(messageService.message("header.code")).thenReturn("Code");

    List<RegimenColumn> regimenColumnsForPrintByProgramId = service.getRegimenColumnsForPrintByProgramId(programId);

    verify(repository).getRegimenColumnsByProgramId(programId);
    verify(messageService, never()).message("patients on treatment");
    assertThat(regimenColumnsForPrintByProgramId.get(0).getLabel(), is("Name"));
    assertThat(regimenColumnsForPrintByProgramId.get(1).getLabel(), is("Code"));
    assertThat(regimenColumnsForPrintByProgramId.get(2).getLabel(), is("patients on treatment"));

  }


}
