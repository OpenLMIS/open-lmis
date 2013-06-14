/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.repository;

import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.distribution.repository.mapper.AllocationProgramProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllocationProgramProductRepository {

  @Autowired
  AllocationProgramProductMapper programProductISAMapper;

  public void insertProgramProductISA(Long programProductId, ProgramProductISA programProductISA) {
    programProductISAMapper.insertISA(programProductISA);
    programProductISAMapper.updateProgramProductForISA(programProductId, programProductISA);
  }

  public void updateProgramProductISA(ProgramProductISA programProductISA) {
    programProductISAMapper.updateISA(programProductISA);
  }

  public List<AllocationProgramProduct> getWithISAByProgram(Long programId) {
    return programProductISAMapper.getWithISAByProgram(programId);
  }
}
