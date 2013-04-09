/*
 * CCommtrackRequisitionageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.List;

@Data
@NoArgsConstructor
public class Report {

  public static final String ERROR_COMMTRACK_MANDATORY_MISSING = "error.restapi.mandatory.missing";

  private Integer facilityId;
  private Integer programId;
  private Integer periodId;
  private Integer userId;

  private List<RnrLineItem> products;

  public void validate() {
    if (facilityId == null || programId == null || periodId == null || userId == null)
      throw new DataException(ERROR_COMMTRACK_MANDATORY_MISSING);
  }
}