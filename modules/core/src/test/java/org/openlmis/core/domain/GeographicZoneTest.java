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

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.db.categories.UnitTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
public class GeographicZoneTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnFalseIfParentLevelIsSiblingOfCurrentZonesLevel() throws Exception {
    GeographicLevel level1 = new GeographicLevel(1234L, "some level", "some level", 1);
    GeographicLevel level2 = new GeographicLevel(1235L, "some level", "some level", 1);
    GeographicZone parentZone = new GeographicZone();
    parentZone.setLevel(level1);
    GeographicZone childZone = new GeographicZone();
    childZone.setLevel(level2);
    childZone.setParent(parentZone);

    assertFalse(childZone.isParentHigherInHierarchy());
  }

  @Test
  public void shouldReturnFalseIfParentLevelIsLowerInHierarchyThanCurrentZonesLevel() throws Exception {
    GeographicLevel parentLevel = new GeographicLevel(1234L, "some level", "some level", 4);
    GeographicLevel childLevel = new GeographicLevel(1234L, "some level", "some level", 2);
    GeographicZone parentZone = new GeographicZone();
    parentZone.setLevel(parentLevel);
    GeographicZone childZone = new GeographicZone();
    childZone.setLevel(childLevel);
    childZone.setParent(parentZone);

    assertFalse(childZone.isParentHigherInHierarchy());
  }

  @Test
  public void shouldReturnTrueIfParentIsCorrectInHierarchy() throws Exception {
    GeographicLevel parentLevel = new GeographicLevel(1234L, "some level", "some level", 1);
    GeographicLevel childLevel = new GeographicLevel(1234L, "some level", "some level", 2);
    GeographicZone parentZone = new GeographicZone();
    parentZone.setLevel(parentLevel);
    GeographicZone childZone = new GeographicZone();
    childZone.setLevel(childLevel);
    childZone.setParent(parentZone);

    assertTrue(childZone.isParentHigherInHierarchy());
  }

  @Test
  public void shouldReturnTrueIfGeoLevelIsRoot() throws Exception {
    GeographicLevel rootLevel = new GeographicLevel(1234L, "root level", "root level", 1);
    GeographicZone root = new GeographicZone();
    root.setLevel(rootLevel);

    assertTrue(root.isRootLevel());
  }

  @Test
  public void shouldReturnFalseIfGeoLevelIsNotRoot() throws Exception {
    GeographicLevel level = new GeographicLevel(1234L, "non root level", "non root level", 2);
    GeographicZone zone = new GeographicZone();
    zone.setLevel(level);

    assertFalse(zone.isRootLevel());
  }

  @Test
  public void shouldThrowExceptionIfLevelEmpty() throws Exception {
    GeographicZone geographicZone = new GeographicZone(1234L, "some level", "some level", null, null);

    expectedEx.expect(dataExceptionMatcher("error.geo.level.invalid"));

    geographicZone.validateLevel();
  }

  @Test
  public void shouldThrowExceptionIfGeoZoneCodeEmpty() throws Exception {
    GeographicZone geographicZone = new GeographicZone();

    expectedEx.expect(dataExceptionMatcher("error.mandatory.fields.missing"));

    geographicZone.validateMandatoryFields();
  }

  @Test
  public void shouldThrowExceptionIfGeoZoneNameEmpty() throws Exception {
    GeographicZone geographicZone = new GeographicZone();
    geographicZone.setCode("code");

    expectedEx.expect(dataExceptionMatcher("error.mandatory.fields.missing"));

    geographicZone.validateMandatoryFields();
  }

  @Test
  public void shouldNotThrowExceptionIfParentNotGivenToRootLevelGeoZone() throws Exception {
    GeographicLevel rootLevel = new GeographicLevel(1234L, "non root level", "non root level", 1);
    GeographicZone geographicZone = new GeographicZone(1234L, "some level", "some level", rootLevel, null);

    geographicZone.validateLevel();
  }

  @Test
  public void shouldThrowExceptionIfParentGivenToRootLevelGeoZone() throws Exception {
    GeographicLevel rootLevel = new GeographicLevel(1234L, "non root level", "non root level", 1);
    GeographicZone someGeoZone = new GeographicZone(1234L, "non root level", "non root level", null, null);
    GeographicZone geographicZone = new GeographicZone(1234L, "some level", "some level", rootLevel, someGeoZone);

    expectedEx.expect(dataExceptionMatcher("error.invalid.hierarchy"));

    geographicZone.validateLevel();
  }

  @Test
  public void shouldThrowExceptionIfParentNotGivenForNonRootLevelGeoZone() throws Exception {
    GeographicLevel level = new GeographicLevel(1234L, "non root level", "non root level", 2);
    GeographicZone geographicZone = new GeographicZone(1234L, "some level", "some level", level, null);

    expectedEx.expect(dataExceptionMatcher("error.invalid.hierarchy"));

    geographicZone.validateLevel();
  }

  @Test
  public void shouldNotThrowExceptionIfParentGivenForNonRootLevelGeoZone() throws Exception {
    GeographicLevel level = new GeographicLevel(1234L, "non root level", "non root level", 2);
    GeographicZone parentZone = new GeographicZone(1234L, "non root level", "non root level", null, null);
    GeographicZone geographicZone = new GeographicZone(1234L, "some level", "some level", level, parentZone);

    geographicZone.validateLevel();
  }
}
