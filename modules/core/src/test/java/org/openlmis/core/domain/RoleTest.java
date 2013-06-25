/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.db.categories.UnitTests;

import java.util.HashSet;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
public class RoleTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldGiveErrorIfRoleDoesNotHaveAnyRights() throws Exception {
    Role role = new Role("role test", FALSE, "description");

    expectedEx.expect(dataExceptionMatcher("error.role.without.rights"));

    role.validate();
  }

  @Test
  public void shouldGiveErrorIfRoleHasEmptyRightsList() {
    Role role = new Role("role test", FALSE, "description", new HashSet<Right>());

    expectedEx.expect(dataExceptionMatcher("error.role.without.rights"));

    role.validate();
  }


  @Test
  public void shouldGiveErrorIfRoleDoesNotHaveAnyName() throws Exception {
    Role role = new Role("", FALSE, "description", new HashSet<>(asList(CREATE_REQUISITION)));

    expectedEx.expect(dataExceptionMatcher("error.role.without.name"));
    role.validate();

    role.setName(null);
    expectedEx.expect(dataExceptionMatcher("error.role.without.name"));
    role.validate();
  }
}
