/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Report {
  public static final String ERROR_MANDATORY_FIELD_MISSING = "error.restapi.mandatory.missing";
  private Long requisitionId;
  private Long facilityId;
  private Long programId;
  private Long periodId;
  private String userId;
  private Vendor vendor;
  private List<RnrLineItem> products;

  public void validate() {
    if (facilityId == null || programId == null || periodId == null || userId == null || vendor == null)
      throw new DataException(ERROR_MANDATORY_FIELD_MISSING);
  }

  public Rnr getRequisition() {
    Rnr rnr = new Rnr();
    rnr.setId(requisitionId);
    rnr.setFullSupplyLineItems(products);
    return rnr;
  }
}