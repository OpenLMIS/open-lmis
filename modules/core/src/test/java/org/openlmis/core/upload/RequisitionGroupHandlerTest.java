/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@Category(UnitTests.class)
public class RequisitionGroupHandlerTest {

  public static final Long USER = 1L;
  RequisitionGroupHandler requisitionGroupHandler;

  @Mock
  RequisitionGroupService requisitionGroupService;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    requisitionGroupHandler = new RequisitionGroupHandler(requisitionGroupService);
  }

  @Test
  public void shouldSaveRequisitionGroupWithModifiedByAndModifiedDateSet() throws Exception {
    RequisitionGroup requisitionGroup = new RequisitionGroup();
    requisitionGroup.setModifiedBy(USER);

    RequisitionGroup existing = new RequisitionGroup();
    requisitionGroupHandler.save(requisitionGroup);

    verify(requisitionGroupService).save(requisitionGroup);
  }

}
