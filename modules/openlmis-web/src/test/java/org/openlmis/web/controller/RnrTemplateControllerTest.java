/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
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
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
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

  }
}
