/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.distribution.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.distribution.repository.mapper.FacilityProgramProductMapper;
import org.openlmis.distribution.repository.mapper.IsaMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class AllocationProgramProductRepositoryTest {

  @InjectMocks
  AllocationProgramProductRepository repository;

  @Mock
  IsaMapper isaMapper;

  @Mock
  FacilityProgramProductMapper mapper;


  @Test
  public void shouldInsertISA() throws Exception {
    ProgramProductISA isa = new ProgramProductISA();
    repository.insertISA(isa);
    verify(isaMapper).insert(isa);
  }

  @Test
  public void shouldUpdateISA() throws Exception {
    ProgramProductISA isa = new ProgramProductISA();
    repository.updateISA(isa);
    verify(isaMapper).update(isa);
  }

  @Test
  public void shouldGetIsa() throws Exception {
    ProgramProductISA expectedIsa = new ProgramProductISA();
    when(isaMapper.getIsa(1l)).thenReturn(expectedIsa);

    ProgramProductISA isa = repository.getIsa(1l);

    verify(isaMapper).getIsa(1l);
    assertThat(expectedIsa, is(isa));
  }

  @Test
  public void shouldGetAllocationProgramProductWithIsa() throws Exception {
    ProgramProductISA expectedIsa = new ProgramProductISA();
    when(isaMapper.getIsa(1l)).thenReturn(expectedIsa);

    AllocationProgramProduct allocationProgramProduct = repository.getByProgramProductId(1L);

    assertThat(allocationProgramProduct.getProgramProductIsa(), is(expectedIsa));
    assertThat(allocationProgramProduct.getProgramProductId(), is(1l));
    verify(isaMapper).getIsa(1L);
  }

  @Test
  public void shouldGetAllocationProgramProductWithIsaForAFacility() throws Exception {
    long programProductId = 1L;
    long facilityId = 2L;
    when(mapper.getOverriddenIsa(programProductId, facilityId)).thenReturn(34);

    Integer overriddenIsa = repository.getOverriddenIsa(programProductId, facilityId);

    assertThat(overriddenIsa, is(34));
    verify(mapper).getOverriddenIsa(programProductId, facilityId);
  }


  @Test
  public void shouldReplaceAnyExistingOverriddenIsaWithNewOne() throws Exception {
    long programProductId = 1L;
    long facilityId = 2L;
    AllocationProgramProduct product = new AllocationProgramProduct(programProductId, facilityId, 34, null);

    repository.save(product);

    verify(mapper).removeFacilityProgramProductMapping(facilityId, programProductId);
    verify(mapper).insert(product);
  }
}
