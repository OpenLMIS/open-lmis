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
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.openlmis.web.controller.FacilityProgramProductController.PROGRAM_PRODUCT_LIST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramProductControllerTest {

  private MockHttpServletRequest request = new MockHttpServletRequest();

  @Mock
  ProgramProductService service;

  @Mock
  private MessageService messageService;

  @InjectMocks
  ProgramProductController controller;

  private List<ProgramProduct> expectedProgramProductList = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    request.getSession().setAttribute(UserAuthenticationSuccessHandler.USER_ID, 11L);
  }

  @Test
  public void shouldGetProgramProductsByProgram() throws Exception {
    Program program = new Program(1l);
    when(service.getByProgram(program)).thenReturn(expectedProgramProductList);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.getProgramProductsByProgram(1l);

    assertThat((List<ProgramProduct>) responseEntity.getBody().getData().get(PROGRAM_PRODUCT_LIST),
      is(expectedProgramProductList));
    verify(service).getByProgram(program);
  }

  @Test
  public void shouldGetUnapprovedProgramProducts() {
    Long facilityTypeId = 1L;
    Long programId = 2L;
    when(service.getUnapprovedProgramProducts(facilityTypeId, programId)).thenReturn(expectedProgramProductList);

    List<ProgramProduct> unapprovedProgramProducts = controller.getUnapprovedProgramProducts(facilityTypeId, programId);

    assertThat(unapprovedProgramProducts, is(expectedProgramProductList));
    verify(service).getUnapprovedProgramProducts(facilityTypeId, programId);
  }

  @Test
  public void shouldSearchProgramProducts() {
    when(service.search(anyString(), Matchers.any(Pagination.class), anyString())).thenReturn(expectedProgramProductList);
    when(service.getTotalSearchResultCount("pro", "Program")).thenReturn(20);
    InOrder order = inOrder(service);

    ResponseEntity<OpenLmisResponse> programProducts = controller.getByProgram(1, "pro", "Program", "10");

    order.verify(service).getTotalSearchResultCount("pro", "Program");
    order.verify(service).search(anyString(), Matchers.any(Pagination.class), anyString());
    assertThat((List<ProgramProduct>) programProducts.getBody().getData().get(PROGRAM_PRODUCT_LIST), is(expectedProgramProductList));
  }

  @Test
  public void shouldSaveProgramProduct() {
    ProgramProduct programProduct = new ProgramProduct();
    Product product = new Product();
    programProduct.setProduct(product);
    when(messageService.message("message.product.created.success", programProduct.getProduct().getName())).thenReturn("save success");

    ResponseEntity<OpenLmisResponse> response = controller.save(programProduct, request);

    assertThat(product.getCreatedBy(), is(11L));
    assertThat(product.getModifiedBy(), is(11L));
    assertThat(response.getBody().getSuccessMsg(), is("save success"));
    assertThat((Long) response.getBody().getData().get("productId"), is(product.getId()));
    verify(service).saveProduct(programProduct);
  }

  @Test
  public void shouldReturnBadRequestWhenServiceThrowsExceptionOnSave() {
    ProgramProduct programProduct = new ProgramProduct();
    programProduct.setProduct(new Product());
    doThrow(new DataException("error message")).when(service).saveProduct(programProduct);

    ResponseEntity<OpenLmisResponse> response = controller.save(programProduct, request);

    assertThat(response.getBody().getErrorMsg(), is("error message"));
    verify(service).saveProduct(programProduct);
  }

  @Test
  public void shouldGetById() {
    Product product = new Product();
    product.setCode("p10");
    Long productId = 1L;
    product.setId(productId);
    Date modifiedDate = new Date();
    product.setModifiedDate(modifiedDate);
    ProgramProduct programProduct = new ProgramProduct();
    programProduct.setProduct(product);

    when(service.getById(1L)).thenReturn(programProduct);

    ResponseEntity<OpenLmisResponse> response = controller.getById(1L);

    assertThat((ProgramProduct) response.getBody().getData().get("programProduct"), is(programProduct));
    assertThat((Date) response.getBody().getData().get("productLastUpdated"), is(modifiedDate));
    verify(service).getById(1L);
  }

  @Test
  public void shouldThrowExceptionIfInvalidProgramProductBeingUpdated() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();
    Product product = new Product();
    programProduct.setProduct(product);
    doThrow(new DataException("error")).when(service).saveProduct(programProduct);

    ResponseEntity<OpenLmisResponse> errorResponse = controller.update(programProduct, 9L, request);

    assertThat(errorResponse.getBody().getErrorMsg(), is("error"));
    assertThat(errorResponse.getStatusCode(), is(BAD_REQUEST));
  }

  @Test
  public void shouldUpdateProgramProduct() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();
    Product product = new Product();
    programProduct.setProduct(product);
    doNothing().when(service).saveProduct(programProduct);
    when(messageService.message("message.product.updated.success", programProduct.getProduct().getName())).thenReturn("updated");

    ResponseEntity<OpenLmisResponse> response = controller.update(programProduct, 1L, request);

    assertThat((Long) response.getBody().getData().get("productId"), is(programProduct.getProduct().getId()));
    assertThat(programProduct.getProduct().getId(), is(1L));
    assertThat(programProduct.getProduct().getModifiedBy(), is(11L));
    assertThat(response.getBody().getSuccessMsg(), is("updated"));
    verify(service).saveProduct(programProduct);
  }
}
