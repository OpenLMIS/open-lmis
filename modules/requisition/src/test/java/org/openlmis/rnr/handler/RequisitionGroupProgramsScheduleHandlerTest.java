package org.openlmis.rnr.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.rnr.domain.RequisitionGroupProgramSchedule;
import org.openlmis.rnr.service.RequisitionGroupProgramScheduleService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupProgramsScheduleHandlerTest {

    public static final String USER = "User";
    @Mock
    RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveRGToProgramAndScheduleMappingWithModifiedBy() throws Exception {

        RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();

        new RequisitionGroupProgramScheduleHandler(requisitionGroupProgramScheduleService).save(requisitionGroupProgramSchedule, USER);

        assertThat(requisitionGroupProgramSchedule.getModifiedBy(), is(USER));
        assertThat(requisitionGroupProgramSchedule.getModifiedDate(), is(notNullValue()));
        verify(requisitionGroupProgramScheduleService).save(requisitionGroupProgramSchedule);
    }
}
