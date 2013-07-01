/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.repository.FacilityProgramProductRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityProgramProductServiceTest {

  @InjectMocks
  private FacilityProgramProductService service;

  @Mock
  private FacilityProgramProductRepository repository;

  @Mock
  ProgramProductService programProductService;

  @Test
  public void shouldInsertIsa() throws Exception {
    ProgramProductISA isa = new ProgramProductISA();
    service.insertISA(isa);
    verify(repository).insertISA(isa);
  }

  @Test
  public void shouldUpdateIsa() throws Exception {
    ProgramProductISA isa = new ProgramProductISA();
    service.updateISA(isa);
    verify(repository).updateISA(isa);
  }

  @Test
  public void shouldGetProductsFilledWithIsaForAFacility() throws Exception {
    long facilityId = 2l;
    final ProgramProduct programProduct = new ProgramProduct();
    programProduct.setId(1l);
    final ProgramProduct programProduct2 = new ProgramProduct();
    programProduct2.setId(2l);

    List<ProgramProduct> products = new ArrayList<ProgramProduct>() {{
      add(programProduct);
      add(programProduct2);
    }};
    when(programProductService.getByProgram(new Program(1l))).thenReturn(products);

    FacilityProgramProduct facilityProduct1 = new FacilityProgramProduct(programProduct, 2l, 34);
    when(repository.getOverriddenIsa(programProduct.getId(), facilityId)).thenReturn(34);

    FacilityProgramProduct facilityProduct2 = new FacilityProgramProduct(programProduct2, 2l, 44);
    when(repository.getOverriddenIsa(programProduct2.getId(), facilityId)).thenReturn(44);

    List<FacilityProgramProduct> returnedProducts = service.getForProgramAndFacility(1l, facilityId);

    assertThat(returnedProducts.get(0), is(facilityProduct1));
    assertThat(returnedProducts.get(0).getOverriddenIsa(), is(34));

    assertThat(returnedProducts.get(1), is(facilityProduct2));
    assertThat(returnedProducts.get(1).getOverriddenIsa(), is(44));

    verify(programProductService).getByProgram(new Program(1l));
    verify(repository).getOverriddenIsa(programProduct.getId(), facilityId);
    verify(repository).getOverriddenIsa(programProduct2.getId(), facilityId);
  }

  @Test
  public void shouldSaveFacilityProgramProductList() throws Exception {
    final FacilityProgramProduct facilityProduct1 = spy(new FacilityProgramProduct());
    facilityProduct1.setId(1l);
    final FacilityProgramProduct facilityProduct2 = spy(new FacilityProgramProduct());
    facilityProduct1.setId(2l);
    List<FacilityProgramProduct> facilityProgramProducts = new ArrayList<FacilityProgramProduct>() {{
      add(facilityProduct1);
      add(facilityProduct2);
    }};

    service.saveOverriddenIsa(1l, facilityProgramProducts);

    verify(repository).save(facilityProduct1);
    verify(repository).save(facilityProduct2);
  }


  @Test
  public void shouldGetFacilityProgramProductsForFacilityAndProgram() throws Exception {

    List<FacilityProgramProduct> facilityProgramProducts = new ArrayList<>();
    Long facilityId = 1l;
    Long programId = 1l;
    when(repository.getByFacilityAndProgram(facilityId, programId)).thenReturn(facilityProgramProducts);

    List<FacilityProgramProduct> returnedFacilityProgramProducts = service.getByFacilityAndProgram(facilityId, programId);

    assertThat(returnedFacilityProgramProducts, is(facilityProgramProducts));
    verify(repository).getByFacilityAndProgram(facilityId, programId);

  }
}
