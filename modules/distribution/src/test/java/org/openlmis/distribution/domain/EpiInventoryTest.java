/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Date;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
@PrepareForTest({EpiInventory.class})
public class EpiInventoryTest {

  @Test
  public void shouldPopulateEpiInventoryLineItems() throws Exception {
    Facility facility = new Facility(2L);
    ProgramSupported programSupported = new ProgramSupported(1L, true, new Date());

    FacilityProgramProduct facilityProgramProduct1 = spy(new FacilityProgramProduct());
    facilityProgramProduct1.setProduct(mock(Product.class));
    FacilityProgramProduct facilityProgramProduct2 = spy(new FacilityProgramProduct());
    facilityProgramProduct2.setProduct(mock(Product.class));

    programSupported.setProgramProducts(asList(facilityProgramProduct1, facilityProgramProduct2));
    facility.setSupportedPrograms(asList(programSupported));

    when(facilityProgramProduct1.getActive()).thenReturn(true);
    when(facilityProgramProduct1.getProduct().getActive()).thenReturn(true);

    when(facilityProgramProduct2.getActive()).thenReturn(true);
    when(facilityProgramProduct2.getProduct().getActive()).thenReturn(true);

    whenNew(EpiInventoryLineItem.class).withAnyArguments().thenReturn(mock(EpiInventoryLineItem.class));

    Distribution distribution = new Distribution();
    distribution.setId(1L);
    distribution.setPeriod(new ProcessingPeriod(null, null, null, 4, "period"));
    Long createdBy = 2L;
    Long facilityVisitId = 3L;
    FacilityVisit facilityVisit = mock(FacilityVisit.class);
    facilityVisit.setId(facilityVisitId);
    facilityVisit.setCreatedBy(createdBy);
    EpiInventory epiInventory = new EpiInventory(facilityVisit, facility, distribution);

    assertThat(epiInventory.getLineItems().size(), is(2));
  }
}
