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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.RegimenColumn;
import org.openlmis.rnr.domain.RegimenTemplate;
import org.openlmis.rnr.service.RegimenColumnService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class RegimenTemplateControllerTest {

  @InjectMocks
  RegimenTemplateController controller;

  MockHttpServletRequest httpServletRequest;

  @Mock
  RegimenColumnService service;

  @Before
  public void setUp() throws Exception {
    httpServletRequest = new MockHttpServletRequest();
    MockHttpSession mockHttpSession = new MockHttpSession();
    httpServletRequest.setSession(mockHttpSession);
    mockHttpSession.setAttribute(USER, USER);
    mockHttpSession.setAttribute(USER_ID, 1L);
  }

  @Test
  public void shouldGetRegimenTemplate() throws Exception {

    List<RegimenColumn> expectedRegimens = new ArrayList<>();
    Long programId = 1l;
    RegimenTemplate template = new RegimenTemplate(programId, expectedRegimens);
    when(service.getRegimenTemplateOrMasterTemplate(programId)).thenReturn(template);

    ResponseEntity<OpenLmisResponse> response = controller.getProgramOrMasterRegimenTemplate(programId);

    verify(service).getRegimenTemplateOrMasterTemplate(programId);
    assertThat((RegimenTemplate) response.getBody().getData().get("template"), is(template));
  }

  @Test
  public void shouldGetProgramRegimenTemplate() throws Exception {
    List<RegimenColumn> expectedRegimens = new ArrayList<>();
    Long programId = 1l;
    RegimenTemplate template = new RegimenTemplate(programId, expectedRegimens);
    when(service.getRegimenTemplateByProgramId(programId)).thenReturn(template);

    ResponseEntity<OpenLmisResponse> response = controller.getProgramRegimenTemplate(programId);

    verify(service).getRegimenTemplateByProgramId(programId);
    assertThat((RegimenTemplate) response.getBody().getData().get("template"), is(template));
  }
}
