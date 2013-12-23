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
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RefrigeratorReadingDTOTest {

  @Test
  public void shouldTransformRefrigeratorReadingToReading() throws Exception {
    Reading temperature = new Reading("32.4", false);
    Reading functioningCorrectly = new Reading("Y", false);
    Reading lowAlarmEvents = new Reading("2", false);
    Reading highAlarmEvents = new Reading("", true);
    Reading problemSinceLastTime = new Reading("Y", false);
    RefrigeratorProblem problems = new RefrigeratorProblem(3L, false, true, false, false, false, false, null);
    String notes = "Notes";
    Long facilityId = 2L;

    RefrigeratorReading expectedReading = new RefrigeratorReading(new Refrigerator("brand", "model", "serial number", facilityId), 1L, 32.4F,
      "Y", 2, null, "Y", problems, notes);

    RefrigeratorReadingDTO refrigeratorReadingDTO = new RefrigeratorReadingDTO(1L,
      "brand",
      "model",
      "serial number",
      temperature,
      functioningCorrectly,
      lowAlarmEvents,
      highAlarmEvents,
      problemSinceLastTime,
      problems,
      notes);

    RefrigeratorReading refrigeratorReading = refrigeratorReadingDTO.transform(facilityId);

    assertThat(refrigeratorReading, is(expectedReading));
  }
}
