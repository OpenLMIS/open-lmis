/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.authentication.web;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RightName;
import org.openlmis.core.service.RightService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.RightType.ADMIN;
import static org.openlmis.core.domain.RightType.REQUISITION;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class PermissionEvaluatorTest {

  @Mock
  RoleRightsService roleRightsService;

  @Mock
  RightService rightService;

  @InjectMocks
  PermissionEvaluator evaluator;

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermission() throws Exception {

    Right right1 = new Right(RightName.CONFIGURE_RNR, ADMIN);
    Right right2 = new Right(RightName.AUTHORIZE_REQUISITION, REQUISITION);
    List<Right> rights = new ArrayList<>(asList(right1,right2));
    Long userId = 1L;
    when(roleRightsService.getRights(userId)).thenReturn(rights);

    assertThat(evaluator.hasPermission(userId, "AUTHORIZE_REQUISITION,  CONFIGURE_RNR"), is(true));
    assertThat(evaluator.hasPermission(userId, "AUTHORIZE_REQUISITION"), is(true));
    assertThat(evaluator.hasPermission(userId, "MANAGE_FACILITY"), is(false));
  }

  @Test
  public void shouldReturnTrueIfUserHasReportingRight(){

    Long userId = 1L;
    when(rightService.hasReportingRight(userId)).thenReturn(true);

    assertThat(evaluator.hasReportingPermission(userId),is(true));
    verify(rightService).hasReportingRight(userId);
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHaveReportingRight(){

    Long userId = 1L;
    when(rightService.hasReportingRight(userId)).thenReturn(false);

    assertThat(evaluator.hasReportingPermission(userId), is(false));
    verify(rightService).hasReportingRight(userId);
  }
}
