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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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

  @InjectMocks
  FacilityApprovedProductController facilityApprovedProductController;

  @Test
  public void shouldGetAllNonFullSupplyProductsByFacilityAndProgram() throws Exception {
    Long facilityId = 1L;
    Long programId = 1L;
    ArrayList<FacilityTypeApprovedProduct> nonFullSupplyProducts = new ArrayList<>();
    when(facilityApprovedProductService.getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId)).thenReturn(nonFullSupplyProducts);
    ResponseEntity<OpenLmisResponse> openLmisResponse =
      facilityApprovedProductController.getAllNonFullSupplyProductsByFacilityAndProgram(facilityId, programId);
    verify(facilityApprovedProductService).getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId);
    assertThat((ArrayList<FacilityTypeApprovedProduct>) openLmisResponse.getBody().getData().get(FacilityApprovedProductController.NON_FULL_SUPPLY_PRODUCTS), is(nonFullSupplyProducts));
  }
}
