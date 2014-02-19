/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
public class RefrigeratorReadingDTOTest {

  @Test
  public void shouldTransformRefrigeratorReadingToReading() throws Exception {
    Long facilityVisitId = 5L;
    Reading temperature = new Reading("32.4", false);
    Reading functioningCorrectly = new Reading("N", false);
    Reading lowAlarmEvents = new Reading("2", false);
    Reading highAlarmEvents = new Reading("", true);
    Reading problemSinceLastTime = new Reading("Y", false);
    RefrigeratorProblem problems = new RefrigeratorProblem(3L, false, true, false, false, false, false, null);
    String notes = "Notes";
    Long facilityId = 2L;

    RefrigeratorReading expectedReading = new RefrigeratorReading(new Refrigerator("brand", "model", "serial number", facilityId, true), facilityVisitId, 32.4F,
      "N", 2, null, "Y", problems, notes);

    RefrigeratorReadingDTO refrigeratorReadingDTO = new RefrigeratorReadingDTO(new Refrigerator("brand", "model", "serial number", facilityId, true),
      facilityVisitId,
      temperature,
      functioningCorrectly,
      lowAlarmEvents,
      highAlarmEvents,
      problemSinceLastTime,
      problems,
      notes);

    RefrigeratorReading refrigeratorReading = refrigeratorReadingDTO.transform();

    assertThat(refrigeratorReading, is(expectedReading));
  }

  @Test
  public void shouldValidateRefrigeratorProblemsIfFunctioningCorrectlySinceLastVisitIsFalse() throws Exception {
    Long facilityVisitId = 5L;
    Reading temperature = new Reading("32.4", false);
    Reading functioningCorrectly = new Reading("N", false);
    Reading lowAlarmEvents = new Reading("2", false);
    Reading highAlarmEvents = new Reading("", true);
    Reading problemSinceLastTime = new Reading("Y", false);
    RefrigeratorProblem problems = mock(RefrigeratorProblem.class);
    String notes = "Notes";
    Long facilityId = 2L;

    RefrigeratorReading expectedReading = new RefrigeratorReading(new Refrigerator("brand", "model", "serial number", facilityId, true), facilityVisitId, 32.4F,
      "N", 2, null, "Y", problems, notes);

    RefrigeratorReadingDTO refrigeratorReadingDTO = new RefrigeratorReadingDTO(new Refrigerator("brand", "model", "serial number", facilityId, true),
      facilityVisitId,
      temperature,
      functioningCorrectly,
      lowAlarmEvents,
      highAlarmEvents,
      problemSinceLastTime,
      problems,
      notes);

    RefrigeratorReading refrigeratorReading = refrigeratorReadingDTO.transform();

    assertThat(refrigeratorReading, is(expectedReading));
    verify(problems).validate();
  }

  @Test
  public void shouldSetProblemsToNullIfProblemSinceLastVisitIsFalse() throws Exception {
    Long facilityVisitId = 5L;
    Reading temperature = new Reading("32.4", false);
    Reading functioningCorrectly = new Reading("Y", false);
    Reading lowAlarmEvents = new Reading("2", false);
    Reading highAlarmEvents = new Reading("", true);
    Reading problemSinceLastTime = new Reading("N", false);
    RefrigeratorProblem problems = new RefrigeratorProblem();
    String notes = "Notes";
    Long facilityId = 2L;

    RefrigeratorReadingDTO refrigeratorReadingDTO = new RefrigeratorReadingDTO(new Refrigerator("brand", "model", "serial number", facilityId, true),
      facilityVisitId,
      temperature,
      functioningCorrectly,
      lowAlarmEvents,
      highAlarmEvents,
      problemSinceLastTime,
      problems,
      notes);

    RefrigeratorReading refrigeratorReading = refrigeratorReadingDTO.transform();

    RefrigeratorReading expectedReading = new RefrigeratorReading(new Refrigerator("brand", "model", "serial number", facilityId, true), facilityVisitId, 32.4F,
      "Y", 2, null, "N", null, notes);

    assertThat(refrigeratorReading, is(expectedReading));
    assertThat(refrigeratorReading.getProblem(), is(nullValue()));
  }
}
