/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.model.AuditFields;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramSupportedPersistenceHandlerTest {

  @Mock
  ProgramSupportedService service;

  @Mock
  FacilityService facilityService;
  @InjectMocks
  private ProgramSupportedPersistenceHandler programSupportedPersistenceHandler;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();


  @Test
  public void shouldSaveProgramSupported() {
    ProgramSupported programSupported = new ProgramSupported();
    programSupportedPersistenceHandler.save(programSupported);
    verify(service).uploadSupportedProgram(programSupported);
  }

  @Test
  public void shouldPublishProgramSupportedFeedForAllFacilitiesUploaded() throws Exception {
    Date currentTimestamp = new Date();
    AuditFields auditFields = new AuditFields(currentTimestamp);
    Facility facility = new Facility();
    List<Facility> facilities = asList(facility);
    when(facilityService.getAllByProgramSupportedModifiedDate(currentTimestamp)).thenReturn(facilities);

    programSupportedPersistenceHandler.postProcess(auditFields);

    verify(facilityService).getAllByProgramSupportedModifiedDate(currentTimestamp);
    verify(service).notifyProgramSupportedUpdated(facility);
  }


}
