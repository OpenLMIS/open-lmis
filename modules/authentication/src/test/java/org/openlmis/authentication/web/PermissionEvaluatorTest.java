package org.openlmis.authentication.web;

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

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermission() throws Exception {

    Set<Right> rights = new HashSet<Right>() {{
      add(Right.CONFIGURE_RNR);
      add(Right.AUTHORIZE_REQUISITION);
    }};
    when(roleRightsService.getRights("loggedInUser")).thenReturn(rights);
    PermissionEvaluator evaluator = new PermissionEvaluator(roleRightsService);

    assertThat(evaluator.hasPermission("loggedInUser", "AUTHORIZE_REQUISITION, CONFIGURE_RNR"), is(true));
    assertThat(evaluator.hasPermission("loggedInUser", "AUTHORIZE_REQUISITION"), is(true));
    assertThat(evaluator.hasPermission("loggedInUser", "MANAGE_FACILITY"), is(false));
  }
}
