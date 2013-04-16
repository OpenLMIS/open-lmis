/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file.csv.handler;

import lombok.Data;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

/**
* Created with IntelliJ IDEA.
* User: sandeeb
* Date: 4/15/13
* Time: 5:06 PM
* To change this template use File | Settings | File Templates.
*/
@Data
public class RawShipment implements Importable {
  @ImportField(name = "Order Number")
  private String orderNumber;
}
