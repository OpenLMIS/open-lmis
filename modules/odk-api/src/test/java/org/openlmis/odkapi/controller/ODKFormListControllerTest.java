/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
/**
 * Created with IntelliJ IDEA.
 * User: Messay Yohannes <deliasmes@gmail.com>
  * To change this template use File | Settings | File Templates.
 */
package org.openlmis.odkapi.controller;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.odkapi.domain.ODKXForm;
import org.openlmis.odkapi.domain.ODKXFormList;
import org.openlmis.odkapi.service.ODKXFormService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@Category(UnitTests.class)
@PrepareForTest(ResponseEntity.class)
public class ODKFormListControllerTest {
    @Mock
    private ODKXFormService odkxFormService;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;


    @InjectMocks
    private ODKFormListController odkFormListController;
    @Before
    public void setUp() throws Exception
    {

    }

    @Test
    public void testGetFormList() throws Exception
    {
       assert (true);
    }

    @Test
    public void testGetForm() throws Exception
    {

    }

}
