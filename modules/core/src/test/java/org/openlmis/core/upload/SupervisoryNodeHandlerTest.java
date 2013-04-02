/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SupervisoryNodeHandlerTest {

  public static final Integer USER = 1;
  @Mock
  SupervisoryNodeService supervisoryNodeService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void shouldSaveSupervisoryNode() throws Exception {
    SupervisoryNode supervisoryNode = new SupervisoryNode();

    SupervisoryNode existing = new SupervisoryNode();
    new SupervisoryNodeHandler(supervisoryNodeService).save(existing, supervisoryNode, new AuditFields(USER, new Date()));
    assertThat(supervisoryNode.getModifiedBy(), is(USER));
    assertThat(supervisoryNode.getModifiedDate(), is(notNullValue()));

    verify(supervisoryNodeService).save(supervisoryNode);
  }


}
