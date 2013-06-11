/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryZoneMember extends BaseModel implements Importable{

  @ImportField(name = "Delivery Zone", nested = "code", mandatory = true)
  private DeliveryZone deliveryZone;

  @ImportField(name = "Facility", nested = "code", mandatory = true)
  private Facility facility;

  private Long createdBy;
  private Date createdDate;

  public DeliveryZoneMember(DeliveryZone deliveryZone, Facility facility) {
    this.deliveryZone = deliveryZone;
    this.facility = facility;
  }
}
