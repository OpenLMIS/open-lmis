/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

@Category(UnitTests.class)
public class ShipmentFileColumnTest {
  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldThrowErrorIfPositionIsZero() throws Exception {
    ShipmentFileColumn column = new ShipmentFileColumn("name", "Label", 0, true, true, "dd/mm/yy");

    exException.expect(DataException.class);
    exException.expectMessage("shipment.file.invalid.position");

    column.validate();
  }

  @Test
  public void shouldThrowErrorIfPositionIsNull() throws Exception {
    ShipmentFileColumn column = new ShipmentFileColumn("name", "Label", null, true, true, "dd/mm/yy");

    exException.expect(DataException.class);
    exException.expectMessage("shipment.file.invalid.position");

    column.validate();
  }
}
