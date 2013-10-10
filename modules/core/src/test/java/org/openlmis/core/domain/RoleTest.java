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

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.db.categories.UnitTests;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
public class RoleTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldGiveErrorIfRoleDoesNotHaveAnyRights() throws Exception {
    Role role = new Role("role test", "description");

    expectedEx.expect(dataExceptionMatcher("error.role.without.rights"));

    role.validate();
  }

  @Test
  public void shouldGiveErrorIfRoleHasEmptyRightsList() {
    Role role = new Role("role test", "description", new HashSet<Right>());

    expectedEx.expect(dataExceptionMatcher("error.role.without.rights"));

    role.validate();
  }


  @Test
  public void shouldGiveErrorIfRoleDoesNotHaveAnyName() throws Exception {
    Role role = new Role("", "description", new HashSet<>(asList(CREATE_REQUISITION)));

    expectedEx.expect(dataExceptionMatcher("error.role.without.name"));
    role.validate();

    role.setName(null);
    expectedEx.expect(dataExceptionMatcher("error.role.without.name"));
    role.validate();
  }

  @Test
  public void shouldGiveErrorIfRelatedRightsAreNotSelectedForAdmin() {
    Role role = new Role("Admin", "admin", new HashSet<>(asList(MANAGE_REPORT)));

    expectedEx.expect(dataExceptionMatcher("error.role.related.right.not.selected"));

    role.validate();
  }

  @Test
  public void shouldGiveErrorIfRelatedRightsAreNotSelectedForRequisition() {
    Role role = new Role("Admin", "admin", new HashSet<>(asList(CREATE_REQUISITION, AUTHORIZE_REQUISITION)));

    expectedEx.expect(dataExceptionMatcher("error.role.related.right.not.selected"));

    role.validate();
  }

  @Test
  public void shouldGiveErrorIfRelatedRightsAreNotSelectedForShipment() {
    Role role = new Role("Admin", "admin", new HashSet<>(asList(CONVERT_TO_ORDER)));

    expectedEx.expect(dataExceptionMatcher("error.role.related.right.not.selected"));

    role.validate();
  }
}
