
/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.core.upload;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.model.AuditFields;

import java.util.Date;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class ProgramProductPersistenceHandlerTest {

  @InjectMocks
  ProgramProductPersistenceHandler handler;

  @Mock
  ProgramService programService;

  @Test
  public void shouldPostProcessFileToSendNotifications() throws Exception {
    Date currentTimestamp = new Date();
    handler.postProcess(new AuditFields(1L, currentTimestamp));

    verify(programService).notifyProgramChange();
  }


}
