/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.db.categories.UnitTests;
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
  public void shouldGetFacilityProgramProductWithIsaForAFacility() throws Exception {
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
    FacilityProgramProduct product = new FacilityProgramProduct(programProduct, facilityId, 34);

    repository.save(product);

    verify(mapper).removeFacilityProgramProductMapping(1L, facilityId);
    verify(mapper).insert(product);
  }

}
