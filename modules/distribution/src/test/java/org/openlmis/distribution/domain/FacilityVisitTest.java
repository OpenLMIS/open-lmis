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
import org.openlmis.core.domain.Facility;
import org.openlmis.db.categories.UnitTests;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class FacilityVisitTest {

  @Test
  public void shouldConstruct() throws Exception {
    Distribution distribution = new Distribution();
    distribution.setId(1L);
    distribution.setCreatedBy(3L);
    Facility facility = new Facility(2L);

    FacilityVisit facilityVisit = new FacilityVisit(facility, distribution);

    assertThat(facilityVisit.getDistributionId(), is(distribution.getId()));
    assertThat(facilityVisit.getFacilityId(), is(facility.getId()));
    assertThat(facilityVisit.getCreatedBy(), is(distribution.getCreatedBy()));
  }

  @Test
  public void shouldGetVisitDetailsIfFacilityVisited() throws Exception {
    Date visitedDate = new Date();
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisit.setObservations("some");
    facilityVisit.setVisited(true);
    facilityVisit.setConfirmedBy(new Facilitator());
    facilityVisit.setVerifiedBy(new Facilitator());
    facilityVisit.setVehicleId("123");
    facilityVisit.setVisitDate(visitedDate);
    facilityVisit.setReasonForNotVisiting(ReasonForNotVisiting.HEALTH_CENTER_NOT_IN_DLS);

    facilityVisit.setApplicableVisitInfo();

    FacilityVisit expectedFacilityVisit = new FacilityVisit();
    expectedFacilityVisit.setObservations("some");
    expectedFacilityVisit.setVisited(true);
    expectedFacilityVisit.setConfirmedBy(new Facilitator());
    expectedFacilityVisit.setVerifiedBy(new Facilitator());
    expectedFacilityVisit.setVehicleId("123");
    expectedFacilityVisit.setVisitDate(visitedDate);
    assertThat(facilityVisit, is(expectedFacilityVisit));
  }

  @Test
  public void shouldGetUnvisitedFacilityDetails() throws Exception {
    Date visitedDate = new Date();
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisit.setObservations("some");
    facilityVisit.setVisited(false);
    facilityVisit.setConfirmedBy(new Facilitator());
    facilityVisit.setVerifiedBy(new Facilitator());
    facilityVisit.setVehicleId("123");
    facilityVisit.setVisitDate(visitedDate);
    facilityVisit.setReasonForNotVisiting(ReasonForNotVisiting.HEALTH_CENTER_NOT_IN_DLS);
    facilityVisit.setOtherReasonDescription("other reasons");

    facilityVisit.setApplicableVisitInfo();

    assertThat(facilityVisit.getOtherReasonDescription(), is("other reasons"));
    assertThat(facilityVisit.getReasonForNotVisiting(), is(ReasonForNotVisiting.HEALTH_CENTER_NOT_IN_DLS));
    assertFalse(facilityVisit.getVisited());
  }

  @Test
  public void shouldNoChangeFacilityVisitIfVisitedFlagIsNull() throws Exception {
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisit.setObservations("some observation");
    facilityVisit.setVisited(null);
    facilityVisit.setReasonForNotVisiting(ReasonForNotVisiting.HEALTH_CENTER_NOT_IN_DLS);
    facilityVisit.setOtherReasonDescription("other reasons");

    facilityVisit.setApplicableVisitInfo();

    assertThat(facilityVisit.getOtherReasonDescription(), is("other reasons"));
    assertThat(facilityVisit.getReasonForNotVisiting(), is(ReasonForNotVisiting.HEALTH_CENTER_NOT_IN_DLS));
    assertThat(facilityVisit.getObservations(), is("some observation"));
  }
}
