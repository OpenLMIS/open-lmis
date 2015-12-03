/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class RightNameTest {

  @Test
  public void shouldReturnCommaSeparateRightNames() {

    String result = RightName.commaSeparateRightNames("name1", "name2");

    assertThat(result, is("{name1, name2}"));
  }
}
