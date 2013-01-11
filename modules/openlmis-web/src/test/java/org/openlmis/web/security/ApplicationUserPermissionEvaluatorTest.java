package org.openlmis.web.security;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationUserPermissionEvaluatorTest {

  @Mock
  Authentication authentication;

  @Mock
  RoleRightsService roleRightsService;

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermission() throws Exception {
    when(authentication.getName()).thenReturn("user123");
    List<Right> rights = new ArrayList<Right>(){{
      add(Right.CONFIGURE_RNR);
    }};
    when(roleRightsService.getRights("user123")).thenReturn(rights);
    ApplicationUserPermissionEvaluator evaluator = new ApplicationUserPermissionEvaluator(roleRightsService);
    assertTrue(evaluator.hasPermission(authentication, "", "CONFIGURE_RNR"));
  }

  @Test @Ignore("WIP")
  public void shouldReturnTrueIfUserHasAuthorizeRightOverRnr() throws Exception {
    when(authentication.getName()).thenReturn("user123");
    List<Right> rights = new ArrayList<Right>(){{
      add(Right.CONFIGURE_RNR);
    }};
    when(roleRightsService.getRights("user123")).thenReturn(rights);
    ApplicationUserPermissionEvaluator evaluator = new ApplicationUserPermissionEvaluator(roleRightsService);
    Rnr rnr = new Rnr();
    assertTrue(evaluator.hasPermission(authentication, rnr, "CONFIGURE_RNR"));
  }
}
