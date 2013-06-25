/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.repository;

import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.distribution.repository.mapper.IsaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllocationProgramProductRepository {

  @Autowired
  IsaMapper isaMapper;

  public void insertISA(ProgramProductISA programProductISA) {
    isaMapper.insert(programProductISA);
  }

  public void updateISA(ProgramProductISA programProductISA) {
    isaMapper.update(programProductISA);
  }

  public ProgramProductISA getIsa(Long id) {
    return isaMapper.getIsa(id);
  }
}
