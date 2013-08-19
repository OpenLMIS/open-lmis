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
import org.openlmis.core.repository.mapper.SupplyLineMapperExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class SupplyLineRepositoryExtension  {

  private SupplyLineMapper supplyLineMapper;
  private SupplyLineMapperExtension supplyLineMapperExt;

  @Autowired
  public SupplyLineRepositoryExtension(SupplyLineMapperExtension supplyLineMapperExt) {
    this.supplyLineMapperExt = supplyLineMapperExt;
  }

    public List<SupplyLine> getAllSupplyLine(){
        return supplyLineMapperExt.getAllSupplyLine();
    }

    public SupplyLine getSupplylineById(Long id) {
        return supplyLineMapperExt.getSupplylineById(id);
    }

    public SupplyLine getSupplylineDetailById(Long id) {
        return supplyLineMapperExt.getSupplylineDetailById(id);
    }

    public void deleteById(Long supplylineId) {
        supplyLineMapperExt.deleteById(supplylineId);
    }

}
