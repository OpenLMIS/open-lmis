/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
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

}
