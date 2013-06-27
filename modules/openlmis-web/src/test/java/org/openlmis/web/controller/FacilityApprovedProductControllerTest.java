/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityApprovedProductControllerTest {

  @Mock
  FacilityApprovedProductService facilityApprovedProductService;


  FacilityApprovedProductController facilityApprovedProductController;
  @Test
  public void shouldGetAllNonFullSupplyProductsByFacilityAndProgram() throws Exception {
    Long facilityId = 1L;
    Long programId = 1L;
    facilityApprovedProductController = new FacilityApprovedProductController(facilityApprovedProductService);
    ArrayList<FacilityTypeApprovedProduct> nonFullSupplyProducts = new ArrayList<>();
    when(facilityApprovedProductService.getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId)).thenReturn(nonFullSupplyProducts);
    ResponseEntity<OpenLmisResponse> openLmisResponse =
      facilityApprovedProductController.getAllNonFullSupplyProductsByFacilityAndProgram(facilityId, programId);
    verify(facilityApprovedProductService).getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId);
    assertThat((ArrayList<FacilityTypeApprovedProduct>) openLmisResponse.getBody().getData().get(FacilityApprovedProductController.NON_FULL_SUPPLY_PRODUCTS), is(nonFullSupplyProducts));
  }
}
