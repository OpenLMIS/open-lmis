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

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

@Category(UnitTests.class)
public class RefrigeratorProblemTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowErrorIfAllProblemsFalse() throws Exception {
    RefrigeratorProblem refrigeratorProblem = new RefrigeratorProblem(null, false, false, false, false, false, false, null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.reading.value");

    refrigeratorProblem.validate();
  }

  @Test
  public void shouldNotThrowErrorIfAnyOneProblemTrue() throws Exception {
    RefrigeratorProblem refrigeratorProblem = new RefrigeratorProblem(null, true, null, null, null, null, null, null);

    refrigeratorProblem.validate();
  }
}
