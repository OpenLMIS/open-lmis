
/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionStatusChange {
  private Long id;

  private Long rnrId;
  private RnrStatus status;
  private User createdByUser;
  private Long createdBy;
  private Date createdDate;

  public RequisitionStatusChange(Rnr requisition) {
    this.rnrId = requisition.getId();
    this.status = requisition.getStatus();
    this.createdBy = requisition.getModifiedBy();
  }
}
