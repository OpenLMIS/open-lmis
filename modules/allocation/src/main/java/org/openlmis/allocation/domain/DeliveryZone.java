/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
public class DeliveryZone extends BaseModel implements Importable {

  @ImportField(name = "Delivery zone code", mandatory = true)
  String code;
  @ImportField(name = "Delivery zone name", mandatory = true)
  String name;
  @ImportField(name = "Description")
  String description;

  Long createdBy;
  Date createdDate;

}
