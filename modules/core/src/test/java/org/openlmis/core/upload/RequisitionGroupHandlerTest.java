/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.upload.model.AuditFields;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


public class RequisitionGroupHandlerTest {

    public static final Integer USER = 1;
    RequisitionGroupHandler requisitionGroupHandler;

    @Mock
    RequisitionGroupService requisitionGroupService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        requisitionGroupHandler = new RequisitionGroupHandler(requisitionGroupService);
    }

    @Test
    public void shouldSaveRequisitionGroupWithModifiedByAndModifiedDateSet() throws Exception {
        RequisitionGroup requisitionGroup = new RequisitionGroup();
        requisitionGroup.setModifiedBy(USER);

        requisitionGroupHandler.save(requisitionGroup, new AuditFields(USER, null));

        assertThat(requisitionGroup.getModifiedBy(), is(USER));
        assertThat(requisitionGroup.getModifiedDate(), is(notNullValue()));
        verify(requisitionGroupService).save(requisitionGroup);
    }
}
