/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.SupervisoryNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
@NoArgsConstructor
public class SupervisoryNodeHandler extends AbstractModelPersistenceHandler {

  public static final String DUPLICATE_SUPERVISORY_NODE = "Duplicate Supervisory Node found";
  private SupervisoryNodeService supervisoryNodeService;

  @Autowired
  public SupervisoryNodeHandler(SupervisoryNodeService supervisoryNodeService) {
    this.supervisoryNodeService = supervisoryNodeService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return supervisoryNodeService.getByCode((SupervisoryNode) record);
  }

  @Override
  protected void save(BaseModel record) {
    supervisoryNodeService.save((SupervisoryNode) record);
  }

  @Override
  protected String getDuplicateMessageKey() {
    return DUPLICATE_SUPERVISORY_NODE;
  }

}
