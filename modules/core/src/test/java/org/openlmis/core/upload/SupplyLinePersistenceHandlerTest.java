/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.builder.SupplyLineBuilder;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
public class SupplyLinePersistenceHandlerTest {

  SupplyLineService supplyLineService;
  SupplyLinePersistenceHandler supplyLinePersistenceHandler;

  @Before
  public void setUp() {
    supplyLineService = mock(SupplyLineService.class);
    supplyLinePersistenceHandler = new SupplyLinePersistenceHandler(supplyLineService);
  }

  @Test
  public void shouldSaveSupplyLine() {
    SupplyLine supplyLine = make(a(SupplyLineBuilder.defaultSupplyLine));
    supplyLinePersistenceHandler.save(supplyLine);
    verify(supplyLineService).save(supplyLine);

  }
}


