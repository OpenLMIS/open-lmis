/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.service.FacilityProgramProductService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class FacilityProgramProductControllerTest {


  @Mock
  private FacilityProgramProductService service;

  public static final Long userId = 1L;

  private MockHttpServletRequest httpServletRequest;

  private MockHttpSession session;

  @InjectMocks
  private FacilityProgramProductController controller;

  @Before
  public void setUp() {
    httpServletRequest = new MockHttpServletRequest();
    session = new MockHttpSession();
    httpServletRequest.setSession(session);
  }

  @Test
  public void shouldInsertProgramProductISA() {
    ProgramProductISA programProductISA = new ProgramProductISA();
    Long programProductId = 1l;
    String username = "Foo";

    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);

    controller.insertIsa(programProductId, programProductISA,httpServletRequest);

    verify(service).insertISA(programProductISA);
    assertThat(programProductISA.getProgramProductId(), is(1l));
  }

  @Test
  public void shouldUpdateProgramProductISA() {
    ProgramProductISA programProductISA = new ProgramProductISA();
    Long isaId = 1l;
    Long programProductId = 2l;

    controller.updateIsa(isaId, programProductId, programProductISA,httpServletRequest);

    verify(service).updateISA(programProductISA);
  }
}
