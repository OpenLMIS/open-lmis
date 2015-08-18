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
import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.form.FacilityTypeApprovedProductList;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;
import static java.util.Collections.EMPTY_LIST;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.FacilityApprovedProductController.*;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(FacilityApprovedProductController.class)
public class FacilityApprovedProductControllerTest {

  private static final String USER_ID = "USER_ID";
  public static final long userId = 1L;
  private MockHttpServletRequest request;

  @Mock
  FacilityApprovedProductService service;

  @Mock
  private MessageService messageService;

  @InjectMocks
  FacilityApprovedProductController controller;

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest();
    request.getSession().setAttribute(USER_ID, userId);
  }

  @Test
  public void shouldGetAllNonFullSupplyProductsByFacilityAndProgram() {
    Long facilityId = 1L;
    Long programId = 1L;
    ArrayList<FacilityTypeApprovedProduct> nonFullSupplyProducts = new ArrayList<>();
    when(service.getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId)).thenReturn(nonFullSupplyProducts);

    ResponseEntity<OpenLmisResponse> openLmisResponse = controller.getAllNonFullSupplyProductsByFacilityAndProgram(facilityId, programId);

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

  @Test
  public void shouldSave() {
    FacilityTypeApprovedProductList facilityTypeApprovedProducts = new FacilityTypeApprovedProductList();
    doNothing().when(service).saveAll(facilityTypeApprovedProducts, 1L);
    when(messageService.message("message.facility.type.approved.products.added.successfully", facilityTypeApprovedProducts.size())).thenReturn("1 product(s) added successfully");

    ResponseEntity<OpenLmisResponse> response = controller.insert(facilityTypeApprovedProducts, request);

    assertThat(response.getBody().getSuccessMsg(), is("1 product(s) added successfully"));
  }

  @Test
  public void shouldThrowExceptionOnInsert() {
    FacilityTypeApprovedProductList facilityTypeApprovedProducts = new FacilityTypeApprovedProductList();
    doThrow(new DataException("error")).when(service).saveAll(facilityTypeApprovedProducts, 1l);

    ResponseEntity<OpenLmisResponse> errorResponse = controller.insert(facilityTypeApprovedProducts, request);

    assertThat(errorResponse.getBody().getErrorMsg(), is("error"));
    assertThat(errorResponse.getStatusCode(), is(BAD_REQUEST));
  }

  @Test
  public void shouldUpdate() {
    String productName = "Primary Name for facility approved product";
    Product product = new Product();
    product.setPrimaryName(productName);
    ProgramProduct programProduct = new ProgramProduct();
    programProduct.setProduct(product);
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = new FacilityTypeApprovedProduct();
    facilityTypeApprovedProduct.setProgramProduct(programProduct);
    doNothing().when(service).save(facilityTypeApprovedProduct);

    when(messageService.message("message.facility.approved.product.updated.success", productName)).thenReturn(productName + " updated successfully");

    ResponseEntity<OpenLmisResponse> response = controller.update(2L, facilityTypeApprovedProduct, request);

    assertThat(facilityTypeApprovedProduct.getId(), is(2L));
    assertThat(facilityTypeApprovedProduct.getModifiedBy(), is(userId));
    assertThat((FacilityTypeApprovedProduct) response.getBody().getData().get(FACILITY_TYPE_APPROVED_PRODUCT), is(facilityTypeApprovedProduct));
    assertThat(response.getBody().getSuccessMsg(), is("Primary Name for facility approved product updated successfully"));
  }

  @Test
  public void shouldThrowExceptionOnUpdate() {
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = new FacilityTypeApprovedProduct();
    doThrow(new DataException("error")).when(service).save(facilityTypeApprovedProduct);

    ResponseEntity<OpenLmisResponse> errorResponse = controller.update(2L, facilityTypeApprovedProduct, request);

    assertThat(errorResponse.getBody().getErrorMsg(), is("error"));
    assertThat(errorResponse.getStatusCode(), is(BAD_REQUEST));
  }

  @Test
  public void shouldDeleteFacilityApprovedProduct() {
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = new FacilityTypeApprovedProduct();
    facilityTypeApprovedProduct.setId(3L);
    doNothing().when(service).delete(3L);

    ResponseEntity<OpenLmisResponse> response = controller.delete(3L);

    assertThat(response.getBody().getSuccessMsg(), is("message.facility.approved.product.deleted.success"));
    verify(service).delete(3L);
  }
}
