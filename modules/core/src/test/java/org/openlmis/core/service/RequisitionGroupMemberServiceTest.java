/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.repository.RequisitionGroupMemberRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupMemberServiceTest {

    @Mock
    RequisitionGroupMemberRepository requisitionGroupMemberRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveRGMember() throws Exception {

        RequisitionGroupMember requisitionGroupMember = new RequisitionGroupMember();
        new RequisitionGroupMemberService(requisitionGroupMemberRepository).save(requisitionGroupMember);

        verify(requisitionGroupMemberRepository).insert(requisitionGroupMember);
    }
}
