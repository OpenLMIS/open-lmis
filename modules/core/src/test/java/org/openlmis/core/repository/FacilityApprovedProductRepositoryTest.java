/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityApprovedProductMapper;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Calendar;
import java.util.Date;

import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
import static org.openlmis.core.repository.FacilityApprovedProductRepository.FACILITY_TYPE_DOES_NOT_EXIST;

@RunWith(MockitoJUnitRunner.class)
public class FacilityApprovedProductRepositoryTest {

  @Rule
  public ExpectedException expectedException = none();

  @Mock
  private FacilityApprovedProductMapper facilityApprovedProductMapper;

  private FacilityApprovedProductRepository facilityApprovedProductRepository;

  @Mock
  private FacilityMapper facilityMapper;

  @Before
  public void setUp() {
    facilityApprovedProductRepository = new FacilityApprovedProductRepository(facilityApprovedProductMapper, facilityMapper, null);
  }

  @Test
  public void shouldInsertAFacilitySupportedProduct() {
    FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct();
    ProgramProduct programProduct = new ProgramProduct();
    programProduct.setId(1);
    facilityApprovedProduct.setProgramProduct(programProduct);
    facilityApprovedProduct.setFacilityType(new FacilityType("warehouse"));

    when(facilityApprovedProductMapper.getFacilityApprovedProductIdByProgramProductAndFacilityTypeCode(1, "warehouse")).thenReturn(null);

    facilityApprovedProductRepository.insert(facilityApprovedProduct);


    verify(facilityApprovedProductMapper).insert(facilityApprovedProduct);
  }


  @Test
  public void shouldThrowExceptionsWhenFacilityCodeDoesNotExist() throws Exception {
    FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct();
    ProgramProduct programProduct = new ProgramProduct();
    programProduct.setId(1);
    facilityApprovedProduct.setProgramProduct(programProduct);
    facilityApprovedProduct.setFacilityType(new FacilityType("warehouse"));

    when(facilityApprovedProductMapper.getFacilityApprovedProductIdByProgramProductAndFacilityTypeCode(1, "warehouse")).thenReturn(null);

    doThrow(DataIntegrityViolationException.class).when(facilityApprovedProductMapper).insert(facilityApprovedProduct);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(FACILITY_TYPE_DOES_NOT_EXIST);

    facilityApprovedProductRepository.insert(facilityApprovedProduct);
  }

  @Test
  public void shouldGetFullSupplyFacilityApprovedProducts(){
    facilityApprovedProductRepository.getFullSupplyProductsByFacilityAndProgram(5,8);
    verify(facilityApprovedProductMapper).getProductsByFacilityProgramAndFullSupply(5, 8, true);
  }

  @Test
  public void shouldGetNonFullSupplyFacilityApprovedProducts(){
    facilityApprovedProductRepository.getNonFullSupplyProductsByFacilityAndProgram(5,8);
    verify(facilityApprovedProductMapper).getProductsByFacilityProgramAndFullSupply(5, 8, false);
  }

  @Test
  public void shouldThrowErrorIfFacilityProductExistsWithSameTimeStamp() throws Exception {
    FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct();
    ProgramProduct programProduct = new ProgramProduct();
    facilityApprovedProduct.setProgramProduct(programProduct);
    programProduct.setId(1);

    Date modifiedDate = new Date();
    facilityApprovedProduct.setModifiedDate(modifiedDate);

    FacilityApprovedProduct savedFacilityApprovedProduct = new FacilityApprovedProduct();
    savedFacilityApprovedProduct.setModifiedDate(modifiedDate);

    facilityApprovedProduct.setFacilityType(new FacilityType("warehouse"));

    when(facilityApprovedProductMapper.getFacilityApprovedProductIdByProgramProductAndFacilityTypeCode(1, "warehouse")).thenReturn(savedFacilityApprovedProduct);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("facilityApprovedProduct.duplicate.found");

    facilityApprovedProductRepository.insert(facilityApprovedProduct);
  }

  @Test
  public void shouldUpdateFacilityApprovedProductIfExists() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();
    programProduct.setId(1);

    FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct();
    facilityApprovedProduct.setProgramProduct(programProduct);

    FacilityType facilityType = new FacilityType();
    facilityApprovedProduct.setFacilityType(facilityType);

    Calendar today = Calendar.getInstance();
    facilityApprovedProduct.setModifiedDate(today.getTime());

    FacilityApprovedProduct savedFacilityApprovedProduct = new FacilityApprovedProduct();
    today.add(Calendar.DATE, -1);
    savedFacilityApprovedProduct.setModifiedDate(today.getTime());
    facilityApprovedProduct.setFacilityType(new FacilityType("warehouse"));

    when(facilityApprovedProductMapper.getFacilityApprovedProductIdByProgramProductAndFacilityTypeCode(1, "warehouse")).thenReturn(savedFacilityApprovedProduct);
    when(facilityMapper.getFacilityTypeIdForCode(null)).thenReturn(1);
    doNothing().when(facilityApprovedProductMapper).updateFacilityApprovedProduct(facilityApprovedProduct);

    facilityApprovedProductRepository.insert(facilityApprovedProduct);

    verify(facilityApprovedProductMapper).updateFacilityApprovedProduct(facilityApprovedProduct);
  }
}
