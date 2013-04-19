/*
 * CShipment © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippedLineItem extends BaseModel implements Importable{
  @ImportField(mandatory = true, type = "int", name = "Order Number")
  private Integer orderId;

  @ImportField(mandatory = true, name = "Product Code")
  private String productCode;

  @ImportField(mandatory = true, type = "int", name = "Quantity Shipped")
  private Integer quantityShipped;

}
