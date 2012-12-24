package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.service.RequisitionGroupProgramScheduleService;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.model.ModelClass;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupProgramScheduleHandlerTest {

    public static final String USER = "User";
    @Mock
    RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveRGToProgramAndScheduleMappingWithModifiedBy() {

        RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();

        new RequisitionGroupProgramScheduleHandler(requisitionGroupProgramScheduleService).save(requisitionGroupProgramSchedule, USER);

        assertThat(requisitionGroupProgramSchedule.getModifiedBy(), is(USER));
        assertThat(requisitionGroupProgramSchedule.getModifiedDate(), is(notNullValue()));
        verify(requisitionGroupProgramScheduleService).save(requisitionGroupProgramSchedule);
    }

    @Test
    public void shouldMarkRequisitionGroupFieldAsImportable() {
        Field requisitionGroupField = new ModelClass(RequisitionGroupProgramSchedule.class).findImportFieldWithName("RG Code");
        assertNotNull(requisitionGroupField);
        ImportField annotation = requisitionGroupField.getAnnotation(ImportField.class);
        assertTrue(annotation.mandatory());
        assertEquals("code", annotation.nested());
    }

    @Test
    public void shouldMarkProgramFieldAsImportable() {
        Field programField = new ModelClass(RequisitionGroupProgramSchedule.class).findImportFieldWithName("Program");
        assertNotNull(programField);
        ImportField annotation = programField.getAnnotation(ImportField.class);
        assertTrue(annotation.mandatory());
        assertEquals("code", annotation.nested());
    }

    @Test
    public void shouldMarkScheduleFieldAsImportable() {
        Field scheduleField = new ModelClass(RequisitionGroupProgramSchedule.class).findImportFieldWithName("Schedule");
        assertNotNull(scheduleField);
        ImportField annotation = scheduleField.getAnnotation(ImportField.class);
        assertTrue(annotation.mandatory());
        assertEquals("code", annotation.nested());
    }

    @Test
    public void shouldMarkDirectDeliveryFieldAsImportable() {
        Field directDeliveryField = new ModelClass(RequisitionGroupProgramSchedule.class).findImportFieldWithName("Direct Delivery");
        assertNotNull(directDeliveryField);
        ImportField annotation = directDeliveryField.getAnnotation(ImportField.class);
        assertTrue(annotation.mandatory());
        assertEquals("", annotation.nested());
    }

    @Test
    public void shouldMarkDropOffFacilityFieldAsImportable() {
        Field dropOffFacilityField = new ModelClass(RequisitionGroupProgramSchedule.class).findImportFieldWithName("Drop off Facility");
        assertNotNull(dropOffFacilityField);
        ImportField annotation = dropOffFacilityField.getAnnotation(ImportField.class);
        assertFalse(annotation.mandatory());
        assertEquals("code", annotation.nested());
    }

}
