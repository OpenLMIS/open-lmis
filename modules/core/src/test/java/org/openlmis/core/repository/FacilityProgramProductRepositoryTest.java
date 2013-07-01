/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.domain.AllocationProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.repository.mapper.FacilityProgramProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductIsaMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class FacilityProgramProductRepositoryTest {

  @InjectMocks
  FacilityProgramProductRepository repository;

  @Mock
  ProgramProductIsaMapper programProductIsaMapper;

  @Mock
  FacilityProgramProductMapper mapper;


  @Test
  public void shouldInsertISA() throws Exception {
    ProgramProductISA isa = new ProgramProductISA();
    repository.insertISA(isa);
    verify(programProductIsaMapper).insert(isa);
  }

  @Test
  public void shouldUpdateISA() throws Exception {
    ProgramProductISA isa = new ProgramProductISA();
    repository.updateISA(isa);
    verify(programProductIsaMapper).update(isa);
  }

  @Test
  public void shouldGetIsa() throws Exception {
    ProgramProductISA expectedIsa = new ProgramProductISA();
    when(programProductIsaMapper.getIsaByProgramProductId(1l)).thenReturn(expectedIsa);

    ProgramProductISA isa = repository.getIsaByProgramProductId(1l);

    verify(programProductIsaMapper).getIsaByProgramProductId(1l);
    assertThat(expectedIsa, is(isa));
  }

  @Test
  public void shouldGetAllocationProgramProductWithIsaForAFacility() throws Exception {
    Long programProductId = 1L;
    Long facilityId = 2L;
    when(mapper.getOverriddenIsa(programProductId, facilityId)).thenReturn(34);

    Integer overriddenIsa = repository.getOverriddenIsa(programProductId, facilityId);

    assertThat(overriddenIsa, is(34));
    verify(mapper).getOverriddenIsa(programProductId, facilityId);
  }


  @Test
  public void shouldReplaceAnyExistingOverriddenIsaWithNewOne() throws Exception {
    Long facilityId = 2L;
    ProgramProduct programProduct = new ProgramProduct();
    programProduct.setId(1l);
    AllocationProgramProduct product = new AllocationProgramProduct(programProduct, facilityId, 34);

    repository.save(product);

    verify(mapper).removeFacilityProgramProductMapping(1L, facilityId);
    verify(mapper).insert(product);
  }

  @Test
  public void shouldGetAllocationProgramProductsForFacilityAndProgram() throws Exception {

    List<AllocationProgramProduct> allocationProgramProducts = new ArrayList<>();
    Long facilityId = 1l;
    Long programId = 1l;
    when(mapper.getByFacilityAndProgram(facilityId, programId)).thenReturn(allocationProgramProducts);

    List<AllocationProgramProduct> returnedAllocationProgramProducts = repository.getByFacilityAndProgram(facilityId, programId);

    assertThat(returnedAllocationProgramProducts, is(allocationProgramProducts));
    verify(mapper).getByFacilityAndProgram(facilityId, programId);

  }
}
