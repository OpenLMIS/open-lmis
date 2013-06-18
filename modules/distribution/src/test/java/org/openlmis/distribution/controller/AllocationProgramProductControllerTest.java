/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.distribution.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.distribution.response.AllocationResponse;
import org.openlmis.distribution.service.AllocationProgramProductService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.distribution.controller.AllocationProgramProductController.PROGRAM_PRODUCT_LIST;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class AllocationProgramProductControllerTest {


  @Mock
  private AllocationProgramProductService service;

  @InjectMocks
  private AllocationProgramProductController controller;

  @Test
  public void shouldGetProgramProductsByProgram() throws Exception {
    List<AllocationProgramProduct> expectedProgramProductList = new ArrayList<>();
    Long programId = 1l;
    when(service.get(programId)).thenReturn(expectedProgramProductList);

    ResponseEntity<AllocationResponse> responseEntity = controller.getProgramProductsByProgram(programId);

    assertThat((List<AllocationProgramProduct>) responseEntity.getBody().getData().get(PROGRAM_PRODUCT_LIST), is(expectedProgramProductList));
    verify(service).get(programId);
  }

  @Test
  public void shouldInsertProgramProductISA() {
    ProgramProductISA programProductISA = new ProgramProductISA();
    Long programProductId = 1l;

    controller.insertIsa(programProductId, programProductISA);

    verify(service).insertISA(programProductISA);
    assertThat(programProductISA.getProgramProductId(), is(1l));
  }

  @Test
  public void shouldUpdateProgramProductISA() {
    ProgramProductISA programProductISA = new ProgramProductISA();
    Long isaId = 1l;
    Long programProductId = 2l;

    controller.updateIsa(isaId, programProductId, programProductISA);

    verify(service).updateISA(programProductISA);
  }
}
