/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderBatch {
  private Integer id;
  private Date createTimeStamp;
  private Integer createdByUserId;
  private Facility supplyingFacility;
  private List<Rnr> requisitions;

  public OrderBatch(Facility supplyingFacility, Integer orderedBy) {
    this.supplyingFacility = supplyingFacility;
    this.createdByUserId = orderedBy;
  }
}
