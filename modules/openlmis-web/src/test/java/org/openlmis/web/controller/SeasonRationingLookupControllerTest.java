/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.OrderQuantityAdjustmentFactor;
import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.openlmis.core.service.OrderQuantityAdjustmentFactorService;
import org.openlmis.core.service.OrderQuantityAdjustmentTypeService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.controller.seasonalRationing.SeasonRationingLookupController;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import org.mockito.Matchers;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class SeasonRationingLookupControllerTest {
    @Mock
    private OrderQuantityAdjustmentFactorService factorService;
    private MockHttpServletRequest servletRequest;
    @Mock
    private OrderQuantityAdjustmentTypeService typeService;
    @InjectMocks
    private SeasonRationingLookupController lookupController;
    private static final String USER = "user";
    private static final Long USER_ID = 1l;

    @Before
    public void setUp() throws Exception {
        servletRequest = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
        session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

        servletRequest.setSession(session);
    }


    @Test
    public void testGetSeasonalityRationingDetail1() {
        OrderQuantityAdjustmentType adjustmentType = new OrderQuantityAdjustmentType();
        lookupController.getSeasonalityRationingDetail1(1l);
        verify(typeService).loadOrderQuantityAdjustmentType(1l);
    }

    @Test
    public void testUpdateSeasnalityRationingType() {
        OrderQuantityAdjustmentType adjustmentType = new OrderQuantityAdjustmentType();
        lookupController.updateSeasnalityRationingType(adjustmentType, servletRequest);
        verify(typeService).updateOrderQuantityAdjustmentType(adjustmentType);
    }

    @Test
    public void testCreateSeasonalityRationingType() {
        OrderQuantityAdjustmentType adjustmentType = new OrderQuantityAdjustmentType();
        lookupController.createSeasonalityRationingType(adjustmentType, servletRequest);
        verify(typeService).addOrderQuantityAdjustmentType(adjustmentType);
    }

    @Test
    public void testRemoveSeasonRationingType() {
        OrderQuantityAdjustmentType adjustmentType = new OrderQuantityAdjustmentType();
        lookupController.removeSeasonRationingType(1l, servletRequest);
        verify(typeService).deleteOrderQuantityAdjustmentType(adjustmentType);

    }

    @Test
    public void testSearchSeasonRationingTypeList() {

        lookupController.searchSeasonRationingTypeList("");
        verify(typeService).searchForQuantityAdjustmentType("");
    }

    @Test
    public void testDeleteSeasonalRationingType() {
        OrderQuantityAdjustmentType adjustmentType = new OrderQuantityAdjustmentType();
        lookupController.deleteSeasonalRationingType(adjustmentType, servletRequest);
        verify(typeService).deleteOrderQuantityAdjustmentType(adjustmentType);
    }

    @Test
    public void testLoadAllSeasonRationingTypes() {

        lookupController.loadAllSeasonRationingTypes();
        verify(typeService).loadOrderQuantityAdjustmentTypeList();
    }

    /*

     */

    @Test
    public void testGetAdjustmentFactorDetail1() {
        this.lookupController.getAdjustmentFactorDetail1(1l);
        verify(this.factorService).loadOrderQuantityAdjustmentFactorDetail(1l);
    }

    @Test
    public void testUpdateAdjustmentFactor() {
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();
        this.lookupController.updateAdjustmentFactor(adjustmentFactor, servletRequest);
        verify(this.factorService).updateOrderQuantityAdjustmentFactor(adjustmentFactor);
    }

    @Test
    public void testCreateAdjustmentFactor() {
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();
        this.lookupController.createAdjustmentFactor(adjustmentFactor, servletRequest);
        verify(this.factorService).addOrderQuantityAdjustmentFactor(adjustmentFactor);
    }

    @Test
    public void testRemoveAdjustmentFactor() {
        this.lookupController.removeAdjustmentFactor(1l, servletRequest);
        verify(this.factorService, atLeastOnce()).deleteOrderQuantityAdjustmentFactor(Matchers.any(OrderQuantityAdjustmentFactor.class));
    }

    @Test
    public void testSearchAdjustmentFactorList() {
        this.lookupController.searchAdjustmentFactorList("");
        verify(this.factorService).searchAdjustmentFactor("");
    }

    @Test
    public void testDeleteAdjustmentFactor() {
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();
        this.lookupController.deleteAdjustmentFactor(adjustmentFactor, servletRequest);
        verify(this.factorService).deleteOrderQuantityAdjustmentFactor(adjustmentFactor);
    }

    @Test
    public void testLoadAllAdjustmentFactories() {
        this.lookupController.loadAllAdjustmentFactories();
        verify(this.factorService).loadOrderQuantityAdjustmentFactor();
    }

}
