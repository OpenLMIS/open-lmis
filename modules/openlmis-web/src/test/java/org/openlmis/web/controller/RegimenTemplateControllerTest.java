/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.RegimenColumn;
import org.openlmis.core.domain.RegimenTemplate;
import org.openlmis.core.service.RegimenColumnService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
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
  public void shouldGetRegimenColumns() throws Exception {

    List<RegimenColumn> expectedRegimens = new ArrayList<>();
    Long programId = 1l;
    RegimenTemplate template = new RegimenTemplate(programId, expectedRegimens);
    when(service.getRegimenTemplate(programId)).thenReturn(template);

    ResponseEntity<OpenLmisResponse> response = controller.getRegimenTemplate(programId);

    verify(service).getRegimenTemplate(programId);
    assertThat((RegimenTemplate) response.getBody().getData().get("template"), is(template));
  }
}
