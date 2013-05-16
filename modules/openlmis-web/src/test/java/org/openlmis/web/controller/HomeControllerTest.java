/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
@Category(UnitTests.class)
public class HomeControllerTest {

    @Mock
    HttpServletRequest request;
    @Mock
    HttpSession session;

    HomeController homeController;

    @Before
    public void setUp() {
        initMocks(this);
        homeController = new HomeController();
        when(request.getSession()).thenReturn(session);
    }


    @Test
    public void shouldRedirectToHomePage() {
        String homePageURl = homeController.homeDefault();
        assertEquals("redirect:/public/pages/index.html", homePageURl);
    }

}
