/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProgramSupportedPersistenceHandlerTest {

  @Mock
  FacilityService facilityService;

  private ProgramSupportedPersistenceHandler programSupportedPersistenceHandler;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    programSupportedPersistenceHandler = new ProgramSupportedPersistenceHandler(facilityService);
  }

  @Test
  public void shouldSaveProgramSupported() {
    ProgramSupported programSupported = new ProgramSupported();
    ProgramSupported existing = new ProgramSupported();
    programSupportedPersistenceHandler.save(existing, programSupported, new AuditFields(1, null));
    verify(facilityService).uploadSupportedProgram(programSupported);
    assertThat(programSupported.getModifiedBy(), is(1));
  }

  @Test
  public void shouldThrowErrorIfDuplicateFacilityAndProgramCodeFoundWithSameTimeStamp() {
    ProgramSupported programSupported = new ProgramSupported();
    Date currentTimestamp = new Date();
    programSupported.setModifiedDate(currentTimestamp);

    AuditFields auditFields = new AuditFields();
    auditFields.setCurrentTimestamp(currentTimestamp);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Facility has already been mapped to the program ");

    programSupportedPersistenceHandler.throwExceptionIfAlreadyProcessedInCurrentUpload(programSupported, auditFields);
  }

}
