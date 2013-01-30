package org.openlmis.web.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationUserPermissionEvaluatorTest {

  public static final String USER = "user123";

  @Mock
  Authentication authentication;

  @Mock
  RoleRightsService roleRightsService;

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermission() throws Exception {
    when(authentication.getName()).thenReturn(USER);
    List<Right> rights = new ArrayList<Right>() {{
      add(Right.CONFIGURE_RNR);
      add(Right.AUTHORIZE_REQUISITION);
    }};
    when(roleRightsService.getRights(USER)).thenReturn(rights);
    ApplicationUserPermissionEvaluator evaluator = new ApplicationUserPermissionEvaluator(roleRightsService);
    assertTrue(evaluator.hasPermission(authentication, "", "AUTHORIZE_REQUISITION, CONFIGURE_RNR"));
    assertTrue(evaluator.hasPermission(authentication, "", "AUTHORIZE_REQUISITION"));
  }
}
