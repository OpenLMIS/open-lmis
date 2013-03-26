/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramProductRepository;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProgramProductBuilder.defaultProgramProduct;

@RunWith(MockitoJUnitRunner.class)
public class ProgramProductServiceTest {
  @Rule
  public ExpectedException expectException = ExpectedException.none();

  @Mock
  private ProgramProductRepository programProductRepository;

  private ProgramProductService programProductService;

  @Before
  public void setUp() throws Exception {
    programProductService = new ProgramProductService(programProductRepository);
  }

  @Test
  public void shouldUpdateCurrentPriceOfProgramProductCodeCombinationAndUpdatePriceHistory() throws Exception {
    ProgramProduct programProduct = make(a(defaultProgramProduct));
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, new Money("1"), "source");
    programProductPrice.setModifiedBy(1);

    ProgramProduct returnedProgramProduct = new ProgramProduct();
    returnedProgramProduct.setId(123);
    when(programProductRepository.getProgramProductByProgramAndProductCode(programProduct)).thenReturn(returnedProgramProduct);

    programProductService.updateProgramProductPrice(programProductPrice);

    assertThat(programProductPrice.getProgramProduct().getId(), is(123));
    assertThat(programProductPrice.getProgramProduct().getModifiedBy(), is(1));
    verify(programProductRepository).getProgramProductByProgramAndProductCode(programProduct);
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
  public void shouldInsertProgramProduct() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();

    programProductService.insert(programProduct);

    verify(programProductRepository).insert(programProduct);

  }

  @Test
  public void shouldThrowErrorIfProgramProductExistsWithSameTimeStamp() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();
    Date date = new Date();
    programProduct.setModifiedDate(date);
    expectException.expect(DataException.class);
    expectException.expectMessage("Duplicate Program Product found");

    doThrow(new DataException("Duplicate Program Product found")).when(programProductRepository).insert(programProduct);

    programProductService.insert(programProduct);
  }
}
