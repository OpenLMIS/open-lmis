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
import static java.util.Collections.EMPTY_LIST;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProgramProductBuilder.*;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class ProgramProductServiceTest {

  @Rule
  public ExpectedException expectException = ExpectedException.none();

  @Mock
  private ProgramProductRepository programProductRepository;

  @Mock
  private ProductService productService;

  @Mock
  private ProgramService programService;

  @InjectMocks
  private ProgramProductService programProductService;

  @Mock
  private ProgramRepository programRepository;

  @Mock
  private FacilityRepository facilityRepository;

  @Test
  public void shouldUpdateCurrentPriceOfProgramProductCodeCombinationAndUpdatePriceHistory() throws Exception {
    ProgramProduct programProduct = make(a(defaultProgramProduct));
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, new Money("1"), "source");
    programProductPrice.setModifiedBy(1L);

    ProgramProduct returnedProgramProduct = new ProgramProduct();
    returnedProgramProduct.setId(123L);
    when(programProductRepository.getByProgramAndProductCode(programProduct)).thenReturn(returnedProgramProduct);

    programProductService.updateProgramProductPrice(programProductPrice);

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

    programProductService.updateProgramProductPrice(programProductPrice);
  }

  @Test
  public void shouldThrowExceptionIfProgramProductByProgramAndProductCodesNotFound() throws Exception {
    ProgramProduct programProduct = make(a(defaultProgramProduct));
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, new Money("1"), "source");
    programProductPrice.setModifiedBy(1L);

    when(programProductRepository.getByProgramAndProductCode(programProduct)).thenReturn(null);

    expectException.expect(DataException.class);
    expectException.expectMessage("programProduct.product.program.invalid");

    programProductService.updateProgramProductPrice(programProductPrice);
  }

  @Test
  public void shouldInsertProgramProduct() throws Exception {
    ProgramProduct programProduct = make(a(defaultProgramProduct));

    programProductService.save(programProduct);

    verify(programProductRepository).save(programProduct);
  }

  @Test
  public void shouldNotifyIfNewActiveProductAddedToAProgramAndProgramProductIsActive() throws Exception {
    String product = "product";
    ProgramProduct programProduct = make(a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
      with(active, true)));
    programProduct.setId(null);
    when(productService.isActive(product)).thenReturn(true);

    programProductService.save(programProduct);

    verify(programService).setFeedSendFlag(programProduct.getProgram(), true);
  }

  @Test
  public void shouldNotNotifyIfNewProductAddedWhichIsGloballyInactive() throws Exception {
    String product = "product";
    ProgramProduct programProduct = make(a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
      with(active, true)));
    when(productService.isActive(product)).thenReturn(false);

    programProductService.save(programProduct);

    verify(programService, never()).setFeedSendFlag(programProduct.getProgram(), false);
  }

  @Test
  public void shouldNotNotifyIfNewInactiveProductAddedEvenIfItIsGloballyActive() throws Exception {
    String product = "product";
    ProgramProduct programProduct = make(a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
      with(active, false)));
    when(productService.isActive(product)).thenReturn(true);

    programProductService.save(programProduct);

    verify(programService, never()).setFeedSendFlag(programProduct.getProgram(), false);
  }

  @Test
  public void shouldNotNotifyIfNewInactiveProductAddedAlsoInactiveGlobally() throws Exception {
    String product = "product";
    ProgramProduct programProduct = make(a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
      with(active, false)));
    when(productService.isActive(product)).thenReturn(false);

    programProductService.save(programProduct);

    verify(programService, never()).setFeedSendFlag(programProduct.getProgram(), false);
  }

  @Test
  public void shouldNotifyOnUpdateIfActiveFlagsChangeFromTTAndBecomeFT() throws Exception {
    String product = "product";
    Long existingProgramProductId = 2L;
    ProgramProduct programProductForUpdate = make(a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
      with(active, false)));
    programProductForUpdate.setId(existingProgramProductId);

    ProgramProduct existingProgramProduct = make(a(defaultProgramProduct, with(active, true), with(productActive, true)));
    when(programProductRepository.getById(existingProgramProductId)).thenReturn(existingProgramProduct);

    programProductService.save(programProductForUpdate);

    verify(programService).setFeedSendFlag(programProductForUpdate.getProgram(), true);
  }

  @Test
  public void shouldNotifyOnUpdateIfActiveFlagsChangeFromFTAndBecomeTT() throws Exception {
    String product = "product";
    Long existingProgramProductId = 2L;
    ProgramProduct programProductForUpdate = make(a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
      with(active, true)));
    programProductForUpdate.setId(existingProgramProductId);

    ProgramProduct existingProgramProduct = make(a(defaultProgramProduct, with(active, false), with(productActive, true)));
    when(programProductRepository.getById(existingProgramProductId)).thenReturn(existingProgramProduct);

    programProductService.save(programProductForUpdate);

    verify(programService).setFeedSendFlag(programProductForUpdate.getProgram(), true);
  }

  @Test
  public void shouldNotNotifyOnUpdateIfActiveFlagsChangeFromFFAndBecomeFF() throws Exception {
    String product = "product";
    Long existingProgramProductId = 2L;
    ProgramProduct programProductForUpdate = make(a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
      with(active, false)));
    programProductForUpdate.setId(existingProgramProductId);

    ProgramProduct existingProgramProduct = make(a(defaultProgramProduct, with(active, false), with(productActive, false)));
    when(programProductRepository.getById(existingProgramProductId)).thenReturn(existingProgramProduct);

    programProductService.save(programProductForUpdate);

    verify(programService, never()).setFeedSendFlag(programProductForUpdate.getProgram(), true);
  }

  @Test
  public void shouldNotNotifyOnUpdateIfActiveFlagsChangeFromTFAndBecomeTF() throws Exception {
    String product = "product";
    Long existingProgramProductId = 2L;
    ProgramProduct programProductForUpdate = make(a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
      with(active, true)));
    programProductForUpdate.setId(existingProgramProductId);

    ProgramProduct existingProgramProduct = make(a(defaultProgramProduct, with(active, true), with(productActive, false)));
    when(programProductRepository.getById(existingProgramProductId)).thenReturn(existingProgramProduct);

    programProductService.save(programProductForUpdate);

    verify(programService, never()).setFeedSendFlag(programProductForUpdate.getProgram(), true);
  }

  @Test
  public void shouldNotNotifyOnUpdateIfActiveFlagsChangeFromTFAndBecomeFF() throws Exception {
    String product = "product";
    Long existingProgramProductId = 2L;
    ProgramProduct programProductForUpdate = make(a(defaultProgramProduct, with(productCode, product), with(ProgramProductBuilder.programCode, "program"),
      with(active, false)));
    programProductForUpdate.setId(existingProgramProductId);

    ProgramProduct existingProgramProduct = make(a(defaultProgramProduct, with(active, true), with(productActive, false)));
    when(programProductRepository.getById(existingProgramProductId)).thenReturn(existingProgramProduct);

    programProductService.save(programProductForUpdate);

    verify(programService, never()).setFeedSendFlag(programProductForUpdate.getProgram(), true);
  }

  @Test
  public void shouldGetAllProductsByProgram() {
    Program program = new Program();
    List<ProgramProduct> expectedProgramProducts = new ArrayList<>();
    when(programProductRepository.getByProgram(program)).thenReturn(expectedProgramProducts);

    List<ProgramProduct> programProducts = programProductService.getByProgram(program);

    assertThat(programProducts, is(expectedProgramProducts));
    verify(programProductRepository).getByProgram(program);
  }

  @Test
  public void shouldGetAllProgramProductsByProductCode() {
    List<ProgramProduct> expectedProgramProducts = new ArrayList<>();
    when(programProductRepository.getByProductCode("code")).thenReturn(expectedProgramProducts);

    List<ProgramProduct> programProducts = programProductService.getByProductCode("code");

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

    List<ProgramProduct> programProducts = programProductService.getProgramProductsBy(" P1", " warehouse");

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

    List<ProgramProduct> programProducts = programProductService.getProgramProductsBy("P1", null);

    assertThat(programProducts, is(expectedProgramProducts));
    verify(facilityRepository, never()).getFacilityTypeByCode(any(FacilityType.class));
    verify(programRepository).getIdByCode("P1");
    verify(programProductRepository).getProgramProductsBy(10L, null);
  }

  @Test
  public void shouldGetActiveProgramProductsForAProgram() throws Exception {
    List<ProgramProduct> expectedProgramProducts = EMPTY_LIST;
    when(programProductRepository.getActiveByProgram(2L)).thenReturn(expectedProgramProducts);

    List<ProgramProduct> programProducts = programProductService.getActiveByProgram(2L);

    verify(programProductRepository).getActiveByProgram(2L);
    assertThat(programProducts, is(expectedProgramProducts));
  }
}
