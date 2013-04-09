/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.repository.SupplyLineRepository;
import org.openlmis.core.service.SupplyLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@NoArgsConstructor
public class SupplyLinePersistenceHandler extends AbstractModelPersistenceHandler {

  public static final String DUPLICATE_ENTRY_FOR_SUPPLY_LINE_FOUND = "Duplicate entry for Supply Line found";

  SupplyLineService supplyLineService;

  @Autowired
  public SupplyLinePersistenceHandler(SupplyLineService supplyLineService) {
    this.supplyLineService = supplyLineService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return supplyLineService.getExisting(((SupplyLine)record));
  }

  @Override
  protected void save(BaseModel record) {
    supplyLineService.save((SupplyLine) record);
  }

  @Override
  protected String getDuplicateMessageKey() {
    return DUPLICATE_ENTRY_FOR_SUPPLY_LINE_FOUND;
  }

}