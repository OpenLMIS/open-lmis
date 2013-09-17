/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.search.criteria;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class RequisitionSearchCriteria {

  Long userId;
  Long facilityId;
  Long programId;
  Long periodId;
  boolean withoutLineItems;
  Date dateRangeStart;
  Date dateRangeEnd;

  public RequisitionSearchCriteria(Long facilityId, Long programId, Date periodStartDate, Date periodEndDate) {
    this(facilityId, programId);
    this.dateRangeStart = periodStartDate;
    this.dateRangeEnd = periodEndDate;
  }

  public RequisitionSearchCriteria(Long facilityId, Long programId, Long userId, Date dateRangeStart, Date dateRangeEnd) {
    this(facilityId, programId, dateRangeStart, dateRangeEnd);
    this.userId = userId;
  }

  public RequisitionSearchCriteria(Long facilityId, Long programId, Long periodId) {
    this(facilityId, programId);
    this.periodId = periodId;
  }

  public RequisitionSearchCriteria(Long facilityId, Long programId, Long periodId, boolean withoutLineItems) {
    this(facilityId, programId, periodId);
    this.withoutLineItems = withoutLineItems;
  }

  public RequisitionSearchCriteria(Long facilityId, Long programId) {
    this.facilityId = facilityId;
    this.programId = programId;
  }
}
