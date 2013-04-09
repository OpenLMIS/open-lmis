/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.service.RequisitionGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("requisitionGroupHandler")
@NoArgsConstructor
public class RequisitionGroupHandler extends AbstractModelPersistenceHandler {

  public static final String DUPLICATE_REQUISITION_GROUP_CODE = "Duplicate Requisition Group Code found";

  private RequisitionGroupService requisitionGroupService;

  @Autowired
  public RequisitionGroupHandler(RequisitionGroupService requisitionGroupService) {
    this.requisitionGroupService = requisitionGroupService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return this.requisitionGroupService.getByCode((RequisitionGroup) record);
  }

  @Override
  protected void save(BaseModel record) {
    requisitionGroupService.save((RequisitionGroup) record);
  }

  @Override
  protected String getDuplicateMessageKey() {
    return DUPLICATE_REQUISITION_GROUP_CODE;
  }
}
