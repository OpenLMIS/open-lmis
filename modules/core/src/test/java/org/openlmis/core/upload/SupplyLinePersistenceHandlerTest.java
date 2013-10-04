/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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


