/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.domain;

import lombok.Data;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
public class Shipment implements Importable {
  private Integer id;

  @ImportField(mandatory = true, name = "Order Number")
  private String orderNumber;

  @ImportField(mandatory = true, name = "Product Code")
  private String productCode;

  @ImportField(mandatory = true,  name = "Quantity Shipped")
  private String quantityShipped;
}
