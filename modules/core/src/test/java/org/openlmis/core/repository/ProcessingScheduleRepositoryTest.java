package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessingScheduleRepositoryTest {

    @Rule
    public ExpectedException expectedEx = org.junit.rules.ExpectedException.none();

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

    @Test
    public void shouldInsertASchedule() throws Exception {
        ProcessingSchedule processingSchedule = new ProcessingSchedule("testScheduleCode", "testScheduleName");
        repository.save(processingSchedule);
        verify(processingScheduleMapper).insert(processingSchedule);
    }

    @Test
    public void shouldNotSaveScheduleWithoutItsCode() throws Exception {
        ProcessingSchedule processingSchedule = new ProcessingSchedule();
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Schedule can not be saved without its code.");
        repository.save(processingSchedule);
    }

    @Test
    public void shouldNotSaveScheduleWithoutItsName() throws Exception {
        ProcessingSchedule processingSchedule = new ProcessingSchedule();
        processingSchedule.setCode("testCode");
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Schedule can not be saved without its name.");
        repository.save(processingSchedule);
    }
}
