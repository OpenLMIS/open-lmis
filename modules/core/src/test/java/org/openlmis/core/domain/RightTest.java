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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.domain.Right.*;

@Category(UnitTests.class)
public class RightTest {

  @Test
  public void shouldReturnViewRequisitionAsDependentRight() throws Exception {
    Right[] rights = {CREATE_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION};

    for (Right right : rights) {
      List<Right> dependentRights = right.getDefaultRights();
      assertThat(dependentRights.size(), is(1));
      assertThat(dependentRights.get(0), is(VIEW_REQUISITION));
    }
  }

  @Test
  public void shouldReturnEmptyListWhenNoDefaultRightIsAvailable() throws Exception {
    Right[] rights = {CONFIGURE_RNR, MANAGE_FACILITY, MANAGE_ROLE, MANAGE_SCHEDULE, MANAGE_USER, UPLOADS, VIEW_REQUISITION};

    for (Right right : rights) {
      assertThat(right.getDefaultRights().size(), is(0));
    }
  }

  @Test
  public void shouldCompareTwoRights() {
    Right nullRight = null;
    Right nonNullRight = Right.CONFIGURE_RNR;
    Right approveRight = Right.APPROVE_REQUISITION;
    Right createRight = Right.CREATE_REQUISITION;
    RightComparator rightComparator = new RightComparator();
    assertThat(rightComparator.compare(nullRight, nonNullRight), is(greaterThan(0)));
    assertThat(rightComparator.compare(nonNullRight, nullRight), is(lessThan(0)));
    assertThat(rightComparator.compare(nonNullRight, nonNullRight), is(0));
    assertThat(rightComparator.compare(nullRight, nullRight), is(0));
    assertThat(rightComparator.compare(nullRight, nullRight), is(0));
    assertThat(rightComparator.compare(createRight, approveRight), is(greaterThan(0)));
    assertThat(rightComparator.compare(approveRight, createRight), is(lessThan(0)));
  }
}
