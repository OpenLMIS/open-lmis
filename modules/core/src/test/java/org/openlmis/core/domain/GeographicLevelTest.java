/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *  Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTests.class)
public class GeographicLevelTest {
  @Test
  public void shouldReturnTrueIfLevelIsRoot() throws Exception {
    GeographicLevel root = new GeographicLevel(123L, "root", "root", 1);
    assertTrue(root.isRootLevel());
  }

  @Test
  public void shouldReturnFalseIfLevelIsNotRoot() throws Exception {
    GeographicLevel someLevel = new GeographicLevel(123L, "some level", "some level", 2);
    assertFalse(someLevel.isRootLevel());
  }

  @Test
  public void testIsChildOf() throws Exception {
    GeographicLevel someLevel = new GeographicLevel(123L, "some level", "some level", 2);
    GeographicLevel root = new GeographicLevel(123L, "root", "root", 1);

    assertTrue(someLevel.isLowerInHierarchyThan(root));
    assertFalse(root.isLowerInHierarchyThan(someLevel));
    assertFalse(root.isLowerInHierarchyThan(root));
    assertFalse(someLevel.isLowerInHierarchyThan(someLevel));
  }
}
