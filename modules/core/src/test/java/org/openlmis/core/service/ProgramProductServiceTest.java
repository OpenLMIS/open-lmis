/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProgramProductBuilder.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramProductServiceTest {

  @Rule
  public ExpectedException expectException = ExpectedException.none();

  @Mock
  private ProgramProductRepository programProductRepository;

  @Mock
  private ProductService productService;

  @Mock
  private ProgramService programService;

  @Mock
  private ProgramRepository programRepository;

  @Mock
  private FacilityRepository facilityRepository;

  @Mock
  private ProductCategoryService categoryService;

  @Mock
  private FacilityApprovedProductService facilityApprovedProductService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @InjectMocks
  private ProgramProductService service;

  @Test
  public void shouldUpdateCurrentPriceOfProgramProductCodeCombinationAndUpdatePriceHistory() throws Exception {
    ProgramProduct programProduct = make(a(defaultProgramProduct));
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, new Money("1"), "source");
    programProductPrice.setModifiedBy(1L);

    ProgramProduct returnedProgramProduct = new ProgramProduct();
    returnedProgramProduct.setId(123L);
    when(programProductRepository.getByProgramAndProductCode(programProduct)).thenReturn(returnedProgramProduct);

    service.updateProgramProductPrice(programProductPrice);

    assertThat(programProductPrice.getProgramProduct().getId(), is(123L));
    assertThat(programProductPrice.getProgramProduct().getModifiedBy(), is(1L));
    verify(programProductRepository).getByProgramAndProductCode(programProduct);
    verify(programProductRepository).updateCurrentPrice(programProduct);
    verify(programProductRepository).updatePriceHistory(programProductPrice);
  }

  @Test
  public void shouldValidateProgramProductPriceBeforeSaving() throws Exception {
    expectException.expect(DataException.class);
    expectException.expectMessage("error-code");

    ProgramProductPrice programProductPrice = mock(ProgramProductPrice.class);
    doThrow(new DataException("error-code")).when(programProductPrice).validate();

    service.updateProgramProductPrice(programProductPrice);
  }

  @Test
  public void shouldThrowExceptionIfProgramProductByProgramAndProductCodesNotFound() throws Exception {
    ProgramProduct programProduct = make(a(defaultProgramProduct));
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, new Money("1"), "source");
    programProductPrice.setModifiedBy(1L);

    when(programProductRepository.getByProgramAndProductCode(programProduct)).thenReturn(null);

    expectException.expect(DataException.class);
    expectException.expectMessage("programProduct.product.program.invalid");

    service.updateProgramProductPrice(programProductPrice);
  }

  @Test
  public void shouldInsertProgramProduct() throws Exception {
    ProgramProduct programProduct = make(a(defaultProgramProduct));

    service.save(programProduct);

    verify(programProductRepository).save(programProduct);
  }

  @Test
  public void shouldNotifyIfNewActiveProductAddedToAProgramAndProgramProductIsActive() throws Exception {
    String product = "product";
    ProgramProduct programProduct = make(
      a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
        with(active, true)));
    programProduct.setId(null);
    when(productService.isActive(product)).thenReturn(true);

    service.save(programProduct);

    verify(programService).setFeedSendFlag(programProduct.getProgram(), true);
  }

  @Test
  public void shouldNotNotifyIfNewProductAddedWhichIsGloballyInactive() throws Exception {
    String product = "product";
    ProgramProduct programProduct = make(
      a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
        with(active, true)));
    when(productService.isActive(product)).thenReturn(false);

    service.save(programProduct);

    verify(programService, never()).setFeedSendFlag(programProduct.getProgram(), false);
  }

  @Test
  public void shouldNotNotifyIfNewInactiveProductAddedEvenIfItIsGloballyActive() throws Exception {
    String product = "product";
    ProgramProduct programProduct = make(
      a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
        with(active, false)));
    when(productService.isActive(product)).thenReturn(true);

    service.save(programProduct);

    verify(programService, never()).setFeedSendFlag(programProduct.getProgram(), false);
  }

  @Test
  public void shouldNotNotifyIfNewInactiveProductAddedAlsoInactiveGlobally() throws Exception {
    String product = "product";
    ProgramProduct programProduct = make(
      a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
        with(active, false)));
    when(productService.isActive(product)).thenReturn(false);

    service.save(programProduct);

    verify(programService, never()).setFeedSendFlag(programProduct.getProgram(), false);
  }

  @Test
  public void shouldNotifyOnUpdateIfActiveFlagsChangeFromTTAndBecomeFT() throws Exception {
    String product = "product";
    Long existingProgramProductId = 2L;
    ProgramProduct programProductForUpdate = make(
      a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
        with(active, false)));
    programProductForUpdate.setId(existingProgramProductId);

    ProgramProduct existingProgramProduct = make(
      a(defaultProgramProduct, with(active, true), with(productActive, true)));
    when(programProductRepository.getById(existingProgramProductId)).thenReturn(existingProgramProduct);

    service.save(programProductForUpdate);

    verify(programService).setFeedSendFlag(programProductForUpdate.getProgram(), true);
  }

  @Test
  public void shouldNotifyOnUpdateIfActiveFlagsChangeFromFTAndBecomeTT() throws Exception {
    String product = "product";
    Long existingProgramProductId = 2L;
    ProgramProduct programProductForUpdate = make(
      a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
        with(active, true)));
    programProductForUpdate.setId(existingProgramProductId);

    ProgramProduct existingProgramProduct = make(
      a(defaultProgramProduct, with(active, false), with(productActive, true)));
    when(programProductRepository.getById(existingProgramProductId)).thenReturn(existingProgramProduct);

    service.save(programProductForUpdate);

    verify(programService).setFeedSendFlag(programProductForUpdate.getProgram(), true);
  }

  @Test
  public void shouldNotNotifyOnUpdateIfActiveFlagsChangeFromFFAndBecomeFF() throws Exception {
    String product = "product";
    Long existingProgramProductId = 2L;
    ProgramProduct programProductForUpdate = make(
      a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
        with(active, false)));
    programProductForUpdate.setId(existingProgramProductId);

    ProgramProduct existingProgramProduct = make(
      a(defaultProgramProduct, with(active, false), with(productActive, false)));
    when(programProductRepository.getById(existingProgramProductId)).thenReturn(existingProgramProduct);

    service.save(programProductForUpdate);

      verify(programService, never()).setFeedSendFlag(programProductForUpdate.getProgram(), true);
  }

  @Test
  public void shouldNotNotifyOnUpdateIfActiveFlagsChangeFromTFAndBecomeTF() throws Exception {
    String product = "product";
    Long existingProgramProductId = 2L;
    ProgramProduct programProductForUpdate = make(
      a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
        with(active, true)));
    programProductForUpdate.setId(existingProgramProductId);

    ProgramProduct existingProgramProduct = make(
      a(defaultProgramProduct, with(active, true), with(productActive, false)));
    when(programProductRepository.getById(existingProgramProductId)).thenReturn(existingProgramProduct);

    service.save(programProductForUpdate);

    verify(programService, never()).setFeedSendFlag(programProductForUpdate.getProgram(), true);
  }

  @Test
  public void shouldNotNotifyOnUpdateIfActiveFlagsChangeFromTFAndBecomeFF() throws Exception {
    String product = "product";
    Long existingProgramProductId = 2L;
    ProgramProduct programProductForUpdate = make(
      a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
        with(active, false)));
    programProductForUpdate.setId(existingProgramProductId);

    ProgramProduct existingProgramProduct = make(
      a(defaultProgramProduct, with(active, true), with(productActive, false)));
    when(programProductRepository.getById(existingProgramProductId)).thenReturn(existingProgramProduct);

    service.save(programProductForUpdate);

    verify(programService, never()).setFeedSendFlag(programProductForUpdate.getProgram(), true);
  }

  @Test
  public void shouldGetAllProductsByProgram() {
    Program program = new Program();
    List<ProgramProduct> expectedProgramProducts = new ArrayList<>();
    when(programProductRepository.getByProgram(program)).thenReturn(expectedProgramProducts);

    List<ProgramProduct> programProducts = service.getByProgram(program);

    assertThat(programProducts, is(expectedProgramProducts));
    verify(programProductRepository).getByProgram(program);
  }

  @Test
  public void shouldGetAllProgramProductsByProductCode() {
    List<ProgramProduct> expectedProgramProducts = new ArrayList<>();
    when(programProductRepository.getByProductCode("code")).thenReturn(expectedProgramProducts);

    List<ProgramProduct> programProducts = service.getByProductCode("code");

    assertThat(programProducts, is(expectedProgramProducts));
    verify(programProductRepository).getByProductCode("code");
  }

  @Test
  public void shouldGetAllProgramProductsByProgramCodeAndFacilityTypeCode() {
    List<ProgramProduct> expectedProgramProducts = new ArrayList<>();
    when(programRepository.getIdByCode("P1")).thenReturn(10L);
    FacilityType warehouse = new FacilityType("warehouse");
    when(facilityRepository.getFacilityTypeByCode(warehouse)).thenReturn(warehouse);
    when(programProductRepository.getProgramProductsBy(10L, "warehouse")).thenReturn(expectedProgramProducts);

    List<ProgramProduct> programProducts = service.getProgramProductsBy(" P1", " warehouse");

    assertThat(programProducts, is(expectedProgramProducts));
    verify(facilityRepository).getFacilityTypeByCode(warehouse);
    verify(programRepository).getIdByCode("P1");
    verify(programProductRepository).getProgramProductsBy(10L, "warehouse");
  }


  @Test
  public void shouldGetAllProgramProductsByProgramCodeForNullFacilityTypeCode() {
    List<ProgramProduct> expectedProgramProducts = new ArrayList<>();
    when(programRepository.getIdByCode("P1")).thenReturn(10L);
    when(programProductRepository.getProgramProductsBy(10L, "warehouse")).thenReturn(expectedProgramProducts);

    List<ProgramProduct> programProducts = service.getProgramProductsBy("P1", null);

    assertThat(programProducts, is(expectedProgramProducts));
    verify(facilityRepository, never()).getFacilityTypeByCode(any(FacilityType.class));
    verify(programRepository).getIdByCode("P1");
    verify(programProductRepository).getProgramProductsBy(10L, null);
  }

  @Test
  public void shouldThrowExceptionIfCategoryDoesNotExist() {
    ProgramProduct programProduct = new ProgramProduct();
    ProductCategory category = new ProductCategory();
    category.setCode("Invalid Code");
    programProduct.setProductCategory(category);
    when(categoryService.getProductCategoryIdByCode("Invalid Code")).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.reference.data.invalid.product");

    service.save(programProduct);
  }

  @Test
  public void shouldSetIdIfCategoryExists() {
    Long categoryId = 5L;
    ProgramProduct programProduct = make(a(defaultProgramProduct));
    ProductCategory category = new ProductCategory();
    category.setCode("Code");
    programProduct.setProductCategory(category);
    when(categoryService.getProductCategoryIdByCode("Code")).thenReturn(categoryId);

    service.save(programProduct);

    assertThat(category.getId(), is(categoryId));
  }

  @Test
  public void shouldReturnFacilityTypeUnapprovedProgramProducts() throws Exception {
    Long facilityTypeId = 56L;
    Long programId = 67L;

    ProgramProduct programProduct1 = new ProgramProduct(1L);
    ProgramProduct programProduct2 = new ProgramProduct(2L);
    ProgramProduct programProduct3 = new ProgramProduct(3L);
    ProgramProduct programProduct4 = new ProgramProduct(4L);
    ProgramProduct programProduct5 = new ProgramProduct(5L);

    List<ProgramProduct> allProgramProducts = asList(programProduct1, programProduct2, programProduct3,
      programProduct4, programProduct5);

    when(programProductRepository.getByProgram(any(Program.class))).thenReturn(allProgramProducts);

    FacilityTypeApprovedProduct facilityTypeApprovedProduct1 = new FacilityTypeApprovedProduct("code1", programProduct1,
      3.2);
    FacilityTypeApprovedProduct facilityTypeApprovedProduct2 = new FacilityTypeApprovedProduct("code1", programProduct4,
      3.2);
    FacilityTypeApprovedProduct facilityTypeApprovedProduct3 = new FacilityTypeApprovedProduct("code1", programProduct3,
      3.2);

    List<FacilityTypeApprovedProduct> approvedProducts = asList(facilityTypeApprovedProduct1,
      facilityTypeApprovedProduct2, facilityTypeApprovedProduct3);

    when(facilityApprovedProductService.getAllBy(facilityTypeId, programId, "", new Pagination())).thenReturn(
      approvedProducts);

    List<ProgramProduct> unapprovedProgramProducts = service.getUnapprovedProgramProducts(
      facilityTypeId, programId);

    assertThat(unapprovedProgramProducts.size(), is(2));
    assertThat(unapprovedProgramProducts.get(0).getId(), is(programProduct2.getId()));
    assertThat(unapprovedProgramProducts.get(1).getId(), is(programProduct5.getId()));
  }

  @Test
  public void shouldCallProgramProductServiceIfColumnNameIsProgram() {
    String searchParam = "a";
    String column = "Program";
    when(programProductRepository.getTotalSearchResultCount(searchParam)).thenReturn(10);

    service.getTotalSearchResultCount(searchParam, column);

    verify(programProductRepository).getTotalSearchResultCount(searchParam);
  }

  @Test
  public void shouldCallProductServiceIfColumnNameIsProduct() {
    String searchParam = "a";
    String column = "Product";
    when(productService.getTotalSearchResultCount(searchParam)).thenReturn(10);

    service.getTotalSearchResultCount(searchParam, column);

    verify(productService).getTotalSearchResultCount(searchParam);
  }

  @Test
  public void shouldSaveAll() throws Exception {
    ProgramProduct programProduct1 = new ProgramProduct();
    programProduct1.setActive(true);
    ProgramProduct programProduct2 = new ProgramProduct();
    programProduct2.setActive(false);
    Product product = new Product();
    product.setCreatedBy(1L);
    product.setModifiedBy(2L);

    ProgramProductService spyService = spy(service);

    doNothing().when(spyService).save(programProduct1);
    doNothing().when(spyService).save(programProduct2);

    spyService.saveAll(asList(programProduct1, programProduct2), product);

    assertThat(programProduct1.getProduct(), is(product));
    assertThat(programProduct1.getCreatedBy(), is(1L));
    assertThat(programProduct1.getModifiedBy(), is(2L));

    assertThat(programProduct2.getProduct(), is(product));
    assertThat(programProduct2.getCreatedBy(), is(1L));
    assertThat(programProduct2.getModifiedBy(), is(2L));

    verify(spyService).save(programProduct1);
    verify(spyService).save(programProduct2);
  }
}
