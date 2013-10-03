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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.service.RequisitionGroupProgramScheduleService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.model.Field;
import org.openlmis.upload.model.ModelClass;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
@Category(UnitTests.class)
public class RequisitionGroupProgramScheduleHandlerTest {

  public static final Integer USER = 1;
  @Mock
  RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void shouldSaveRGToProgramAndScheduleMappingWithModifiedBy() {

    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();

    new RequisitionGroupProgramScheduleHandler(requisitionGroupProgramScheduleService).save(requisitionGroupProgramSchedule);

    verify(requisitionGroupProgramScheduleService).save(requisitionGroupProgramSchedule);
  }

  @Test
  public void shouldMarkRequisitionGroupFieldAsImportable() {
    Field requisitionGroupField = new ModelClass(RequisitionGroupProgramSchedule.class).findImportFieldWithName("RG Code");
    assertNotNull(requisitionGroupField);
    assertTrue(requisitionGroupField.isMandatory());
    assertEquals("code", requisitionGroupField.getNested());
  }

  @Test
  public void shouldMarkProgramFieldAsImportable() {
    Field programField = new ModelClass(RequisitionGroupProgramSchedule.class).findImportFieldWithName("Program");
    assertNotNull(programField);
    assertTrue(programField.isMandatory());
    assertEquals("code", programField.getNested());
  }

  @Test
  public void shouldMarkScheduleFieldAsImportable() {
    Field scheduleField = new ModelClass(RequisitionGroupProgramSchedule.class).findImportFieldWithName("Schedule");
    assertNotNull(scheduleField);
    assertTrue(scheduleField.isMandatory());
    assertEquals("code", scheduleField.getNested());
  }

  @Test
  public void shouldMarkDirectDeliveryFieldAsImportable() {
    Field directDeliveryField = new ModelClass(RequisitionGroupProgramSchedule.class).findImportFieldWithName("Direct Delivery");
    assertNotNull(directDeliveryField);
    assertTrue(directDeliveryField.isMandatory());
    assertEquals("", directDeliveryField.getNested());
  }

  @Test
  public void shouldMarkDropOffFacilityFieldAsImportable() {
    Field dropOffFacilityField = new ModelClass(RequisitionGroupProgramSchedule.class).findImportFieldWithName("Drop off Facility");
    assertNotNull(dropOffFacilityField);
    assertFalse(dropOffFacilityField.isMandatory());
    assertEquals("code", dropOffFacilityField.getNested());
  }

}
