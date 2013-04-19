/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.domain.Right.*;

public class RightTest {

  @Test
  public void shouldReturnViewRequisitionAsDependentRight() throws Exception {

    Right[] rights = {CREATE_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION};

    for (Right right : rights) {
      List<Right> dependentRights = right.getDependentRights();
      assertThat(dependentRights.size(), is(1));
      assertThat(dependentRights.get(0), is(VIEW_REQUISITION));
    }
  }


  @Test
  public void shouldReturnEmptyListWhenNoDependentRightIsAvailable() throws Exception {
    Right[] rights = {CONFIGURE_RNR, MANAGE_FACILITY, MANAGE_ROLE, MANAGE_SCHEDULE, MANAGE_USERS, UPLOADS, VIEW_REQUISITION};

    for (Right right : rights) {
      assertThat(right.getDependentRights().size(), is(0));
    }
  }

  @Test
  public void shouldCompareTwoRights(){
    Right nullRight = null;
    Right nonNullRight = Right.CONFIGURE_RNR;
    Right approveRight = Right.APPROVE_REQUISITION;
    Right createRight = Right.CREATE_REQUISITION;
    RightComparator rightComparator = new RightComparator();
    assertThat(rightComparator.compare(nullRight, nonNullRight),is(greaterThan(0)));
    assertThat(rightComparator.compare(nonNullRight, nullRight),is(lessThan(0)));
    assertThat(rightComparator.compare(nonNullRight, nonNullRight), is(0));
    assertThat(rightComparator.compare(nullRight, nullRight), is(0));
    assertThat(rightComparator.compare(nullRight, nullRight), is(0));
    assertThat(rightComparator.compare(createRight, approveRight), is(greaterThan(0)));
    assertThat(rightComparator.compare(approveRight, createRight), is(lessThan(0)));


  }
}
