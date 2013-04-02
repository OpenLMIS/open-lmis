/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.service.RequisitionGroupMemberService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupMemberHandlerTest {

  public static final Integer USER = 1;

  @Mock
  RequisitionGroupMemberService requisitionGroupMemberService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void shouldSaveRGMembersTaggedWithModifiedBy() throws Exception {
    RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();

    RequisitionGroupMember existing = new RequisitionGroupMember();
    new RequisitionGroupMemberHandler(requisitionGroupMemberService).save(existing, requisitionGroupMember, new AuditFields(USER, null));

    assertThat(requisitionGroupMember.getModifiedBy(), is(USER));
    assertThat(requisitionGroupMember.getModifiedDate(), is(notNullValue()));

    verify(requisitionGroupMemberService).save(requisitionGroupMember);
  }
}
