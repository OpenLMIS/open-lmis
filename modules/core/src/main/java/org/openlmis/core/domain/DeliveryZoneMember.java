/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryZoneMember extends BaseModel implements Importable{

  @ImportField(name = "Delivery Zone", nested = "code", mandatory = true)
  private DeliveryZone deliveryZone;

  @ImportField(name = "Facility", nested = "code", mandatory = true)
  private Facility facility;

}
