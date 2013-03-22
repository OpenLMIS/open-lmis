/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityApprovedProductRepository;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityApprovedProductBuilder.*;


@RunWith(MockitoJUnitRunner.class)
public class FacilityApprovedProductServiceTest {

  @Mock
  private FacilityApprovedProductRepository facilityApprovedProductRepository;

  @Mock
  private ProgramService programService;

  @Mock
  private ProductService productService;

  @Mock
  private ProgramProductService programProductService;

  @Rule
  public ExpectedException expectedException = none();

  @Test
  public void shouldSaveFacilityApprovedProduct() throws Exception {

    FacilityApprovedProductService facilityApprovedProductService = new FacilityApprovedProductService(facilityApprovedProductRepository, programService, productService, programProductService);
    FacilityApprovedProduct facilityApprovedProduct = make(a(defaultFacilityApprovedProduct));

    Integer programId = 45;
    Integer productId = 10;
    Integer programProductId = 100;

    when(programService.getIdForCode(defaultProgramCode)).thenReturn(programId);
    when(productService.getIdForCode(defaultProductCode)).thenReturn(productId);
    when(programProductService.getIdByProgramIdAndProductId(programId, productId)).thenReturn(100);

    facilityApprovedProductService.save(facilityApprovedProduct);

    verify(programService).getIdForCode(defaultProgramCode);
    verify(productService).getIdForCode(defaultProductCode);
    verify(programProductService).getIdByProgramIdAndProductId(programId, productId);
    verify(facilityApprovedProductRepository).insert(facilityApprovedProduct);

    assertThat(facilityApprovedProduct.getProgramProduct().getProgram().getId(), is(programId));
    assertThat(facilityApprovedProduct.getProgramProduct().getProduct().getId(), is(productId));
    assertThat(facilityApprovedProduct.getProgramProduct().getId(), is(programProductId));
  }

  @Test
  public void shouldNotSaveFacilityApprovedProductAndThrowAnExceptionWhenProgramDoesNotExist() throws Exception {
    FacilityApprovedProductService facilityApprovedProductService = new FacilityApprovedProductService(facilityApprovedProductRepository, programService, productService, programProductService);
    FacilityApprovedProduct facilityApprovedProduct = make(a(defaultFacilityApprovedProduct));

    doThrow(new DataException("abc")).when(programService).getIdForCode(defaultProgramCode);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("abc");

    facilityApprovedProductService.save(facilityApprovedProduct);
    verify(programService).getIdForCode(defaultProgramCode);

    verify(facilityApprovedProductRepository, never()).insert(facilityApprovedProduct);
  }

  @Test
  public void shouldNotSaveFacilityApprovedProductAndThrowAnExceptionWhenProductDoesNotExist() throws Exception {
    FacilityApprovedProductService facilityApprovedProductService = new FacilityApprovedProductService(facilityApprovedProductRepository, programService, productService, programProductService);
    FacilityApprovedProduct facilityApprovedProduct = make(a(defaultFacilityApprovedProduct));

    doThrow(new DataException("abc")).when(productService).getIdForCode(defaultProductCode);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("abc");

    facilityApprovedProductService.save(facilityApprovedProduct);

    verify(productService).getIdForCode(defaultProgramCode);
    verify(facilityApprovedProductRepository, never()).insert(facilityApprovedProduct);
  }

  @Test
  public void shouldNotSaveFacilityApprovedProductAndThrowAnExceptionWhenProgramProductDoesNotExist() throws Exception {
    FacilityApprovedProductService facilityApprovedProductService = new FacilityApprovedProductService(facilityApprovedProductRepository, programService, productService, programProductService);
    FacilityApprovedProduct facilityApprovedProduct = make(a(defaultFacilityApprovedProduct));

    Integer programId = 1;
    Integer productId = 2;

    when(programService.getIdForCode(defaultProgramCode)).thenReturn(programId);
    when(productService.getIdForCode(defaultProductCode)).thenReturn(productId);

    doThrow(new DataException("abc")).when(programProductService).getIdByProgramIdAndProductId(programId, productId);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("abc");

    facilityApprovedProductService.save(facilityApprovedProduct);

    verify(programProductService).getIdByProgramIdAndProductId(programId, productId);
    verify(facilityApprovedProductRepository, never()).insert(facilityApprovedProduct);
  }
}
