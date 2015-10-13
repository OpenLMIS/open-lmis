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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Column;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.RnrTemplateRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RnrTemplateServiceTest {

  private final static Long EXISTING_PROGRAM_ID = 1L;
  @Mock
  private RnrTemplateRepository repository;
  @Mock
  private ProgramService programService;
  @Mock
  private ConfigurationSettingService configService;
  @InjectMocks
  private RnrTemplateService service;

  @Test
  public void shouldFetchAllRnRColumns() throws Exception {
    service.fetchAllRnRColumns(EXISTING_PROGRAM_ID);
    verify(repository).fetchRnrTemplateColumnsOrMasterColumns(EXISTING_PROGRAM_ID);
  }

  @Test
  public void shouldSaveRnRTemplateForAProgramWithGivenColumns() throws Exception {
    ProgramRnrTemplate programRnrTemplate = mock(ProgramRnrTemplate.class);
    List<? extends Column> rnrColumns = new ArrayList<>();
    doReturn(rnrColumns).when(programRnrTemplate).getColumns();
    when(programRnrTemplate.getProgramId()).thenReturn(1L);

    when(programRnrTemplate.validateToSave()).thenReturn(new HashMap<String, OpenLmisMessage>());
    service.saveRnRTemplateForProgram(programRnrTemplate);
    verify(repository).saveProgramRnrTemplate(programRnrTemplate);
    verify(programService).setTemplateConfigured(1L);
  }

  @Test
  public void shouldNotSaveIfErrorInTemplate() throws Exception {
    ProgramRnrTemplate programRnrTemplate = mock(ProgramRnrTemplate.class);
    ArrayList<? extends RnrColumn> rnrColumns = new ArrayList<>();
    doReturn(rnrColumns).when(programRnrTemplate).getColumns();

    HashMap<String, OpenLmisMessage> errors = new HashMap<>();
    errors.put("error-code", new OpenLmisMessage("error-message"));
    when(programRnrTemplate.validateToSave()).thenReturn(errors);
    service.saveRnRTemplateForProgram(programRnrTemplate);
    verify(repository, never()).saveProgramRnrTemplate(programRnrTemplate);
  }

  @Test
  public void shouldFetchProgramTemplate() throws Exception {
    List<? extends Column> columns = new ArrayList<>();
    doReturn(columns).when(repository).fetchRnrTemplateColumnsOrMasterColumns(1L);

    ProgramRnrTemplate template = service.fetchProgramTemplate(1L);

    verify(repository).fetchRnrTemplateColumnsOrMasterColumns(1l);
    assertThat(template.getColumns().size(), is(columns.size()));
  }

  @Test
  public void shouldFetchProgramTemplateForRequisition() {
    List<? extends Column> columns = new ArrayList<>();
    doReturn(columns).when(repository).fetchColumnsForRequisition(1L);
    configService.getConfigurationStringValue("DEFAULT_ZERO");
    when(configService.getConfigurationStringValue("DEFAULT_ZERO")).thenReturn("false");

    ProgramRnrTemplate template = service.fetchProgramTemplateForRequisition(1L);

    verify(repository).fetchColumnsForRequisition(1l);
    assertThat(template.getColumns().size(), is(columns.size()));
    assertThat(template.getApplyDefaultZero(), is(Boolean.FALSE));

  }
}
