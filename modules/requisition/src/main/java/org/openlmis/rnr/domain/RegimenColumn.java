/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegimenColumn extends Column {

  private Long programId;

  private String dataType;

  public RegimenColumn(Long programId, String name, String label, String dataType, Boolean visible, Long createdBy) {
    super(name, label, visible);
    this.programId = programId;
    this.dataType = dataType;
    this.createdBy = createdBy;
  }

  @Override
  public Integer getColumnWidth() {
    if (this.name.equals("remarks")) {
      return 80;
    }
    return 40;
  }
}
