/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("requisitionGroupHandler")
@NoArgsConstructor
public class RequisitionGroupHandler extends AbstractModelPersistenceHandler {

  private RequisitionGroupService requisitionGroupService;

  @Autowired
  public RequisitionGroupHandler(RequisitionGroupService requisitionGroupService) {
    this.requisitionGroupService = requisitionGroupService;
  }

  @Override
  protected Importable getExisting(Importable importable) {
    return null;
  }

  @Override
  protected void save(Importable existingRecord, Importable currentRecord, AuditFields auditFields) {
    RequisitionGroup requisitionGroup = (RequisitionGroup) currentRecord;
    requisitionGroup.setModifiedBy(auditFields.getUser());
    requisitionGroup.setModifiedDate(new Date());
    requisitionGroupService.save(requisitionGroup);}

  @Override
  protected void throwExceptionIfAlreadyProcessedInCurrentUpload(Importable importable, AuditFields auditFields) {
  }

}
