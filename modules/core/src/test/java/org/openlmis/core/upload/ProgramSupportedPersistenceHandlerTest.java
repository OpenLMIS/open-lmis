/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
    verify(service).updateForVirtualFacilities(facility);
    verify(service).notifyProgramSupportedUpdated(facility);
  }


}
