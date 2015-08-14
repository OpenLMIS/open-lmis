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
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.*;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.form.ProductDTO;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductControllerTest {
  private MockHttpServletRequest request = new MockHttpServletRequest();

  @Mock
  private ProductGroupService groupService;

  @Mock
  private ProductFormService formService;

  @Mock
  private ProductService service;

  @Mock
  private ProgramProductService programProductService;

  @Mock
  private ProductCategoryService productCategoryService;

  @Mock
  private ProductPriceScheduleService priceScheduleService;

  @Mock
  private MessageService messageService;

  @InjectMocks
  private ProductController controller;

  @Before
  public void setUp() throws Exception {
    request.getSession().setAttribute(UserAuthenticationSuccessHandler.USER_ID, 11L);
  }

  @Test
  public void shouldGetAllProductGroups() throws Exception {
    List<ProductGroup> groups = new ArrayList<>();
    when(groupService.getAll()).thenReturn(groups);

    List<ProductGroup> productGroups = controller.getAllGroups();

    assertThat(productGroups, is(groups));
  }

  @Test
  public void shouldGetAllProductForms() throws Exception {
    List<ProductForm> forms = new ArrayList<>();
    when(formService.getAll()).thenReturn(forms);

    List<ProductForm> productForms = controller.getAllForms();

    assertThat(productForms, is(forms));
  }

  @Test
  public void shouldGetAllDosageUnits() throws Exception {
    List<DosageUnit> dosageUnits = new ArrayList<>();
    when(service.getAllDosageUnits()).thenReturn(dosageUnits);

    List<DosageUnit> result = controller.getAllDosageUnits();

    assertThat(result, is(dosageUnits));
  }

  @Test
  public void shouldGetById() {
    Long productId = 1L;
    Date modifiedDate = new Date();

    Product product = new Product();
    product.setCode("p10");
    product.setId(productId);
    product.setModifiedDate(modifiedDate);

    List<ProgramProduct> programProducts = asList(new ProgramProduct());

    when(service.getById(1L)).thenReturn(product);
    when(programProductService.getByProductCode("p10")).thenReturn(programProducts);
    when(priceScheduleService.getByProductId(1L)).thenReturn(null);

    ProductDTO productDTO = controller.getById(1L);

    assertThat(productDTO.getProduct(), is(product));
    assertThat(productDTO.getProgramProducts(), is(programProducts));
    assertThat(productDTO.getProductLastUpdated(), is(modifiedDate));
    verify(service).getById(1L);
    verify(programProductService).getByProductCode("p10");
  }

  @Test
  public void shouldReturnNullIfProductDoesNotExists() {
    when(service.getById(2L)).thenReturn(null);

    ProductDTO productDTO = controller.getById(2L);

    assertThat(productDTO, is(nullValue()));
  }

  @Test
  public void shouldSaveProgramProduct() {
    ProductDTO productDTO = new ProductDTO();
    Product product = new Product();
    productDTO.setProduct(product);
    List<ProgramProduct> programProducts = asList(new ProgramProduct());
    productDTO.setProgramProducts(programProducts);

    doNothing().when(service).save(product);
    doNothing().when(programProductService).saveAll(programProducts, product);
    when(messageService.message("message.product.created.success", productDTO.getProduct().getName())).thenReturn("save success");

    ResponseEntity<OpenLmisResponse> response = controller.save(productDTO, request);

    assertThat(product.getCreatedBy(), is(11L));
    assertThat(product.getModifiedBy(), is(11L));
    assertThat(response.getBody().getSuccessMsg(), is("save success"));
    assertThat((Long) response.getBody().getData().get("productId"), is(product.getId()));
    verify(service).save(product);
  }

  @Test
  public void shouldReturnBadRequestWhenServiceThrowsExceptionOnSave() {
    ProductDTO productDTO = new ProductDTO();
    Product product = new Product();
    productDTO.setProduct(product);
    doThrow(new DataException("error message")).when(service).save(product);

    ResponseEntity<OpenLmisResponse> response = controller.save(productDTO, request);

    assertThat(response.getBody().getErrorMsg(), is("error message"));
    verify(service).save(product);
  }

  @Test
  public void shouldThrowExceptionIfInvalidProgramProductBeingUpdated() throws Exception {
    ProductDTO productDTO = new ProductDTO();
    Product product = new Product();
    productDTO.setProduct(product);
    doThrow(new DataException("error")).when(service).save(product);

    ResponseEntity<OpenLmisResponse> errorResponse = controller.update(productDTO, 9L, request);

    assertThat(errorResponse.getBody().getErrorMsg(), is("error"));
    assertThat(errorResponse.getStatusCode(), is(BAD_REQUEST));
  }

  @Test
  public void shouldUpdateProgramProduct() throws Exception {
    ProductDTO productDTO = new ProductDTO();
    Product product = new Product();
    productDTO.setProduct(product);
    List<ProgramProduct> programProducts = asList(new ProgramProduct());
    productDTO.setProgramProducts(programProducts);

    doNothing().when(service).save(product);
    doNothing().when(programProductService).saveAll(programProducts, product);
    doNothing().when(priceScheduleService).saveAll(productDTO.getProductPriceSchedules(), product);

    when(messageService.message("message.product.updated.success", productDTO.getProduct().getName())).thenReturn("updated");

    ResponseEntity<OpenLmisResponse> response = controller.update(productDTO, 1L, request);

    assertThat((Long) response.getBody().getData().get("productId"), is(productDTO.getProduct().getId()));
    assertThat(productDTO.getProduct().getId(), is(1L));
    assertThat(productDTO.getProduct().getModifiedBy(), is(11L));
    assertThat(response.getBody().getSuccessMsg(), is("updated"));
    verify(service).save(product);
  }

  @Test
  public void shouldGetAllProductCategories() throws Exception {
    List<ProductCategory> categories = new ArrayList<>();
    when(productCategoryService.getAll()).thenReturn(categories);

    List<ProductCategory> result = controller.getAllCategories();

    assertThat(result, is(categories));
  }
}
