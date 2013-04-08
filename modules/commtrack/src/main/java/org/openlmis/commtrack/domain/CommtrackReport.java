/*
 * CCommtrackRequisitionageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.commtrack.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.List;

@Data
@NoArgsConstructor
public class CommtrackReport {


  private Integer facilityId;
  private Integer programId;
  private Integer periodId;
  private Integer userId;

  private List<RnrLineItem> products;

  }