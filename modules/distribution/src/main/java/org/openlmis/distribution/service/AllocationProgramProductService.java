/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.service;

import lombok.NoArgsConstructor;
import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.distribution.repository.AllocationProgramProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@NoArgsConstructor
public class AllocationProgramProductService {

  @Autowired
  private AllocationProgramProductRepository programProductISARepository;

  public void saveProgramProductISA(Long programProductId, ProgramProductISA programProductISA) {
    if (programProductISA.getId() == null) {
      programProductISARepository.insertProgramProductISA(programProductId, programProductISA);
    } else {
      programProductISARepository.updateProgramProductISA(programProductISA);
    }
  }

  public List<AllocationProgramProduct> getProgramProductsWithISAByProgram(Long programId) {
    return programProductISARepository.getWithISAByProgram(programId);
  }

}
