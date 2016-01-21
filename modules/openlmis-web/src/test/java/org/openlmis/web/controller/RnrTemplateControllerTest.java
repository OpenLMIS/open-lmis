/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.service.RnrTemplateService;
import org.openlmis.web.form.RnrColumnList;
import org.openlmis.web.form.RnrTemplateForm;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RnrTemplateControllerTest {

  @Mock
  private RnrTemplateService rnrTemplateService;

  @Mock
  private MessageService messageService;

  @InjectMocks
  private RnrTemplateController rnrTemplateController;

  private Long existingProgramId = 1L;


  @Test
  public void shouldGetMasterColumnListForRnR() {
    List<RnrColumn> allColumns = new ArrayList<>();

    when(rnrTemplateService.fetchAllRnRColumns(existingProgramId)).thenReturn(allColumns);
    RnrTemplateForm rnrColumns = rnrTemplateController.fetchAllProgramRnrColumnList(existingProgramId);
    verify(rnrTemplateService).fetchAllRnRColumns(existingProgramId);
    assertThat(rnrColumns.getRnrColumns(), is(allColumns));
  }

  @Test
  public void shouldCreateARnRTemplateForAGivenProgramWithSpecifiedColumns() throws Exception {
    final RnrColumnList rnrColumns = new RnrColumnList();

    MockHttpServletRequest request = new MockHttpServletRequest();

    when(rnrTemplateService.saveRnRTemplateForProgram((ProgramRnrTemplate) any())).thenReturn(new HashMap<String, OpenLmisMessage>());
    when(messageService.message("template.save.success")).thenReturn("dummy success");
    ResponseEntity<OpenLmisResponse> responseEntity = rnrTemplateController.saveRnRTemplateForProgram(existingProgramId, rnrColumns, request);

    assertThat(responseEntity.getBody().getSuccessMsg(), is("dummy success"));
  }

  @Test
  public void shouldFetchColumnsForRnr() throws Exception {
    long programId = 1L;
    rnrTemplateController.fetchColumnsForRequisition(programId);

    verify(rnrTemplateService).fetchColumnsForRequisition(1L);
  }

  @Test
  public void shouldReturnErrorMessagesForCorrespondingFields() throws Exception {

    RnrColumnList rnrColumns = new RnrColumnList();
    MockHttpServletRequest request = new MockHttpServletRequest();
    HashMap<String, OpenLmisMessage> errorMap = new HashMap<>();
    OpenLmisMessage openLmisMessage = new OpenLmisMessage("code", "param1", "param2");
    errorMap.put("key", openLmisMessage);
    when(rnrTemplateService.saveRnRTemplateForProgram((ProgramRnrTemplate) any())).thenReturn(errorMap);
    when(messageService.message(openLmisMessage.getCode(), openLmisMessage.getParams())).thenReturn("dummy message param1 param2");

    ResponseEntity<OpenLmisResponse> responseEntity = rnrTemplateController.saveRnRTemplateForProgram(existingProgramId, rnrColumns, request);

    assertThat((String) responseEntity.getBody().getData().get("key"), is("dummy message param1 param2"));
    assertThat((String) responseEntity.getBody().getData().get("error"), is("form.error"));

  }
}
