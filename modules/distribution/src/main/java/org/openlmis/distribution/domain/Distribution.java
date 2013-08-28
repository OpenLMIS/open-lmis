/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

@Data
@JsonSerialize(include = NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Distribution extends BaseModel {
  DeliveryZone deliveryZone;
  Program program;
  ProcessingPeriod period;
  DistributionStatus status;

  @SuppressWarnings("unused")
  public String getZpp() {
    return deliveryZone.getId() + "_" + program.getId() + "_" + period.getId();
  }
}
