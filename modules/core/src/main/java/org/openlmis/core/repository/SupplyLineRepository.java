/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.repository.mapper.SupplyLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class SupplyLineRepository {

  private SupplyLineMapper supplyLineMapper;

  @Autowired
  public SupplyLineRepository(SupplyLineMapper supplyLineMapper) {
    this.supplyLineMapper = supplyLineMapper;
  }

  public void insert(SupplyLine supplyLine) {
    supplyLineMapper.insert(supplyLine);
  }

  public SupplyLine getSupplyLineBy(SupervisoryNode supervisoryNode, Program program) {
    return supplyLineMapper.getSupplyLineBy(supervisoryNode, program);
  }

  public void update(SupplyLine supplyLine) {
    supplyLineMapper.update(supplyLine);
  }

  public SupplyLine get(Long id) {
        return supplyLineMapper.get(id);
  }

  public SupplyLine getSupplyLineBySupervisoryNodeProgramAndFacility(SupplyLine supplyLine) {
    return supplyLineMapper.getSupplyLineBySupervisoryNodeProgramAndFacility(supplyLine);
  }


}
