/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.distribution.repository.AllocationProgramProductRepository;

import static org.mockito.Mockito.verify;

public class ProgramProductServiceTest {

  @InjectMocks
  private AllocationProgramProductService allocationProgramProductService;

  @Mock
  private AllocationProgramProductRepository programProductRepository;

  @Test
  public void shouldInsertProgramProductISAIfDoesNotExist() {
    ProgramProductISA programProductISA = new ProgramProductISA();
    Long programProductId = 1L;
    allocationProgramProductService.saveProgramProductISA(programProductId, programProductISA);
    verify(programProductRepository).insertProgramProductISA(programProductId, programProductISA);
  }

  @Test
  public void shouldUpdateProgramProductISAIfExists() {
    ProgramProductISA programProductISA = new ProgramProductISA();
    programProductISA.setId(1l);
    Long programProductId = 2L;
    allocationProgramProductService.saveProgramProductISA(programProductId, programProductISA);
    verify(programProductRepository).updateProgramProductISA(programProductISA);
  }
}
