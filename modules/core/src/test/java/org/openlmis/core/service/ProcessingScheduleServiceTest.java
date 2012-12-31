package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.ProcessingScheduleRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessingScheduleServiceTest {

    @Mock
    private ProcessingScheduleRepository processingScheduleRepository;
    private ProcessingScheduleService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ProcessingScheduleService(processingScheduleRepository);
    }

    @Test
    public void shouldGetAllSchedules() throws Exception {
        List<ProcessingSchedule> processingScheduleList = new ArrayList();
        processingScheduleList.add(new ProcessingSchedule());
        when(processingScheduleRepository.getAll()).thenReturn(processingScheduleList);

        List<ProcessingSchedule> processingSchedules = service.getAll();

        assertThat(processingSchedules, is(processingScheduleList));
    }

    @Test
    public void shouldInsertASchedule() throws Exception {
        ProcessingSchedule processingSchedule = new ProcessingSchedule("testCode", "testName");
        service.save(processingSchedule);
        verify(processingScheduleRepository).save(processingSchedule);
    }
}
