package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessingScheduleRepositoryTest {

    @Mock
    private ProcessingScheduleMapper processingScheduleMapper;

    private ProcessingScheduleRepository repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        repository = new ProcessingScheduleRepository(processingScheduleMapper);
    }

    @Test
    public void shouldGetAll() throws Exception {
        List<ProcessingSchedule> scheduleList = new ArrayList<>();
        scheduleList.add(new ProcessingSchedule());
        when(processingScheduleMapper.getAll()).thenReturn(scheduleList);

        List<ProcessingSchedule> schedules = repository.getAll();

        assertThat(schedules, is(scheduleList));
    }
}
