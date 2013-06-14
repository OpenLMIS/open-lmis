/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProgramProductBuilder.defaultProgramProduct;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class ProgramProductServiceTest {
  @Rule
  public ExpectedException expectException = ExpectedException.none();

  @Mock
  private ProgramProductRepository programProductRepository;

  private ProgramProductService programProductService;

  @Before
  public void setUp() throws Exception {
    programProductService = new ProgramProductService(programProductRepository, null, null);
  }

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
    ProgramProduct programProduct = new ProgramProduct();

    programProductService.save(programProduct);

    verify(programProductRepository).save(programProduct);

  }

  @Test
  public void shouldThrowErrorIfProgramProductExistsWithSameTimeStamp() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();
    Date date = new Date();
    programProduct.setModifiedDate(date);
    expectException.expect(DataException.class);
    expectException.expectMessage("Duplicate Program Product found");

    doThrow(new DataException("Duplicate Program Product found")).when(programProductRepository).save(programProduct);

    programProductService.save(programProduct);
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
  public void shouldInsertProgramProductISAIfDoesNotExist() {
    ProgramProductISA programProductISA = new ProgramProductISA();
    Long programProductId = 1L;
    programProductService.saveProgramProductISA(programProductId, programProductISA);
    verify(programProductRepository).insertProgramProductISA(programProductId, programProductISA);
  }

  @Test
  public void shouldUpdateProgramProductISAIfExists() {
    ProgramProductISA programProductISA = new ProgramProductISA();
    programProductISA.setId(1l);
    Long programProductId = 2L;
    programProductService.saveProgramProductISA(programProductId, programProductISA);
    verify(programProductRepository).updateProgramProductISA(programProductISA);
  }
}
