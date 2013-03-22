/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.repository.SupplyLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class SupplyLineService {


  private SupplyLineRepository supplyLineRepository;

  @Autowired
  public SupplyLineService(SupplyLineRepository supplyLineRepository) {
    this.supplyLineRepository = supplyLineRepository;
  }

  public SupplyLine getSupplyLineBy(SupervisoryNode supervisoryNode, Program program) {
    return supplyLineRepository.getSupplyLineBy(supervisoryNode, program);
  }
}
