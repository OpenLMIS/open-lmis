/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.service.RequisitionGroupProgramScheduleService;
import org.openlmis.upload.model.Field;
import org.openlmis.upload.model.ModelClass;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

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
