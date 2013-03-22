/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.authentication.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PermissionEvaluatorTest {

  @Mock
  RoleRightsService roleRightsService;

  private PermissionEvaluator evaluator;

  @Before
  public void setUp() throws Exception {
    evaluator = new PermissionEvaluator(roleRightsService);
  }

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermission() throws Exception {

    Set<Right> rights = new HashSet<Right>() {{
      add(Right.CONFIGURE_RNR);
      add(Right.AUTHORIZE_REQUISITION);
    }};
    Integer userId = 1;
    when(roleRightsService.getRights(userId)).thenReturn(rights);

    assertThat(evaluator.hasPermission(userId, "AUTHORIZE_REQUISITION, CONFIGURE_RNR"), is(true));
    assertThat(evaluator.hasPermission(userId, "AUTHORIZE_REQUISITION"), is(true));
    assertThat(evaluator.hasPermission(userId, "MANAGE_FACILITY"), is(false));
  }

}
