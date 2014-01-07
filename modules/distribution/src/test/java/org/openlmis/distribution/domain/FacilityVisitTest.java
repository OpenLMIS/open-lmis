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

@Category(UnitTests.class)
public class FacilityVisitTest {

  @Test
  public void shouldConstruct() throws Exception {
    Long distributionId = 1L;
    Long facilityId = 2L;
    Long createdBy = 3L;


    FacilityVisit facilityVisit = new FacilityVisit(distributionId, facilityId, createdBy);

    assertThat(facilityVisit.getDistributionId(), is(distributionId));
    assertThat(facilityVisit.getFacilityId(), is(facilityId));
    assertThat(facilityVisit.getCreatedBy(), is(createdBy));

  }
}
