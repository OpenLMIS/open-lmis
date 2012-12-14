package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.rnr.builder.RequisitionGroupBuilder;
import org.openlmis.rnr.domain.RequisitionGroupProgramSchedule;
import org.openlmis.rnr.domain.Schedule;
import org.openlmis.rnr.repository.mapper.RequisitionGroupMapper;
import org.openlmis.rnr.repository.mapper.RequisitionGroupProgramScheduleMapper;
import org.openlmis.rnr.repository.mapper.ScheduleMapper;
import org.springframework.dao.DuplicateKeyException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequisitionGroupProgramScheduleRepositoryTest {

    RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

    @Mock
    RequisitionGroupMapper requisitionGroupMapper;

    @Mock
    ProgramMapper programMapper;

    @Mock
    ScheduleMapper scheduleMapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        requisitionGroupProgramScheduleRepository = new RequisitionGroupProgramScheduleRepository(requisitionGroupProgramScheduleMapper, requisitionGroupMapper, programMapper, scheduleMapper);
        requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
        requisitionGroupProgramSchedule.setRequisitionGroup(make(a(RequisitionGroupBuilder.defaultRequisitionGroup)));
        requisitionGroupProgramSchedule.setProgram(make(a(ProgramBuilder.defaultProgram)));
        requisitionGroupProgramSchedule.setSchedule(new Schedule());
    }

    @Test
    public void shouldGiveErrorIfRequisitionGroupCodeDoesNotExist() throws Exception {

        when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(null);
        when(programMapper.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenReturn(1);
        when(scheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getSchedule().getCode())).thenReturn(1);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Requisition Group Code Does Not Exist");
        requisitionGroupProgramScheduleRepository.insert(requisitionGroupProgramSchedule);

        verify(requisitionGroupMapper).getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode());
        verify(requisitionGroupProgramScheduleMapper, never()).insert(requisitionGroupProgramSchedule);
    }

    @Test
    public void shouldGiveErrorIfProgramCodeDoesNotExist() throws Exception {
        when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(1);
        when(programMapper.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenReturn(null);
        when(scheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getSchedule().getCode())).thenReturn(1);

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Program Code Does Not Exist");
        requisitionGroupProgramScheduleRepository.insert(requisitionGroupProgramSchedule);
    }

    @Test
    public void shouldGiveErrorIfScheduleCodeDoesNotExist() throws Exception {
        when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(1);
        when(programMapper.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenReturn(1);
        when(scheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getSchedule().getCode())).thenReturn(null);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Schedule Code Does Not Exist");

        requisitionGroupProgramScheduleRepository.insert(requisitionGroupProgramSchedule);
    }

    @Test
    public void shouldGiveDuplicateRecordErrorIfDuplicateRGCodeAndProgramCodeFound() throws Exception {
        doThrow(new DuplicateKeyException("")).when(requisitionGroupProgramScheduleMapper).insert(requisitionGroupProgramSchedule);

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Duplicate Requisition Group Code And Program Code Combination found");
        requisitionGroupProgramScheduleRepository.insert(requisitionGroupProgramSchedule);
    }

    @Test
    public void shouldSaveMappingIfAllCorrect() throws Exception {
        when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(1);
        when(programMapper.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenReturn(1);
        when(scheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getSchedule().getCode())).thenReturn(1);

        requisitionGroupProgramScheduleRepository.insert(requisitionGroupProgramSchedule);

        verify(requisitionGroupProgramScheduleMapper).insert(requisitionGroupProgramSchedule);
        verify(requisitionGroupMapper).getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode());
        verify(programMapper).getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode());
        verify(scheduleMapper).getIdForCode(requisitionGroupProgramSchedule.getSchedule().getCode());

        assertThat(requisitionGroupProgramSchedule.getSchedule().getId(), is(1));
        assertThat(requisitionGroupProgramSchedule.getProgram().getId(), is(1));
        assertThat(requisitionGroupProgramSchedule.getRequisitionGroup().getId(), is(1));
    }


}
