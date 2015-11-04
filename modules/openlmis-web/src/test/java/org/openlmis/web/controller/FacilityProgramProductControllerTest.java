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
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.service.FacilityProgramProductService;
import org.openlmis.db.categories.UnitTests;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class FacilityProgramProductControllerTest {

  @Mock
  private FacilityProgramProductService service;

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
  public void shouldInsertFacilityProgramProductISA() {
    ProgramProductISA programProductISA = new ProgramProductISA();
    Long programProductId = 1L;
    Long facilityId = 2L;
    String username = "Foo";

    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);

    controller.insertIsa(facilityId, programProductId, programProductISA, httpServletRequest);

    verify(service).insertISA(facilityId, programProductISA);
    assertThat(programProductISA.getProgramProductId(), is(1l));
  }

}
