/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.distribution.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
public class FacilityDistributionDataTest {

  @Test
  public void shouldConstructFacilityVisit() throws Exception {
    Long createdBy = 3L;
    FacilityDistributionData facilityDistributionData = new FacilityDistributionData(1L, 2L);
    facilityDistributionData.setCreatedBy(createdBy);
    FacilityVisit facilityVisit = mock(FacilityVisit.class);
    facilityDistributionData.setFacilityVisit(facilityVisit);
    FacilityVisit expected = new FacilityVisit();
    when(facilityVisit.construct(1L, 2L, createdBy)).thenReturn(expected);

    FacilityVisit actual = facilityDistributionData.constructFacilityVisit();

    verify(facilityVisit).construct(1L, 2L, createdBy);
    assertThat(actual, is(expected));
  }
}
