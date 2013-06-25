/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.distribution.repository.AllocationProgramProductRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class AllocationProgramProductServiceTest {

  @InjectMocks
  private AllocationProgramProductService service;

  @Mock
  private AllocationProgramProductRepository repository;

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
  public void shouldGetProductsFilledWithIsa() throws Exception {
    final ProgramProduct programProduct = new ProgramProduct();
    programProduct.setId(1l);
    final ProgramProduct programProduct2 = new ProgramProduct();
    programProduct2.setId(2l);

    List<ProgramProduct> products = new ArrayList<ProgramProduct>() {{
      add(programProduct);
      add(programProduct2);
    }};
    when(programProductService.getByProgram(new Program(1l))).thenReturn(products);
    ProgramProductISA isa1 = new ProgramProductISA();
    when(repository.getIsa(1l)).thenReturn(isa1);
    ProgramProductISA isa2 = new ProgramProductISA();
    when(repository.getIsa(2l)).thenReturn(isa2);

    List<AllocationProgramProduct> returnedProducts = service.get(1l);

    assertThat(returnedProducts.get(0).getProgramProductISA(), is(isa1));
    assertThat(returnedProducts.get(1).getProgramProductISA(), is(isa2));
    verify(programProductService).getByProgram(new Program(1l));
    verify(repository, times(2)).getIsa(anyLong());
  }
}
