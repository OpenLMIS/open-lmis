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
import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;
import static java.util.Collections.EMPTY_LIST;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.FacilityApprovedProductController.FACILITY_APPROVED_PRODUCTS;
import static org.openlmis.web.controller.FacilityApprovedProductController.PAGINATION;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(FacilityApprovedProductController.class)
public class FacilityApprovedProductControllerTest {

  @Mock
  FacilityApprovedProductService service;

  @InjectMocks
  FacilityApprovedProductController controller;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void shouldGetAllNonFullSupplyProductsByFacilityAndProgram() throws Exception {
    Long facilityId = 1L;
    Long programId = 1L;
    ArrayList<FacilityTypeApprovedProduct> nonFullSupplyProducts = new ArrayList<>();
    when(service.getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId)).thenReturn(nonFullSupplyProducts);
    ResponseEntity<OpenLmisResponse> openLmisResponse =
      controller.getAllNonFullSupplyProductsByFacilityAndProgram(facilityId, programId);
    verify(service).getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId);
    assertThat((ArrayList<FacilityTypeApprovedProduct>) openLmisResponse.getBody().getData().get(FacilityApprovedProductController.NON_FULL_SUPPLY_PRODUCTS), is(nonFullSupplyProducts));
  }

  @Test
  public void shouldGetAllByFacilityTypeIdAndProgramId() throws Exception {
    Long facilityTypeId = 1L;
    Long programId = 2L;
    String searchParam = "search";
    Integer page = 2;
    String limit = "5";
    Integer count = 10;
    Pagination pagination = new Pagination(0, 0);
    whenNew(Pagination.class).withArguments(page, parseInt(limit)).thenReturn(pagination);
    when(service.getTotalSearchResultCount(facilityTypeId, programId, searchParam)).thenReturn(count);
    when(service.getAllBy(facilityTypeId, programId, searchParam, pagination)).thenReturn(EMPTY_LIST);

    ResponseEntity<OpenLmisResponse> response = controller.getAllBy(facilityTypeId, programId, searchParam, page, limit);

    assertThat((java.util.List) response.getBody().getData().get(FACILITY_APPROVED_PRODUCTS), is(EMPTY_LIST));
    assertThat((Pagination) response.getBody().getData().get(PAGINATION), is(pagination));
    assertThat(pagination.getTotalRecords(), is(count));
    verify(service).getAllBy(facilityTypeId, programId, searchParam, pagination);
    verify(service).getTotalSearchResultCount(facilityTypeId, programId, searchParam);
  }
}
