/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;

public class ShippedLineItemTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowDataExceptionIfQuantityShippedIsNegative() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shippedLineItem.setQuantityShipped(-1);
    expectedException.expect(DataException.class);
    shippedLineItem.validateForSave();
  }
}
