/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.FacilityService;
import org.openlmis.upload.model.AuditFields;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProgramSupportedPersistenceHandlerTest {

  @Mock
  FacilityService facilityService;

  private ProgramSupportedPersistenceHandler programSupportedPersistenceHandler;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    programSupportedPersistenceHandler = new ProgramSupportedPersistenceHandler(facilityService);
  }

  @Test
  public void shouldSaveProgramSupported() {
    ProgramSupported programSupported = new ProgramSupported();
    programSupportedPersistenceHandler.save(programSupported, new AuditFields(1, null));
    verify(facilityService).uploadSupportedProgram(programSupported);
    assertThat(programSupported.getModifiedBy(), is(1));
  }

}
