/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
//import org.openlmis.core.service.SupplyLineService;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.core.service.SupplyLineServiceExtension;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.web.controller.SupplyLineController.SUPPLYLINES;
import static org.openlmis.web.controller.SupplyLineController.SUPPLYLINE;
import static org.openlmis.web.response.OpenLmisResponse.SUCCESS;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class SupplyLineControllerTest {
    SupplyLine supplyline;
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    String supplylineDescription = "Test Description";

    private static final Long userId = 1L;
    private final Long PROGRAM_ID = 1L;
    private final Long SUPPLYINGFACILITY_ID = 1L;
    private final Long SUPERVISORYNODE_ID = 1L;


    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

    @Mock
    SupplyLineService supplyLineService;

    @Mock
    SupplyLineServiceExtension supplyLineServiceExtension;

    @Mock
    MessageService messageService;

    @InjectMocks
    //private SupplyLineController supplyLineController;
    private SupplyLineController supplyLineController;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        MockHttpSession mockHttpSession = new MockHttpSession();
        httpServletRequest.setSession(mockHttpSession);
        mockHttpSession.setAttribute(USER_ID, userId);
    }

    @Test
    public void shouldGetAll() throws Exception {
        List<SupplyLine> supplyLines = new ArrayList<>();
        when(supplyLineServiceExtension.getAllSupplyLine()).thenReturn(supplyLines);

        ResponseEntity<OpenLmisResponse> responseEntity = supplyLineController.getAllSupplyLine();

        Map<String, Object> responseEntityData = responseEntity.getBody().getData();
        assertThat((List<SupplyLine>) responseEntityData.get(SUPPLYLINES), is(supplyLines));
    }


    @Test
    public void shouldSaveSupplyline() throws Exception {
       //when(messageService.message("message.role.created.success", "test role")).thenReturn("'test role' created successfully");
       //ResponseEntity<OpenLmisResponse> responseEntity = supplyLineController.create(supplyline, httpServletRequest);

       //
       // verify(supplyLineService).save(supplyline);
       // assertThat(supplyline.getModifiedBy(), is(userId));
       // String successMsg = (String) responseEntity.getBody().getData().get(SUCCESS);
       // assertThat(successMsg, is("'test supply line' created successfully"));
    }

 }
