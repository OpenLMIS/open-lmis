package org.openlmis.core.domain;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.domain.Right.*;

public class RightTest {

  @Test
  public void shouldReturnViewRequisitionAsDependentRight() throws Exception {

    Right[] rights = {CREATE_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION, CONVERT_TO_ORDER};

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
}
