/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.SupplyLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class SupplyLinePersistenceHandler extends AbstractModelPersistenceHandler {

  SupplyLineService supplyLineService;

  @Autowired
  public SupplyLinePersistenceHandler(SupplyLineService supplyLineService) {
    this.supplyLineService = supplyLineService;
  }

  @Override
  BaseModel getExisting(BaseModel record) {
    SupplyLine supplyLine = (SupplyLine) record;
    SupplyLine supplyLineFromDB = supplyLineService.getExisting(supplyLine);
    if (supplyLineFromDB != null) {
      if (supplyLineFromDB.getSupplyingFacility().getId().equals(supplyLine.getSupplyingFacility().getId())) {
        setMessageKey("error.duplicate.supply.line");
      } else {
        setMessageKey("error.redundant.warehouse");
      }
    }
    return supplyLineFromDB;
  }

  @Override
  protected void save(BaseModel record) {
    supplyLineService.save((SupplyLine) record);
  }


}