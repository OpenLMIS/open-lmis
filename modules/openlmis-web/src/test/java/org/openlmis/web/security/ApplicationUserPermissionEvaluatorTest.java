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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

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

  @Test @Ignore
  public void shouldReturnFalseIfUserDoesNotHaveCreateRightForRequisition() throws Exception {
    Integer facilityId = 1;
    Integer programId = 1;
    Rnr requisition = new Rnr();
    requisition.setFacilityId(facilityId);
    requisition.setProgramId(programId);
    when(authentication.getName()).thenReturn(USER);
    when(roleRightsService.userHasRightForFacilityProgram(facilityId, programId, USER, CREATE_REQUISITION)).thenReturn(false);

    ApplicationUserPermissionEvaluator evaluator = new ApplicationUserPermissionEvaluator(roleRightsService);

    boolean result = evaluator.hasPermission(authentication, requisition, "CREATE_REQUISITION");
    verify(roleRightsService).userHasRightForFacilityProgram(requisition.getFacilityId(), requisition.getProgramId(), USER,CREATE_REQUISITION);
    verify(roleRightsService, never()).getRights(USER);
    assertFalse(result);
  }
}
