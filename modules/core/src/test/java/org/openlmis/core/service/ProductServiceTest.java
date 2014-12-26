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
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProgramProductBuilder.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private ProductCategoryService categoryService;

  @Mock
  private ProductRepository repository;

  @Mock
  private ProgramService programService;

  @Mock
  ProgramProductService programProductService;

  @Mock
  private ProductGroupService productGroupService;

  @Mock
  private ProductFormService productFormService;

  @InjectMocks
  private ProductService service;

  @Test
  public void shouldStoreProduct() throws Exception {
    Product product = new Product();
    product.setPackSize(5);
    DosageUnit unit = new DosageUnit("code", 1);
    product.setDosageUnit(unit);
    DosageUnit newUnit = new DosageUnit("code", 40);

    when(productGroupService.validateAndReturn(product.getProductGroup())).thenReturn(null);
    when(productFormService.validateAndReturn(product.getForm())).thenReturn(null);
    when(repository.getDosageUnitByCode("code")).thenReturn(newUnit);
    service.save(product);

    assertThat(product.getDosageUnit(), is(newUnit));
    verify(repository).insert(product);
    verify(productGroupService).validateAndReturn(null);
    verify(productFormService).validateAndReturn(null);
  }

  @Test
  public void shouldValidateProductBeforeSaveThrowExceptionIfPackSizeIsZero() throws Exception {
    Product spyProduct = spy(new Product());
    doNothing().when(spyProduct).validate();

    service.save(spyProduct);

    verify(spyProduct).validate();
  }

  @Test
  public void shouldThrowExceptionIfDosageUnitReturnedIsNull() throws Exception {
    Product product = new Product();
    product.setPackSize(5);
    product.setDosageUnit(new DosageUnit("code", 98));

    when(productGroupService.validateAndReturn(product.getProductGroup())).thenReturn(null);
    when(productFormService.validateAndReturn(product.getForm())).thenReturn(null);
    when(repository.getDosageUnitByCode("code")).thenReturn(null);
    expectedEx.expect(dataExceptionMatcher("error.reference.data.invalid.dosage.unit"));

    service.save(product);

    verify(repository, never()).insert(product);
    verify(productGroupService).validateAndReturn(null);
    verify(productFormService).validateAndReturn(null);
    verify(repository).getDosageUnitByCode("code");
  }

  @Test
  public void shouldValidateAndInsertProductIfNotPresentAndDosageUnitCodeNotPresent() throws Exception {
    Product product = new Product();
    product.setCode("P1");
    product.setPackSize(5);
    product.setDosageUnit(new DosageUnit(null, 23));
    when(productGroupService.validateAndReturn(product.getProductGroup())).thenReturn(null);
    when(productFormService.validateAndReturn(product.getForm())).thenReturn(null);
    when(repository.getByCode("P1")).thenReturn(null);

    service.save(product);

    verify(productGroupService).validateAndReturn(null);
    verify(productFormService).validateAndReturn(null);
    verify(repository, never()).getDosageUnitByCode(any(String.class));
    verify(repository).insert(product);
  }

  @Test
  public void shouldValidateAndUpdateProductIfPresentAndDosageUnitNotPresent() {
    Product product = new Product();
    product.setId(2L);
    product.setCode("proCode");
    product.setPackSize(5);

    when(productGroupService.validateAndReturn(product.getProductGroup())).thenReturn(null);
    when(productFormService.validateAndReturn(product.getForm())).thenReturn(null);
    List<ProgramProduct> programProducts = new ArrayList<>();
    when(programProductService.getByProductCode("proCode")).thenReturn(programProducts);

    service.save(product);

    verify(repository).update(product);
    verify(productGroupService).validateAndReturn(null);
    verify(productFormService).validateAndReturn(null);
  }

  @Test
  public void shouldNotifyIfActiveFlagsChangeFromTTtoTF() throws Exception {
    String productCode = "P1";
    Product product = new Product();
    product.setActive(false);
    product.setCode(productCode);
    product.setId(2L);
    product.setPackSize(5);

    final ProgramProduct existingProgramProduct = make(a(defaultProgramProduct, with(active, true), with(productActive, true)));
    when(productGroupService.validateAndReturn(product.getProductGroup())).thenReturn(null);
    when(productFormService.validateAndReturn(product.getForm())).thenReturn(null);
    when(programProductService.getByProductCode(productCode)).thenReturn(asList(existingProgramProduct));

    service.save(product);

    verify(programService).setFeedSendFlag(existingProgramProduct.getProgram(), true);
    verify(productGroupService).validateAndReturn(null);
    verify(productFormService).validateAndReturn(null);
  }

  @Test
  public void shouldNotifyIfActiveFlagsChangeFromTFtoTT() throws Exception {
    String productCode = "P1";
    Product product = new Product();
    product.setActive(true);
    product.setCode(productCode);
    product.setId(2L);
    product.setPackSize(5);

    final ProgramProduct tbProduct = make(a(defaultProgramProduct, with(programCode, "TB"), with(active, true), with(productActive, false)));
    final ProgramProduct hivProduct = make(a(defaultProgramProduct, with(programCode, "HIV"), with(active, true), with(productActive, false)));
    when(productGroupService.validateAndReturn(product.getProductGroup())).thenReturn(null);
    when(productFormService.validateAndReturn(product.getForm())).thenReturn(null);
    when(programProductService.getByProductCode(productCode)).thenReturn(asList(hivProduct, tbProduct));

    service.save(product);

    verify(programService).setFeedSendFlag(tbProduct.getProgram(), true);
    verify(programService).setFeedSendFlag(hivProduct.getProgram(), true);
    verify(productGroupService).validateAndReturn(null);
    verify(productFormService).validateAndReturn(null);
  }

  @Test
  public void shouldNotNotifyIfNotAnUpdate() throws Exception {
    String productCode = "P1";
    Product product = new Product();
    product.setActive(true);
    product.setCode(productCode);
    product.setPackSize(5);
    when(productGroupService.validateAndReturn(product.getProductGroup())).thenReturn(null);
    when(productFormService.validateAndReturn(product.getForm())).thenReturn(null);
    service.save(product);

    verify(programService, never()).setFeedSendFlag(any(Program.class), anyBoolean());
    verify(programProductService, never()).getByProductCode(anyString());
    verify(productGroupService).validateAndReturn(null);
    verify(productFormService).validateAndReturn(null);
  }

  @Test
  public void shouldNotNotifyIfActiveFlagsChangeFromFTtoFF() throws Exception {
    String productCode = "P1";
    Product product = new Product();
    product.setActive(false);
    product.setCode(productCode);
    product.setId(2L);
    product.setPackSize(5);

    when(productGroupService.validateAndReturn(product.getProductGroup())).thenReturn(null);
    when(productFormService.validateAndReturn(product.getForm())).thenReturn(null);
    final ProgramProduct existingProgramProduct = make(a(defaultProgramProduct, with(active, false), with(productActive, true)));
    when(programProductService.getByProductCode(productCode)).thenReturn(asList(existingProgramProduct));

    service.save(product);

    verify(programService, never()).setFeedSendFlag(any(Program.class), anyBoolean());
    verify(productGroupService).validateAndReturn(null);
    verify(productFormService).validateAndReturn(null);
  }

  @Test
  public void shouldNotNotifyIfActiveFlagsChangeFromFFtoFT() throws Exception {
    String productCode = "P1";
    Product product = new Product();
    product.setActive(true);
    product.setCode(productCode);
    product.setId(2L);
    product.setPackSize(5);

    when(productGroupService.validateAndReturn(product.getProductGroup())).thenReturn(null);
    when(productFormService.validateAndReturn(product.getForm())).thenReturn(null);
    final ProgramProduct existingProgramProduct = make(a(defaultProgramProduct, with(active, false), with(productActive, false)));
    when(programProductService.getByProductCode(productCode)).thenReturn(asList(existingProgramProduct));

    service.save(product);

    verify(programService, never()).setFeedSendFlag(any(Program.class), anyBoolean());
    verify(productGroupService).validateAndReturn(null);
    verify(productFormService).validateAndReturn(null);
  }

  @Test
  public void shouldGetAll() {
    service.getAllDosageUnits();

    verify(repository).getAllDosageUnits();
  }

  @Test
  public void shouldGetById() {
    service.getById(1L);

    verify(repository).getById(1L);
  }

  @Test
  public void shouldGetIdForCode() {
    service.getIdForCode("code");

    verify(repository).getIdByCode("code");
  }

  @Test
  public void shouldGetByCode() {
    service.getByCode("code");

    verify(repository).getByCode("code");
  }

  @Test
  public void shouldReturnIsActive() {
    service.isActive("code");

    verify(repository).isActive("code");
  }

  @Test
  public void shouldGetTotalSearchResultCount() {
    service.getTotalSearchResultCount("search-param");

    verify(repository).getTotalSearchResultCount("search-param");
  }
}
